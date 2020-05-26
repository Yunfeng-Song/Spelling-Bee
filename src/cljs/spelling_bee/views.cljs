(ns spelling-bee.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame :refer [subscribe dispatch]]
   [spelling-bee.subs :as subs]
   [clojure.string :as str :refer [split upper-case]]
   ["react-flip-move" :as FlipMove]))


(defn popup []
  (let [success @(subscribe [:popup])
        message @(subscribe [:message])]
    [:> FlipMove {:enterAnimation "fade" :leaveAnimation "fade" :duration "1000"}
     (when success
       (dispatch [:popup-finished])
       [:div message])]))


(defn get-char-class
  [char main-char rest-chars]
  (cond
    (= char main-char) "main-char"
    (some #(= char %) rest-chars) ""
    :else "invalid-char"))

(defn chars-with-index-map
  [chars]
  (-> chars
      (count)
      (range)
      (zipmap chars)))

(defn input []
  (let [value  @(subscribe [:input-value])
        chars (split value "")
        chars-with-index (chars-with-index-map chars)
        main-char @(subscribe [:main-char])
        rest-chars @(subscribe [:rest-chars])]
    [:div {:tab-Index -1 :on-key-down #((let [key (.-key %)]
                                          (cond
                                            (re-matches #"[a-z]" key) (dispatch [:add-char (upper-case key)])
                                            (= " " key)               (dispatch [:shuffle])
                                            (= "Backspace" key)       (when (not= "" value)
                                                                        (dispatch [:delete-char]))
                                            (= "Enter" key)           (when (not= "" value)
                                                                        (dispatch [:handle-save])))))}
     (doall (for [[index char] chars-with-index]
              ^{:key (+ index char)} [:span {:class (get-char-class char main-char rest-chars)} char]))
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
     [:button {:on-click #(when (not= "" value)
                            (dispatch [:delete-char]))} "Delete"]
     [:button {:on-click #(dispatch [:shuffle])} "*"]
     [:button {:on-click #(when (not= "" value)
                            (dispatch [:handle-save]))} "Enter"]]))


(defn get-rank-from-score
  [score rankings]
  (rankings (last (filter #(>= score %) (keys rankings)))))

(defn rankings-formater
  [rankings]
  (->> rankings
       (reduce (fn [acc item] (str acc " " (last item) "(" (first item) ")" "\n")) "")
       (str "Rankings: \n\n")))

(defn margin-value
  [score rankings]
  (-> (filter #(>= score %) (keys rankings))
      (count)
      (- 1)
      (* 10.4)
      (str "%")))

(defn slider []
  (let [rankings @(subscribe [:rankings])
        current-score @(subscribe [:current-score])
        rank (get-rank-from-score current-score rankings)]
    [:div#slider-container {:on-click #(js/alert (rankings-formater rankings))}
     [:<> [:h4 rank]
      [:div#sb-progress-line
       [:div
        (for [stage rankings]
          ^{:key (last stage)} [:span {:class (if (<= (first stage) current-score) "sb-progress-dot completed" "sb-progress-dot")}])]
       [:div#sb-progress-marker {:style {:margin-left (margin-value current-score rankings)}} current-score]]]]))


(defn word-list []
  (let [list @(subscribe [:word-list])
        count (count list)]
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
    [popup]
    [:button {:on-click #(dispatch [:test])} "test"]]
   [:div#right-container
    [slider]
    [word-list]]])
