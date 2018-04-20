(ns dnd.views.header
  (:require [dnd.subs :as subs]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

(defn header []
  (let [version (re-frame/subscribe [::subs/version])]
    [re-com/h-box
     :gap "1rem"
     :children [[re-com/box
                 :child (str "nat1 v" @version)
                 :size "auto"]
                [re-com/hyperlink-href
                 :label "Sign Up"
                 :href "/signup"]
                [re-com/hyperlink-href
                 :label "Log In"
                 :href "/login"]]
     :width "100%"
     :class "site-header"]))