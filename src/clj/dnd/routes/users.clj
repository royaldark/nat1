(ns dnd.routes.users
  (:require [buddy.sign.jwt :as jwt]
            [compojure.api.sweet :as capi :refer [GET POST PUT DELETE]]
            [dnd.auth.user :as auth-user]
            [dnd.domain.user :refer [EmailAddress User Username UUID]]
            [dnd.states.jwt :refer [jwt-secret]]
            [ring.util.http-status :as http-status]
            [ring.util.response :as resp]
            [schema.core :as s]))

(s/defschema LoginCredential
  (s/conditional
   :id       {:id       UUID
              :password s/Str}
   :username {:username Username
              :password s/Str}
   :email    {:email EmailAddress
              :password s/Str}))

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
     :body [creds LoginCredential]
     :responses {http-status/ok {:schema User}}
     (println (format "Login %s" creds))
     (let [user (condp #(contains? %2 %1) creds
                  :id       (auth-user/login-by-id! (:id creds)
                                                    (:password creds))
                  :username (auth-user/login-by-username! (:username creds)
                                                          (:password creds))
                  :email    (auth-user/login-by-email! (:email creds)
                                                       (:password creds)))]
       (-> (resp/response user)
           (assoc :jwt-data {:user-id (:id user)}))))

   (GET "/:id" []
     :path-params [id :- UUID]
     :responses {http-status/ok {:schema User}}
     (println (format "Get user %s" id))
     (resp/response
      (auth-user/get-user-by-id id)))))

