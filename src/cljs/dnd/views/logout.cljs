(ns dnd.views.logout
  (:require [re-com.core :as re-com]
            [re-frame.core :as re-frame]
            [dnd.views.header :as v-header]))

(defn logout-panel []
  (re-frame/dispatch [:auth/clear-token])
  (fn []
    [re-com/v-box
     :gap "1em"
     :align :center
     :children [[v-header/header]
                [re-com/title
                 :label "Logging out..."
                 :level :level1]]]))
