(ns dnd.testing.fixtures
  (:require [dnd.server :as server]))

(defn server-fixture
  [f]
  (server/start-server)
  (f))
