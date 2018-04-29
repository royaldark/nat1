(ns dnd.events
  (:require [accountant.core :as accountant]
            [dnd.api :as api]
            [dnd.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [re-frame.core :as re-frame]))

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

;; api/get-user

(re-frame/reg-event-db
 ::api/get-user-success
 (fn [db [_ id user]]
   (assoc-in db [:users id] user)))

(re-frame/reg-event-db
 ::api/get-user-failure
 (fn [db [_ id err]]
   (println "Failed to get user" id ">>>" err)
   db))

(api/reg-api-fx
 ::api/get-user
 (fn [{:keys [db] :as cofx} [_ id]]
   {:http-xhrio {:method :get
                 :uri (str "http://localhost:3000/users/"
                           (js/encodeURIComponent id))
                 :on-success [::api/get-user-success id]
                 :on-failure [::api/get-user-failure id]}}))

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
   {:http-xhrio {:method     :post
                 :uri        "http://localhost:3000/users"
                 :params     {:username username
                              :email    email
                              :password password}
                 :on-success [::api/sign-up-success]
                 :on-failure [::api/sign-up-failure]}}))

(re-frame/reg-event-fx
  ::api/sign-up-success
  (fn [cofx [_ user]]
    (accountant/navigate! "/")
    (re-frame/dispatch [:auth/set-active-user user])
    {}))

(re-frame/reg-event-db
 ::api/sign-up-failure
 (fn [db [_ result]]
   (println result)
   (println (type result))
   #_(assoc db :api-result result)
   db))
