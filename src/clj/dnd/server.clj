(ns dnd.server
  (:require [dnd.handler :refer [handler]]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defonce server
  (atom nil))

(defn start-server
  []
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (reset! server (run-jetty #'handler {:port port :join? false}))))

(defn stop-server
  []
  (.stop @server))

(defn -main [& args]
  (start-server))
