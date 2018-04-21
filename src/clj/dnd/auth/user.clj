(ns dnd.auth.user
  (:require [buddy.core.codecs :as codecs]
            [buddy.core.hash :as hash]
            [buddy.core.nonce :as nonce]
            [codax.core :as c]
            [clojure.string :as str]
            [dnd.states.db :refer [db]])
  (:import (java.util UUID)))

(def ^:private salt-bytes 8)
(def ^:private hash-bits 384)
(def ^:private salt-str-length (* salt-bytes 2))
(def ^:private hash-str-length (/ hash-bits 4))

(defn hash-password
  [s]
  (let [salt     (codecs/bytes->hex (nonce/random-bytes salt-bytes))
        combined (str salt s)
        hashed   (codecs/bytes->hex (hash/sha3-384 combined))]
    (str salt hashed)))

(defn password-matches?
  [input salt+hash]
  (let [salt (subs salt+hash 0 16)
        hash (subs salt+hash 16)]
    (= hash (codecs/bytes->hex
             (hash/sha3-384 (str salt input))))))

(defn valid-hashed-password?
  [s]
  (boolean
   (and (= (+ salt-str-length hash-str-length)
           (count s))
        (re-matches #"[a-f0-9]+" s))))

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
