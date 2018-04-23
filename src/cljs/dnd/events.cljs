(ns dnd.events
  (:require [ajax.core :as ajax]
            [ajax.protocols :refer [Interceptor]]
            [akiroz.re-frame.storage :as storage]
            [re-frame.core :as re-frame]
            [dnd.api :as api]
            [dnd.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(storage/reg-co-fx! :nat1 {:fx   :store
                           :cofx :store})


(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :active-panel active-panel)))

;; api/roll-die

(re-frame/reg-event-fx
 ::api/roll-die
 (fn [{:keys [db] :as cofx}
      [_ id number sides modifier]]
   {:db (assoc-in db [::api/roll-die-results id] 0)
    :http-xhrio {:method          :post
                 :uri             "http://localhost:3000/roll"
                 :params          {:number   number
                                   :sides    sides
                                   :modifier modifier}
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::api/roll-die-success id]
                 :on-failure      [::api/roll-die-failure id]}}))

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
  (fn [{:keys [store] :as cofx} [_ token]]
    {:store (assoc store :auth-token token)}))

(defrecord JwtInterceptor []
  Interceptor
  (-process-request [_ request]
    request)
  (-process-response [_ response]
    (when-let [token (.getResponseHeader response "X-Jwt")]
      (println "Storing JWT token" token)
      (re-frame/dispatch [:auth/set-token token]))
    response))

(re-frame/reg-event-fx
 ::api/sign-up
 (fn [{:keys [db] :as cofx}
      [_ username email password]]
   {#_#_:db (assoc db :signup-result)
    :http-xhrio {:method          :post
                 :uri             "http://localhost:3000/signup"
                 :params          {:username username
                                   :email    email
                                   :password password}
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :interceptors    [(JwtInterceptor.)]
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
