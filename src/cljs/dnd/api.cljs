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

;;;
;;; Authentication Token HTTP Interceptors
;;;

(defrecord JwtRequestInterceptor [request-token]
  Interceptor
  (-process-request [_ request]
    (println "request" request)
    (println "request-token" request-token)
    (-> request
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
                                                         (JwtResponseInterceptor.)]})))))))

