(ns spelling-bee.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   [clojure.string :as str :refer [split upper-case]]))

;;Layer 2 subscriptions
(reg-sub
 :input-value
 (fn [db _]
   (.log js/console "db" db)
   (get-in db [:game-status :value])))

(reg-sub
 :word-list
 (fn [db _]
   (get-in db [:game-status :word-list])))

(reg-sub
 :main-char
 (fn [db _]
   (get-in db [:game-data :chars :main])))

(reg-sub
 :rest-chars
 (fn [db _]
   (get-in db [:game-data :chars :rest])))

(reg-sub
 :answer-list
 (fn [db _]
   (get-in db [:game-data :answers])))

(reg-sub
 :rankings
 (fn [db _]
   (get-in db [:game-data :rankings])))

(reg-sub
 :current-score
 (fn [db _]
   (get-in db [:game-status :current-score])))

(reg-sub
 :popup
 (fn [db _]
   (get-in db [:game-status :popup])))

(reg-sub
 :message
 (fn [db _]
   (get-in db [:game-status :message])))


;;Layer 3 subscriptions
(reg-sub
 :chars-with-index

 :<- [:input-value]

 (fn [value _]
   (let [chars (split value "")]
     (-> chars
         (count)
         (range)
         (zipmap chars)))))

(reg-sub
 :char-class

 :<- [:main-char]
 :<- [:rest-chars]

 (fn [[main-char rest-chars] [_ char]]
   (cond
     (= char main-char)            "main-char"
     (some #(= char %) rest-chars) ""
     :else                         "invalid-char")))

(reg-sub
 :display-rankings

 :<- [:rankings]

 (fn [rankings _]
   (->> rankings
        (reduce (fn [acc item] (str acc " " (last item) "(" (first item) ")" "\n")) "")
        (str "Rankings: \n\n"))))

(reg-sub
 :rank

 :<- [:rankings]
 :<- [:current-score]

 (fn [[rankings score] _]
   (rankings (last (filter #(>= score %) (keys rankings))))))

(reg-sub
 :margin-value

 :<- [:rankings]
 :<- [:current-score]

 (fn [[rankings score] _]
   (-> (filter #(>= score %) (keys rankings))
       (count)
       (- 1)
       (* 10.4)
       (str "%"))))

(reg-sub
 :dot-class

 :<- [:current-score]

 (fn [score [_ stage]]
   (if (<= (first stage) score)
     "sb-progress-dot completed"
     "sb-progress-dot")))