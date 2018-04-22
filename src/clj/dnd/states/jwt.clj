(ns dnd.states.jwt
  (:require [buddy.auth.backends :as backends]
            [mount.core :as mount]))

(mount/defstate jwt-secret
  :start "this is a bad secret")

(mount/defstate jwt-backend
  :start (backends/jws {:secret jwt-secret}))
