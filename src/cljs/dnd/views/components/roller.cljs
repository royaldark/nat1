(ns dnd.views.components.roller
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [dnd.api :as api]
            [dnd.subs :as subs]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

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
