(ns dnd.routes.signup
  (:require [compojure.api.sweet :as capi :refer [GET POST PUT DELETE]]
            [dnd.auth.user :as user]
            [dnd.domain.user :refer [User]]
            [ring.util.http-status :as http-status]
            [ring.util.response :as resp]
            [schema.core :as s]))

(def routes
  (capi/routes
   (POST "/" []
     :body [user User]
     :responses {http-status/ok {:schema User}}
     (println (format "Signing up user '%s' (%s)"
                      (:username user)
                      (:email user)))
     (resp/response (user/create! user)))))
