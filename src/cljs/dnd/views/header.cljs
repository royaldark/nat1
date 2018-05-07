(ns dnd.views.header
  (:require [dnd.api :as api]
            [dnd.subs :as subs]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

(defn ^:private logged-in-panel
  [user]
  [(str (:username user))
   [re-com/hyperlink-href
    :label "Log Out"
    :href "/logout"]])

(defn ^:private logged-out-panel
  []
  [[re-com/hyperlink-href
    :label "Sign Up"
    :href "/signup"]
   [re-com/hyperlink-href
    :label "Log In"
    :href "/login"]])

(defn ^:private campaigns-panel
  [campaigns]
  [(str "Campaigns (" (count @campaigns) ")")])

(defn header []
  (let [version   (re-frame/subscribe [::subs/version])
        user      (re-frame/subscribe [::subs/active-user])
        campaigns (re-frame/subscribe [::subs/campaigns])]
    [re-com/h-box
     :gap "1rem"
     :children (concat [[re-com/box
                          :child [:strong (str "nat1 v" @version)]
                          :size "auto"]]
                       (campaigns-panel campaigns)
                       (if-let [user @user]
                         (logged-in-panel user)
                         (logged-out-panel)))
     :width "100%"
     :class "site-header"]))
