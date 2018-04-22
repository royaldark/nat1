(ns dnd.server
  (:gen-class)
  (:require
    ;; Initialization Dependencies
   [mount.core :as mount]

    ;; Mount States
   dnd.states.db
   dnd.states.http-server
   dnd.states.jwt))

(defn start-server
  []
  (mount/start))

(defn stop-server
  []
  (mount/stop))

(defn -main [& args]
  (start-server))
