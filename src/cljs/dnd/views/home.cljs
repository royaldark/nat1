(ns dnd.views.home
  (:require [dnd.views.header :as v-header]
            [dnd.views.components.roller :as c-roller]
            [re-com.core :as re-com]))

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[v-header/header]
              [c-roller/roller]
              [c-roller/roller]]])
