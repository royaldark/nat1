(ns dnd.states.http-server
  (:require [config.core :as conf]
            [dnd.handler :refer [handler]]
            [dnd.states.db :refer [db]]
            [mount.core :as mount]
            [ring.adapter.jetty :refer [run-jetty]])
  (:import (org.eclipse.jetty.server Server)))

(defn start-server!
  []
  (let [port (Integer/parseInt (or (conf/env :port) "3000"))]
    (run-jetty #'handler {:port port :join? false})))

(defn stop-server!
  [^Server server]
  (.stop server))

(mount/defstate http-server
  :start (start-server!)
  :stop  (stop-server! http-server))
