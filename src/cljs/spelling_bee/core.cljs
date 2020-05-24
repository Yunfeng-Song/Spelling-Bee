(ns spelling-bee.core
  (:require
   [reagent.core :as reagent]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame :refer [dispatch dispatch-sync]]
   [spelling-bee.events :as events]
   [spelling-bee.views :as views]
   [spelling-bee.config :as config]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root)
  #_(.addEventListener js/document "keydown" #(cond
                                                (and (<= 65 (.-keyCode %)) (>= 90 (.-keyCode %))) (dispatch [:add-char (upper-case (.-key %))])
                                                (= 32 (.-keyCode %)) (dispatch [:shuffle])
                                                (= 8 (.-keyCode %)) (dispatch [:delete-char])
                                                (= 13 (.-keyCode %)) (dispatch [:handle-save]))))
