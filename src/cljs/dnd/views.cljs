(ns dnd.views
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [dnd.api :as api]
            [dnd.subs :as subs]
            [dnd.views.home :as v-home]
            [dnd.views.login :as v-login]
            [dnd.views.signup :as v-signup]))

;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel   [v-home/home-panel]
    :login-panel  [v-login/login-panel]
    :signup-panel [v-signup/signup-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [re-com/v-box
     :height "100%"
     :width "100%"
     :justify :center
     :children [[panels @active-panel]]]))
