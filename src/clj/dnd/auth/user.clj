(ns dnd.auth.user
  (:require [codax.core :as c]
            [clojure.string :as str]
            [dnd.states.db :refer [db]])
  (:import (java.util UUID)))

(defn ->byte-array [n]
  (let [bb (java.nio.ByteBuffer/allocate 8)]
    (-> bb (.putLong n) .array)))

(defn uuid []
  (let [juuid (UUID/randomUUID)
        least (->byte-array (.getLeastSignificantBits juuid))
        most  (->byte-array (.getMostSignificantBits juuid))
        b36   (.toString (BigInteger. 1 (byte-array (concat most least))) 36)]
    (str/replace (format "%25s" b36) #" " "0")))

(defn create!
  [user]
  (let [id (uuid)]
    (c/assoc-at! db [:users id] (assoc user :id id))))
