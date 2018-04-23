(ns dnd.events
  (:require [re-frame.core :as re-frame]
            [dnd.api :as api]
            [dnd.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :active-panel active-panel)))

;; api/roll-die

(api/reg-api-fx
 ::api/roll-die
 (fn [{:keys [db] :as cofx}
      [_ id number sides modifier]]
   {:db         (assoc-in db [::api/roll-die-results id] 0)
    :http-xhrio {:method     :post
                 :uri        "http://localhost:3000/roll"
                 :params     {:number   number
                              :sides    sides
                              :modifier modifier}
                 :on-success [::api/roll-die-success id]
                 :on-failure [::api/roll-die-failure id]}}))

(re-frame/reg-event-db
 ::api/roll-die-success
 (fn [db [_ id result]]
   (assoc-in db [::api/roll-die-results id] result)))

(re-frame/reg-event-db
 ::api/roll-die-failure
 (fn [db [_ id result]]
   (println result)
   #_(assoc db :api-result result)
   db))

;; api/sign-up

(re-frame/reg-event-fx
  :auth/set-token
  [(re-frame/inject-cofx :store)]
  (fn [{:keys [store] :as cofx} [_ token]]
    {:store (assoc store :auth-token token)}))

(api/reg-api-fx
 ::api/sign-up
 (fn [{:keys [db store] :as cofx}
      [_ username email password]]
   {#_#_:db (assoc db :signup-result)
    :http-xhrio {:method          :post
                 :uri             "http://localhost:3000/signup"
                 :params          {:username username
                                   :email    email
                                   :password password}
                 :on-success      [::api/sign-up-success]
                 :on-failure      [::api/sign-up-failure]}}))

(re-frame/reg-event-db
 ::api/sign-up-success
 (fn [db [_ result]]
   (assoc db ::api/sign-up-results result)))

(re-frame/reg-event-db
 ::api/sign-up-failure
 (fn [db [_ result]]
   (println result)
   (println (type result))
   #_(assoc db :api-result result)
   db))
