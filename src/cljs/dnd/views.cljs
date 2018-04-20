(ns dnd.views
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [dnd.api :as api]
            [dnd.subs :as subs]))


;; home

(defn header []
  (let [version (re-frame/subscribe [::subs/version])]
    [re-com/h-box
     :children [[re-com/box
                 :child (str "nat1 v" @version)
                 :size "auto"]
                [re-com/hyperlink-href
                 :label "Log In"
                 :href "/login"]]
     :width "100%"
     :class "site-header"]))

(defn roller []
  (let [id         (random-uuid)
        number     (reagent/atom "1")
        sides      (reagent/atom "20")
        modifier   (reagent/atom "0")
        digit-re   #"^[0-9]+$"
        result     (re-frame/subscribe [::subs/die-roll id])
        select-all (handler-fn
                     (.select (.-target event)))
        text-style {:padding "0"
                    :text-align :center}]
    (fn []
      [re-com/h-box
       :align :end
       :gap "0.25rem"
       :padding "0 0 0 1rem"
       :children [[re-com/input-text
                   :width "1.5rem"
                   :model number
                   :validation-regex digit-re
                   :on-change #(reset! number %)
                   :attr {:on-focus select-all}
                   :style text-style]
                  "d"
                  [re-com/input-text
                   :width "2.5rem"
                   :model sides
                   :validation-regex digit-re
                   :on-change #(reset! sides %)
                   :attr {:on-focus select-all}
                   :style text-style]
                  "+"
                  [re-com/input-text
                   :width "2rem"
                   :model modifier
                   :validation-regex digit-re
                   :on-change #(reset! modifier %)
                   :attr {:on-focus select-all}
                   :style text-style]
                  "="
                  [re-com/box
                   :child [:div
                           {:style {:width "100%"}}
                           (if-let [num (:result @result)]
                             (str num)
                             "\u00A0")]
                   :style {:box-shadow "0 2px 0 0 blue"
                           :min-width "2rem"
                           :text-align "center"}]
                  [re-com/button
                   :label "Roll"
                   :on-click #(re-frame/dispatch [::api/roll-die id
                                                  (js/parseInt @number)
                                                  (js/parseInt @sides)
                                                  (js/parseInt @modifier)])
                   :style {:margin-left "1rem"}]]])))

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
   :children [[re-com/title
               :label "Login"
               :level :level1]
              [login-form]
              [re-com/hyperlink-href
                 :label "Home"
                 :href "/"]]])

;; main

 (defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :login-panel [login-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [re-com/v-box
     :height "100%"
     :width "100%"
     :justify :center
     :children [[panels @active-panel]]]))
