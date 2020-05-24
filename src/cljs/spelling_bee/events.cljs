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
   (assoc db :value value)))

(reg-event-db
 :handle-save
 (fn [db _]
   (let [value (:value db)]
     (cond
       (< (count value) 4) (do
                             (js/alert "Too short")
                             (assoc db :value ""))


       (not (every? #(some #{%} (conj (get-in db [:game-data :chars :rest]) (first (get-in db [:game-data :chars :main])))) (split value ""))) (do
                                                                                                                                                 (js/alert "Bad letters")
                                                                                                                                                 (assoc db :value ""))
       (not (some #{(get-in db [:game-data :chars :main])} (split value ""))) (do
                                                                                (js/alert "Missing center letter")
                                                                                (assoc db :value ""))
       (not (some #{value} (map upper-case (get-in db [:game-data :answers]))))  (do
                                                                                   (js/alert "Not in word list")
                                                                                   (assoc db :value ""))
       (some #{value} (:word-list db))                   (do
                                                           (js/alert "Already found")
                                                           (assoc db :value ""))
       :else  (let [point (+ (- (count value) 3) (if (every? #(some #{%} (split value "")) (get-in db [:game-data :chars :rest])) 7 0))]
                (js/alert (str "Good! +" point))
                (-> db
                    (update :word-list conj value)
                    (update :current-score + point)
                    (assoc :value "")))))))

(reg-event-db
 :add-char
 (fn [db [_ value]]
   (update db :value + value)))

(reg-event-db
 :delete-char
 (fn [db _]
   (update db :value subs 0 (- (count (:value db)) 1))))

(reg-event-db
 :shuffle
 (fn [db _]
   (update-in db [:game-data :chars :rest] shuffle)))

