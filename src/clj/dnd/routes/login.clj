(ns dnd.routes.login
  (:require [compojure.api.sweet :as capi :refer [GET POST PUT DELETE]]
            [dnd.auth.user :as user]
            [dnd.domain.user :refer [User Username Password]]
            [ring.util.http-status :as http-status]
            [ring.util.response :as resp]
            [schema.core :as s]))

(def routes
  (capi/routes
   (POST "/" []
     :body-params [username :- Username
                   password :- Password]
     :responses {http-status/ok {:schema User}}
     (println (format "Login %s" username))
     (resp/response (user/login! username password)))))
