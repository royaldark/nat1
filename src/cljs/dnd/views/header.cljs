(ns dnd.views.header
  (:require [dnd.api :as api]
            [dnd.subs :as subs]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

(defn header []
  (let [version (re-frame/subscribe [::subs/version])
        user    (re-frame/subscribe [::subs/active-user])]
    [re-com/h-box
     :gap "1rem"
     :children (concat [[re-com/box
                          :child [:strong (str "nat1 v" @version)]
                          :size "auto"]]
                       (if @user
                         [(str (:username @user))
                          [re-com/hyperlink-href
                          :label "Log Out"
                          :href "/logout"]]
                         [[re-com/hyperlink-href
                           :label "Sign Up"
                           :href "/signup"]
                         [re-com/hyperlink-href
                          :label "Log In"
                          :href "/login"]]))
     :width "100%"
     :class "site-header"]))
