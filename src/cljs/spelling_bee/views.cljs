(ns spelling-bee.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame :refer [subscribe dispatch]]
   [spelling-bee.subs :as subs]
   [clojure.string :as str :refer [split upper-case]]
   ["react-flip-move" :as FlipMove]))


(defn popup []
  (let [success @(subscribe [:popup]) message @(subscribe [:message])]
    [:> FlipMove {:enterAnimation "fade" :leaveAnimation "fade" :duration "1000"}
     (when success
       (dispatch [:popup-finished])
       [:div message])]))


(defn input []
  (let [value  @(subscribe [:input-value])]
    [:div {:tab-Index -1 :on-key-down #(cond
                                         (and (<= 65 (.-keyCode %)) (>= 90 (.-keyCode %))) (dispatch [:add-char (upper-case (.-key %))])
                                         (= 32 (.-keyCode %)) (dispatch [:shuffle])
                                         (= 8 (.-keyCode %)) (when (not= "" value)
                                                               (dispatch [:delete-char]))
                                         (= 13 (.-keyCode %)) (when (not= "" value)
                                                                (dispatch [:handle-save])))}
     (let [chars (split value "") chars-with-index (zipmap (range (count chars)) chars)]
       (doall (for [[index char] chars-with-index]
                ^{:key (+ index char)} [:span {:class @(subscribe [:validate-char char])} char])))
     [:span {:class "blinking-cursor"} "|"]]))


(defn char-buttons []
  (let [main-char @(subscribe [:main-char]) rest-chars @(subscribe [:rest-chars])]
    [:div
     [:button#main-button {:on-click #(dispatch [:add-char main-char])} main-char]
     [:> FlipMove {}
      (for [char rest-chars]
        ^{:key char} [:button {:on-click #(dispatch [:add-char char])} char])]]))


(defn option-buttons []
  (let [value @(subscribe [:input-value])]
    [:div
     [:button {:on-click #(when (not= "" value)
                            (dispatch [:delete-char]))} "Delete"]
     [:button {:on-click #(dispatch [:shuffle])} "*"]
     [:button {:on-click #(when (not= "" value)
                            (dispatch [:handle-save]))} "Enter"]]))


(defn slider []
  (let [rankings @(subscribe [:rankings])]
    [:div#slider-container {:on-click #(js/alert (str "Rankings: \n\n" (reduce (fn [acc item] (str acc " " (last item) "(" (first item) ")" "\n")) "" rankings)))}
     (let [current-score @(subscribe [:current-score]) rank (rankings (last (filter #(>= current-score %) (keys rankings))))]
       [:<> [:h4 rank]
        [:div#sb-progress-line
         [:div {:class "demo"}
          (for [stage rankings]
            ^{:key (last stage)} [:span {:class (if (<= (first stage) current-score) "sb-progress-dot completed" "sb-progress-dot")}])]
         [:div#sb-progress-marker {:style {:margin-left (str (* 10.4 (- (count (filter #(>= current-score %) (keys rankings))) 1)) "%")}} current-score]]])]))


(defn word-list []
  (let [list @(subscribe [:word-list]) count (count list)]
    [:div#word-list-container
     [:div  "You have found " count " word" (when (> count 1) "s")]
     (for [item (sort list)]
       ^{:key item} [:div#word-item item])]))


(defn main-panel []
  [:div
   [:div#left-container
    [input]
    [char-buttons]
    [option-buttons]
    [popup]]
   [:div#right-container
    [slider]
    [word-list]]])
