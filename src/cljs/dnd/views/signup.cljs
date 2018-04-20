(ns dnd.views.signup
  (:require [dnd.api :as api]
            [dnd.util :as util]
            [dnd.views.header :as v-header]
            [reagent.core :as reagent]
            [re-com.core :as re-com]
            [re-frame.core :as re-frame]))

;; Taken from https://github.com/reagent-project/reagent-utils/blob/93668d46134c1e4697b0c097dcf0faccd6722900/src/reagent/validation.cljs#L46
(def email-regex
  #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(defn handle-email-change
  [atom valid-atom new-val]
  (reset! valid-atom (if (re-matches email-regex new-val)
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

