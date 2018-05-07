(ns dnd.api
  (:require [ajax.core :as ajax]
            [ajax.protocols :refer [Interceptor]]
            [akiroz.re-frame.storage :as storage]
            [re-frame.core :as re-frame]))

;;;
;;; Coeffects Registration
;;;

(storage/reg-co-fx! :nat1 {:fx   :store
                           :cofx :store})

;;;
;;; Authentication Event Handler Registrations
;;;

(re-frame/reg-event-fx
  :auth/set-token
  [(re-frame/inject-cofx :store)]
  (fn [{:keys [store] :as cofx} [_ token]]
    {:store (assoc store :auth-token token)}))

(re-frame/reg-event-fx
  :auth/clear-token
 [(re-frame/inject-cofx :store)]
 (fn [{:keys [db store] :as cofx} _]
   {:store (dissoc store :auth-token)
    :db    (dissoc db ::active-user)}))

(re-frame/reg-event-db
  :auth/set-active-user
  (fn [db [_ user]]
    (assoc db ::active-user user)))

;;;
;;; Authentication Token HTTP Interceptors
;;;

(defrecord JwtRequestInterceptor [request-token]
  Interceptor
  (-process-request [_ request]
    (cond-> request
      request-token
      (update-in [:headers "Authorization"]
                 (constantly (str "Token " request-token)))))
  (-process-response [_ response] response))

(defrecord JwtResponseInterceptor []
  Interceptor
  (-process-request [_ request] request)
  (-process-response [_ response]
    (when-let [token (.getResponseHeader response "X-Jwt")]
      (println "Storing JWT token" token)
      (re-frame/dispatch [:auth/set-token token]))
    response))

;;;
;;; API Endpoint Registration Helpers
;;;

(def base-url
  "http://localhost:3000")

(defn reg-api-fx
  ([id handler]
    (reg-api-fx id nil handler))
  ([id interceptors handler]
    (re-frame/reg-event-fx
      id
      (concat interceptors [(re-frame/inject-cofx :store)])
      (fn [{:keys [store] :as cofx} event]
        (let [fx (handler cofx event)]
          (-> fx
            (update :http-xhrio merge {:format          (ajax/json-request-format)
                                       :response-format (ajax/json-response-format {:keywords? true})
                                       :interceptors    [(JwtRequestInterceptor. (:auth-token store))
                                                         (JwtResponseInterceptor.)]})
            (update-in [:http-xhrio :uri] #(str base-url "/" %))))))))

