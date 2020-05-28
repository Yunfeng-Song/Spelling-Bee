(ns spelling-bee.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame :refer [subscribe dispatch]]
   [clojure.string :as str :refer [upper-case]]
   ["react-flip-move" :as FlipMove]))


(defn popup
  [success? message]
  [:> FlipMove {:enterAnimation "fade" :leaveAnimation "fade" :duration "1000"}
   (when success?
     [:div message])])


(defn input []
  (let [value  @(subscribe [:input-value])]
    [:div {:tab-Index -1 :on-key-down #(let [key (.-key %)]
                                         (cond
                                           (re-matches #"[a-z]" key) (dispatch [:add-char (upper-case key)])
                                           (= " " key)               (dispatch [:shuffle])
                                           (= "Backspace" key)       (and (not= "" value) (dispatch [:delete-char]))
                                           (= "Enter" key)           (and (not= "" value) (dispatch [:handle-save]))))}
     (doall (for [[index char] @(subscribe [:chars-with-index])]
              ^{:key (+ index char)} [:span {:class @(subscribe [:char-class char])} char]))
     [:span {:class "blinking-cursor"} "|"]]))


(defn char-buttons []
  (let [main-char @(subscribe [:main-char])
        rest-chars @(subscribe [:rest-chars])]
    [:div
     [:button#main-button {:on-click #(dispatch [:add-char main-char])} main-char]
     [:> FlipMove {}
      (for [char rest-chars]
        ^{:key char} [:button {:on-click #(dispatch [:add-char char])} char])]]))


(defn option-buttons []
  (let [value @(subscribe [:input-value])]
    [:div
     [:button {:on-click #(and (not= "" value) (dispatch [:delete-char]))} "Delete"]
     [:button {:on-click #(dispatch [:shuffle])} "*"]
     [:button {:on-click #(and (not= "" value) (dispatch [:handle-save]))} "Enter"]]))


(defn slider []
  [:div#slider-container {:on-click #(js/alert @(subscribe [:display-rankings]))}
   [:<> [:h4 @(subscribe [:rank])]
    [:div#sb-progress-line
     [:div
      (for [stage @(subscribe [:rankings])]
        ^{:key (last stage)} [:span {:class @(subscribe [:dot-class stage])}])]
     [:div#sb-progress-marker {:style {:margin-left @(subscribe [:margin-value])}} @(subscribe [:current-score])]]]])


(defn word-list [list]
  (let [count (count list)]
    [:div#word-list-container
     [:div  "You have found " count " word" (and (> count 1) "s")]
     (for [item (sort list)]
       ^{:key item} [:div#word-item item])]))


(defn main-panel []
  [:div
   [:div#left-container
    [input]
    [char-buttons]
    [option-buttons]
    [popup @(subscribe [:popup]) @(subscribe [:message])]]
   [:div#right-container
    [slider]
    [word-list @(subscribe [:word-list])]]])
