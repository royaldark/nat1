(ns dnd.handler
  (:require [compojure.api.coercion.schema :as schema-coercion]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :as buddy]
            [compojure.api.sweet :as capi :refer [GET]]
            [compojure.core]
            [compojure.route :refer [resources]]
            [dnd.api :as api]
            [dnd.states.jwt :refer [jwt-backend]]
            [dnd.routes.roll :as roll]
            [dnd.routes.signup :as signup]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.cors :as cors]
            [ring.middleware.reload :as reload]
            [ring.util.response :as resp]
            [ring.util.http-response :as hresp]
            [schema.core :as s]))

(defn render-http-error
  [^Exception e data request]
  (let [data     (ex-data e)
        status   (or (::api/http-status data) 500)
        resp-map {:error   true
                  :status  status
                  :message (.getMessage e)}]
    (-> (hresp/ok resp-map)
        (hresp/status status))))

(def routes
  (capi/api
   {:coercion (schema-coercion/create-coercion
               (-> schema-coercion/default-options
                   (assoc-in [:response :default]
                             schema-coercion/json-coercion-matcher)
                   (assoc-in [:body :default]
                             schema-coercion/json-coercion-matcher)))
    :exceptions  {:handlers
                  {::api/http-error render-http-error}}
    :swagger {:ui "/api-docs"
              :spec "/swagger.json"
              :data {:info {:title ""
                            :description ""}}}}

   (GET "/" [] (resp/resource-response "index.html" {:root "public"}))

   (capi/context "/roll" [] #'roll/routes)
   (capi/context "/signup" [] #'signup/routes)

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
      (buddy/wrap-authentication jwt-backend)
      (cors/wrap-cors :access-control-allow-origin   [#".*"]
                      :access-control-allow-methods  [:get :put :post :delete]
                      :access-control-expose-headers ["X-JWT"])))
