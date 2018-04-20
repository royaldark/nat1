(ns dnd.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.Uri
           goog.history.Html5History)
  (:require [secretary.core :as secretary]
            [goog.events :as gevents]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]
            [dnd.events :as events]))

(defn hook-browser-navigation! []
  (let [history (doto (Html5History.)
                  (gevents/listen
                   EventType/NAVIGATE
                   (fn [event]
                     (secretary/dispatch! (.-token event))))
                  (.setEnabled true)
                  (.setPathPrefix "")
                  (.setUseFragment false))]
  
    (gevents/listen js/document "click"
                    (fn [e]
                      (let [path  (.getPath (.parse Uri (.-href (.-target e))))
                            title (.-title (.-target e))]
                        (when (not-empty path)
                          (. e preventDefault)
                          (. history (setToken path title))))))))

(defn app-routes []
  (defroute "/" []
    (re-frame/dispatch [::events/set-active-panel :home-panel]))

  (defroute "/login" []
    (re-frame/dispatch [::events/set-active-panel :login-panel]))

  ;; --------------------
  (hook-browser-navigation!))
