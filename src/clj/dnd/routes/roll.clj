(ns dnd.routes.roll
  (:require [compojure.api.sweet :as capi :refer [GET POST PUT DELETE]]
            [dnd.engine.dice :as dice]
            [ring.util.response :as resp]
            [schema.core :as s]))

(def routes
  (capi/routes
   (POST "/" []
     :body-params [{number   :- s/Int 1}
                   sides     :- s/Int
                   {modifier :- s/Int 0}]
     :return {:roll s/Int}
     (println (format "Rolling %sd%s+%s" number sides modifier))
     (resp/response {:roll (dice/roll number sides modifier)}))))
