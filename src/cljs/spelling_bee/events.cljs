(ns spelling-bee.events
  (:require
   [re-frame.core :as re-frame :refer [reg-event-db]]
   [spelling-bee.db :as db]
   [clojure.string :refer [upper-case]]))

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
 (fn [db [_ value]]
   (cond
     (some #{value} (:word-list db))                   (do
                                                         (js/alert "Already found") ;;******************************
                                                         (assoc db :value ""))
     (some #{value} (map upper-case (get-in db [:game-data :answers]))) (-> db
                                                                            (update :word-list conj value)
                                                                            (assoc :value ""))
     :else                                             (do
                                                         (js/alert "Bad letters") ;;******************************
                                                         (assoc db :value "")))))

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

