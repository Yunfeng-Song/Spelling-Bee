(ns spelling-bee.events
  (:require
   [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx ->interceptor]]
   [spelling-bee.db :as db]
   [clojure.string :refer [upper-case split join]]))

(reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-db
 :change-value
 (fn [db [_ value]]
   (assoc-in db [:game-status :value] value)))

(defn too-short?
  [value]
  (< (count value) 4))

(defn bad-letters?
  [value letters]
  (not (every? #(some #{%} letters) (split value ""))))

(defn missing-center-letter?
  [value center-letter]
  (not (some #{center-letter} (split value ""))))

(defn not-in-word-list?
  [value word-list]
  (not (some #{value} (map upper-case word-list))))

(defn already-found?
  [value word-list]
  (some #{value} word-list))

(defn pangram?
  [value word-list]
  (every? #(some #{%} (split value "")) word-list))

(defn calculate-point
  [value pangram?]
  (if (> 4 (count value))
    0
    (+ (- (count value) 3) (if pangram? 7 0))))

(def reset-value
  (->interceptor
   :id :reset-value
   :after (fn [context]
            (let [reset-fn (fn [db] (-> db
                                        (assoc-in [:game-status :popup] true)
                                        (assoc-in [:game-status :value] "")))]
              (update-in context [:effects :db] reset-fn)))))

(reg-event-fx
 :handle-save
 [reset-value]
 (fn [{:keys [db]} _]
   {:dispatch-later [{:ms 2000 :dispatch [:popup-finished]}]
    :db (let [value (get-in db [:game-status :value])
              main-char (get-in db [:game-data :chars :main])
              rest-chars (get-in db [:game-data :chars :rest])
              answers (get-in db [:game-data :answers])
              word-list (get-in db [:game-status :word-list])]
          (cond
            (too-short? value)                               (assoc-in  db [:game-status :message] "Too short")
            (bad-letters? value (conj rest-chars main-char)) (assoc-in  db [:game-status :message] "Bad letters")
            (missing-center-letter? value main-char)         (assoc-in  db [:game-status :message] "Missing center letter")
            (not-in-word-list? value answers)                (assoc-in  db [:game-status :message] "Not in word list")
            (already-found? value word-list)                 (assoc-in  db [:game-status :message] "Already found")
            :else                                            (let [p? (pangram? value rest-chars)
                                                                   point (calculate-point value p?)
                                                                   message (if p? "Pangram" "Good")]

                                                               (-> db
                                                                   (assoc-in  [:game-status :message] (str message "! +" point))
                                                                   (update-in [:game-status :word-list] conj value)
                                                                   (update-in [:game-status :current-score] + point)))))}))

(reg-event-db
 :add-char
 (fn [db [_ value]]
   (update-in db [:game-status :value] + value)))

(reg-event-db
 :delete-char
 (fn [db _]
   (update-in db [:game-status :value] #(join "" (drop-last %)))))

(reg-event-db
 :shuffle
 (fn [db _]
   (update-in db [:game-data :chars :rest] shuffle)))

(reg-event-db
 :popup-finished
 (fn [db _]
   (-> db
       (assoc-in [:game-status :popup] false)
       (assoc-in [:game-status :message] ""))))

