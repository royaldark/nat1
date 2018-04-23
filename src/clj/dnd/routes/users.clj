(ns dnd.routes.users
  (:require [buddy.sign.jwt :as jwt]
            [compojure.api.sweet :as capi :refer [GET POST PUT DELETE]]
            [dnd.auth.user :as auth-user]
            [dnd.domain.user :refer [User UUID]]
            [dnd.states.jwt :refer [jwt-secret]]
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
     (let [user (auth-user/create! user)]
       (-> (resp/response user)
           (assoc :jwt-data {:user-id (:id user)}))))

   (POST "/login" []
     :body-params [id       :- UUID
                   password :- s/Str]
     :responses {http-status/ok {:schema User}}
     (println (format "Login %s" id))
     (let [user (auth-user/login-by-id! id password)]
       (-> (resp/response user)
           (assoc :jwt-data {:user-id (:id user)}))))

   (GET "/:id" []
     :path-params [id :- UUID]
     :responses {http-status/ok {:schema User}}
     (println (format "Get user %s" id))
     (resp/response
      (auth-user/get-user-by-id id)))))

