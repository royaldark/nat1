(ns dnd.views.login
  (:require [reagent.core :as reagent]
            [re-com.core :as re-com]
            [re-frame.core :as re-frame]
            [dnd.api :as api]
            [dnd.util :as util]
            [dnd.views.header :as v-header]))

(defn login-form []
  (let [username (reagent/atom "")
        password (reagent/atom "")]
    (fn []
      [re-com/v-box
       :children [[re-com/input-text
                   :placeholder "Username"
                   :model username
                   :on-change #(reset! username %)]
                  (util/nbsp)
                  [re-com/input-text
                   :input-type :password
                   :placeholder "Password"
                   :model password
                   :on-change #(reset! password %)]
                  (util/nbsp)
                  [re-com/button
                   :label "Login"
                   :on-click #(re-frame/dispatch [::api/log-in @username @password])]]])))

(defn login-panel []
  [re-com/v-box
   :gap "1em"
   :align :center
   :children [[v-header/header]
              [re-com/title
               :label "Login"
               :level :level1]
              [login-form]
              [re-com/hyperlink-href
               :label "Home"
               :href "/"]
              [re-com/hyperlink-href
               :label "Sign Up"
               :href "/signup"]]])
