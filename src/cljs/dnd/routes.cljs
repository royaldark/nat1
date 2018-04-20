(ns dnd.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:require [accountant.core :as accountant]
            [dnd.events :as events]
            [goog.events :as gevents]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]
            [secretary.core :as secretary])
  (:import goog.Uri
           goog.history.Html5History))

(defn hook-browser-navigation! []
  (accountant/configure-navigation!
   {:nav-handler (fn [path]
                   (println "nav handler:" path)
                   (secretary/dispatch! path))
    :path-exists? (fn [path]
                    (println "path exists?" path)
                    (secretary/locate-route path))}))

(defn app-routes []
  (defroute "/" []
    (re-frame/dispatch [::events/set-active-panel :home-panel]))

  (defroute "/login" []
    (re-frame/dispatch [::events/set-active-panel :login-panel]))

  (defroute "/signup" []
    (re-frame/dispatch [::events/set-active-panel :signup-panel]))

  (println "hooking browser nav")

  ;; --------------------
  (hook-browser-navigation!)

  (accountant/dispatch-current!))
