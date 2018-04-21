(ns dnd.views.login
  (:require [reagent.core :as reagent]
            [re-com.core :as re-com]
            [dnd.views.header :as v-header]))

(defn login-form []
  (let [username (reagent/atom "joe")
        password (reagent/atom "")]
    (fn []
      [re-com/v-box
       :children ["Username:"
                  [re-com/input-text
                   :model username
                   :on-change #(reset! username %)]
                  "Password:"
                  [re-com/input-text
                   :input-type :password
                   :model password
                   :on-change #(reset! password %)]
                  [re-com/button
                   :label "Login"
                   :on-click #(do nil)]]])))

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