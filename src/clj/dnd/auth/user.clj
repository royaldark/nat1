(ns dnd.auth.user
  (:require [codax.core :as c]
            [dnd.states.db :refer [db]]))

(defn create!
  [username email password]
  (let [id (random-uuid)]
    (c/assoc-at! [:users id]
                 {:id       id
                  :username username
                  :email    email
                  :password password})))
