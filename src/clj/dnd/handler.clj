(ns dnd.handler
  (:require [compojure.api.coercion.schema :as schema-coercion]
            [compojure.api.sweet :as capi]
            [compojure.core]
            [compojure.route :refer [resources]]
            [ring.middleware.cors :as cors]
            [ring.middleware.reload :as reload]
            [ring.util.response :as response :refer [resource-response]]
            [schema.core :as s]))

(def routes
  (capi/api
   {:coercion (schema-coercion/create-coercion
               (assoc-in
                schema-coercion/default-options
                [:response :default]
                schema-coercion/json-coercion-matcher))
    :swagger {:ui "/api-docs"
              :spec "/swagger.json"
              :data {:info {:title ""
                            :description ""}}}}

   (capi/GET "/" [] (resource-response "index.html" {:root "public"}))

   (capi/context "/roll-die" []
     (capi/POST "/" []
       :body-params [sides     :- s/Int
                     {modifier :- s/Int 0}]
       :return {:roll s/Int}
       (println (format "Rolling 1d%s+%s" sides modifier))
       (response/response {:roll (+ (inc (rand-int sides))
                                    modifier)})))

   (resources "/")))

(def dev-handler
  (-> #'routes
      reload/wrap-reload))

(defn wrap-debug-info
  [handler]
  (fn [request]
    (println "request:" request)
    (let [response (handler request)]
      (println "response:" response)
      response)))

(def handler
  (-> #'routes
      wrap-debug-info
      (cors/wrap-cors :access-control-allow-origin  [#".*"]
                      :access-control-allow-methods [:get :put :post :delete])))
