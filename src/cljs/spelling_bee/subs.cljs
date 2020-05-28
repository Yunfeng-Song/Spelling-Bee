(ns spelling-bee.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]))

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