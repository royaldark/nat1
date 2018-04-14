(ns dnd.views
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [dnd.api :as api]
            [dnd.subs :as subs]))


;; home

(defn header []
  (let [version (re-frame/subscribe [::subs/version])]
    [re-com/h-box
     :children [(str "nat 1 v" @version)]
     :width "100%"
     :class "site-header"]))

(defn roller []
  (let [id       (random-uuid)
        sides    (reagent/atom "20")
        modifier (reagent/atom "0")
        result   (re-frame/subscribe [::subs/die-roll id])]
    (fn []
      [re-com/h-box
       :children [[re-com/input-text
                   :width "3.5rem"
                   :model sides
                   :on-change #(reset! sides %)]
                  "+"
                  [re-com/input-text
                   :width "3.5rem"
                   :model modifier
                   :on-change #(reset! modifier %)]
                  "="
                  (or @result "__")
                  [re-com/button
                   :label "Roll"
                   :on-click #(re-frame/dispatch [::api/roll-die id @sides @modifier])]]])))

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[header]
              [roller]
              [roller]]])


;; about

 (defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title] [link-to-home-page]]])


;; main

 (defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [re-com/v-box
     :height "100%"
     :children [[panels @active-panel]]]))
