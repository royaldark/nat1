(ns dnd.routes.signup
  (:require [buddy.sign.jwt :as jwt]
            [compojure.api.sweet :as capi :refer [GET POST PUT DELETE]]
            [dnd.auth.user :as user]
            [dnd.domain.user :refer [User]]
            [dnd.states.jwt :refer [jwt-secret]]
            [ring.util.http-status :as http-status]
            [ring.util.response :as resp]
            [schema.core :as s]))

(def routes
  (capi/routes
   (POST "/" []
     :body [user User]
     :responses {http-status/ok {:token s/Str
                                 :user User} #_{:schema User}}
     (println (format "Signing up user '%s' (%s)"
                      (:username user)
                      (:email user)))
     (let [user (dissoc (user/create! user) :password)]
       (-> (resp/response user)
           (resp/header "X-Jwt" (jwt/sign {:user (:id user)} jwt-secret)))))))
