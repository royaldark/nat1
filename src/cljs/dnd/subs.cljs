(ns dnd.subs
  (:require [dnd.api :as api]
            [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::version
 (fn [db]
   (:version db)))

(re-frame/reg-sub
  ::active-user
  (fn [db]
    (::api/active-user db)))

(re-frame/reg-sub
  ::die-roll-results
  (fn [db]
    (::api/roll-die-results db)))

(re-frame/reg-sub
 ::die-roll
 :<- [::die-roll-results]
 (fn [die-rolls [_ id]]
   (get die-rolls id)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::campaigns
 (fn [db _]
   (:campaigns db)))
