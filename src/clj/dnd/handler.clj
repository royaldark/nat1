(ns dnd.handler
  (:require [compojure.core :refer [GET POST PUT PATCH DELETE defroutes]]
            [compojure.route :refer [resources]]
            [ring.middleware.cors :as cors]
            [ring.middleware.json :as json]
            [ring.middleware.reload :as reload]
            [ring.util.response :as response :refer [resource-response]]))

(defroutes routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (POST "/roll-die" [:as {body :body}]
    (let [{:keys [sides modifier]
           :or   {modifier 0}}
          body]
      (println body)
      (println (format "Rolling 1d%s+%s" sides modifier))
      (response/response {:roll (+ (inc (rand-int sides))
                                   modifier)})))
  (resources "/"))

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
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true :bigdecimals? true})
      (cors/wrap-cors :access-control-allow-origin  [#".*"]
                      :access-control-allow-methods [:get :put :post :delete])))
