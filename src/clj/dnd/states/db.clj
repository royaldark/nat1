(ns dnd.states.db
  (:require [codax.core :as c]
            [mount.core :as mount]))

(defn start-db! []
  (c/open-database! "dev-resources/demo-database"))

(defn stop-db! [db]
  (c/close-database! db))

(mount/defstate db
  :start (start-db!)
  :stop  (stop-db! db))
