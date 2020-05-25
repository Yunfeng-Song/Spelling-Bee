(ns spelling-bee.events
  (:require
   [re-frame.core :as re-frame :refer [reg-event-db]]
   [spelling-bee.db :as db]
   [clojure.string :refer [upper-case split]]))

(reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-db
 :change-value
 (fn [db [_ value]]
   (assoc-in db [:game-status :value] value)))

(reg-event-db
 :handle-save
 (fn [db _]
   (let [value (get-in db [:game-status :value])]
     (cond
       (< (count value) 4) (-> db
                               (assoc-in [:game-status :popup] true)
                               (assoc-in [:game-status :message] "Too short")
                               (assoc-in [:game-status :value] ""))


       (not (every? #(some #{%} (conj (get-in db [:game-data :chars :rest]) (first (get-in db [:game-data :chars :main])))) (split value ""))) (-> db
                                                                                                                                                   (assoc-in [:game-status :popup] true)
                                                                                                                                                   (assoc-in [:game-status :message]  "Bad letters")
                                                                                                                                                   (assoc-in [:game-status :value] ""))
       (not (some #{(get-in db [:game-data :chars :main])} (split value ""))) (-> db
                                                                                  (assoc-in [:game-status :popup] true)
                                                                                  (assoc-in [:game-status :message] "Missing center letter")
                                                                                  (assoc-in [:game-status :value] ""))
       (not (some #{value} (map upper-case (get-in db [:game-data :answers]))))  (-> db
                                                                                     (assoc-in [:game-status :popup] true)
                                                                                     (assoc-in [:game-status :message] "Not in word list")
                                                                                     (assoc-in  [:game-status :value] ""))
       (some #{value} (get-in db [:game-status :word-list]))                   (-> db
                                                                                   (assoc-in [:game-status :popup] true)
                                                                                   (assoc-in  [:game-status :message] "Already found")
                                                                                   (assoc-in [:game-status :value] ""))
       :else  (let [point (+ (- (count value) 3) (if (every? #(some #{%} (split value "")) (get-in db [:game-data :chars :rest])) 7 0))]

                (-> db
                    (assoc-in [:game-status :popup] true)
                    (assoc-in [:game-status :message] (str "Good! +" point))
                    (update-in [:game-status :word-list] conj value)
                    (update-in [:game-status :current-score] + point)
                    (assoc-in [:game-status :value] "")))))))

(reg-event-db
 :add-char
 (fn [db [_ value]]
   (update-in db [:game-status :value] + value)))

(reg-event-db
 :delete-char
 (fn [db _]
   (update-in db [:game-status :value] subs 0 (- (count (get-in db [:game-status :value])) 1))))

(reg-event-db
 :shuffle
 (fn [db _]
   (update-in db [:game-data :chars :rest] shuffle)))

(reg-event-db
 :popup-finished
 (fn [db _]
   (assoc-in db [:game-status :popup] false)))

