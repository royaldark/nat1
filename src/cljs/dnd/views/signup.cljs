(ns dnd.views.signup
  (:require [dnd.api :as api]
            [dnd.domain.user :as user]
            [dnd.util :as util]
            [dnd.views.header :as v-header]
            [reagent.core :as reagent]
            [re-com.core :as re-com]
            [re-frame.core :as re-frame]))

(defn handle-email-change
  [atom valid-atom new-val]
  (reset! valid-atom (if (user/valid-email? new-val)
                       ::valid
                       ::invalid))
  (reset! atom new-val))

(defn signup-form []
  (let [username         (reagent/atom "")
        email            (reagent/atom "")
        email-v          (reagent/atom ::untouched)
        password         (reagent/atom "")
        password-confirm (reagent/atom "")]
    (fn []
      [re-com/v-box
       :children [[re-com/input-text
                   :placeholder "Username"
                   :model username
                   :on-change #(reset! username %)]
                  (util/nbsp)
                  [re-com/input-text
                   :placeholder "Email"
                   :model email
                   :on-change (partial handle-email-change email email-v)
                   :class (case @email-v
                            ::valid "valid"
                            ::invalid "invalid"
                            "")]
                  (util/nbsp)
                  [re-com/input-text
                   :input-type :password
                   :placeholder "Password"
                   :model password
                   :on-change #(reset! password %)]
                  (util/nbsp)
                  [re-com/input-text
                   :input-type :password
                   :placeholder "Confirm Password"
                   :model password-confirm
                   :on-change #(reset! password-confirm %)]
                  (util/nbsp)
                  [re-com/button
                   :label "Sign Up"
                   :on-click #(re-frame/dispatch [::api/sign-up
                                                  @username @email @password])]]])))

(defn signup-panel []
  [re-com/v-box
   :gap "1em"
   :align :center
   :children [[v-header/header]
              [re-com/title
               :label "Sign Up"
               :level :level1]
              [signup-form]
              [re-com/hyperlink-href
               :label "Home"
               :href "/"]
              [re-com/hyperlink-href
               :label "Login"
               :href "/login"]]])

