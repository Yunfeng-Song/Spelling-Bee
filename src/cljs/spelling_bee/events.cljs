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
       (< (count value) 4) (-> db
                               (assoc :popup true)
                               (assoc :message "Too short")
                               (assoc  :value ""))


       (not (every? #(some #{%} (conj (get-in db [:game-data :chars :rest]) (first (get-in db [:game-data :chars :main])))) (split value ""))) (-> db
                                                                                                                                                   (assoc :popup true)
                                                                                                                                                   (assoc :message  "Bad letters")
                                                                                                                                                   (assoc :value ""))
       (not (some #{(get-in db [:game-data :chars :main])} (split value ""))) (-> db
                                                                                  (assoc :popup true)
                                                                                  (assoc :message "Missing center letter")
                                                                                  (assoc :value ""))
       (not (some #{value} (map upper-case (get-in db [:game-data :answers]))))  (-> db
                                                                                     (assoc :popup true)
                                                                                     (assoc :message "Not in word list")
                                                                                     (assoc  :value ""))
       (some #{value} (:word-list db))                   (-> db
                                                             (assoc :popup true)
                                                             (assoc :message "Already found")
                                                             (assoc :value ""))
       :else  (let [point (+ (- (count value) 3) (if (every? #(some #{%} (split value "")) (get-in db [:game-data :chars :rest])) 7 0))]

                (-> db
                    (assoc :popup true)
                    (assoc :message (str "Good! +" point))
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

(reg-event-db
 :popup-finished
 (fn [db _]
   (assoc db :popup false)))

