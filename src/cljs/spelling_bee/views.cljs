(ns spelling-bee.views
  (:require
   [re-frame.core :as re-frame :refer [subscribe dispatch]]
   [spelling-bee.subs :as subs]
   [clojure.string :as str :refer [split]]))

; (defn input []
;   [:input {:value @(subscribe [:input-value])
;            :on-change #(dispatch [:change-value (-> % .-target .-value)])
;            :on-key-down #(when (= 13 (.-keyCode %))
;                            (dispatch [:handle-save (-> % .-target .-value)]))}])
;                            
(defn input []
  [:div
   (let [chars (split @(subscribe [:input-value]) "") chars-with-index (zipmap (range (count chars)) chars)]
     (doall (for [[index char] chars-with-index]
              ^{:key (+ index char)} [:span {:class @(subscribe [:validate-char char])} char])))
   [:span {:class "blinking-cursor"} "|"]])

(defn char-buttons []
  (let [main-char @(subscribe [:main-char]) rest-chars @(subscribe [:rest-chars])]
    [:div
     [:button {:style {:background-color "yellow"}
               :on-click #(dispatch [:add-char main-char])} main-char]
     (for [char rest-chars]
       ^{:key char} [:button {:on-click #(dispatch [:add-char char])} char])]))

(defn option-buttons []
  (let [value @(subscribe [:input-value])]
    [:div
     [:button {:on-click #(when (not= "" value)
                            (dispatch [:delete-char]))} "Delete"]
     [:button {:on-click #(dispatch [:shuffle])} "*"]
     [:button {:on-click #(dispatch [:handle-save value])} "Enter"]]))

(defn slider []
  [:h1 "slider"])

(defn word-list []
  (let [list @(subscribe [:word-list]) count (count list)]
    [:div#word-list-container
     [:div  "You have found " count " word" (when (> count 1) "s")]
     (for [item (sort list)]
       ^{:key item} [:div {:class "w3-animate-fading" :style {:margin-left "2%"}} item])]))
;; :<>
;;<div id= "input" contenteditable>I look like an input</div>
(defn main-panel []
  [:div
   [:div {:style {:float "left"}}
    [input]
    [char-buttons]
    [option-buttons]]
   [:div {:style {:float "right"}}
    [slider]
    [word-list]]])
