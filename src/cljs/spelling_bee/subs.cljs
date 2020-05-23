(ns spelling-bee.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(reg-sub
 :input-value
 (fn [db _]
   (println "db" db)
   (:value db)))

(reg-sub
 :word-list
 (fn [db _]
   (:word-list db)))

(reg-sub
 :main-char
 (fn [db _]
   (get-in db [:game-data :chars :main])))

(reg-sub
 :rest-chars
 (fn [db _]
   (get-in db [:game-data :chars :rest])))

(reg-sub
 :validate-char
 (fn [db [_ value]]
   (cond
     (= value (get-in db [:game-data :chars :main])) "main-char"
     (some #(= value %) (get-in db [:game-data :chars :rest])) ""
     :else "invalid-char")))

(reg-sub
 :answer-list
 (fn [db _]
   (get-in db [:game-data :answers])))