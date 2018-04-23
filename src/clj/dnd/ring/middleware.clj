(ns dnd.ring.middleware
  (:require [buddy.sign.jwt :as jwt]
            [dnd.states.jwt :refer [jwt-backend jwt-secret]]
            [ring.util.response :as resp]))

(defn wrap-debug-info
  [handler]
  (fn [request]
    (println "request:" request)
    (let [response (handler request)]
      (println "response:" response)
      response)))

(defn sign-token
  [data]
  (jwt/sign data jwt-secret))

(defn wrap-jwt-header
  [handler header]
  (fn [request]
    (let [response (handler request)]
      (if-let [data (:jwt-data response)]
        (-> response
            (dissoc :jwt-data)
            (resp/header header (sign-token data)))
        response))))