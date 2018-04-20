(ns dnd.routes.signup
  (:require [compojure.api.sweet :as capi :refer [GET POST PUT DELETE]]
            [dnd.auth.user :as user]
            [ring.util.response :as resp]
            [schema.core :as s]))

(def routes
  (capi/routes
   (POST "/" []
     :body-params [username :- s/Str
                   email    :- s/Str
                   password :- s/Str]
     :return {:id id}
     (println (format "Signing up user '%s' (%s)" username email))
     (resp/response (user/create! username email password)))))
