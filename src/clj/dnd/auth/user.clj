(ns dnd.auth.user
  (:require [buddy.core.codecs :as codecs]
            [buddy.core.hash :as hash]
            [buddy.core.nonce :as nonce]
            [codax.core :as c]
            [clojure.string :as str]
            [dnd.domain.user :refer [UserId Username EmailAddress]]
            [dnd.states.db :refer [db]]
            [schema.core :as s])
  (:import (java.util UUID)))

(def ^:private salt-bytes 8)
(def ^:private hash-bits 384)
(def ^:private salt-str-length (* salt-bytes 2))
(def ^:private hash-str-length (/ hash-bits 4))

(defn hash-password
  [s]
  (let [salt   (codecs/bytes->hex (nonce/random-bytes salt-bytes))
        hashed (codecs/bytes->hex (hash/sha3-384 (str salt s)))]
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
  (let [id   (str "user:" (uuid))
        user (-> user
                 (update :password hash-password)
                 (assoc :id id))]
    (c/assoc-at! db [:users id] user)
    user))

(defn login-by-username!
  [username password])

(def ^:private validate-id!
  (s/validator UserId))
(def ^:private validate-username!
  (s/validator Username))
(def ^:private validate-email!
  (s/validator EmailAddress))

(defn get-user-by-id
  [id]
  (validate-id! id)
  (c/get-at! db [:users id]))

(defn get-user-by-username
  [username]
  (validate-username! username)
  (let [users (c/seek-at! db [:users])]
    (->> users
         (filter (fn [[_ user]]
                   (= username (:username user))))
         first
         second)))

(defn get-user-by-email
  [email]
  (let [users (c/seek-at! db [:users])]
    (->> users
         (filter (fn [[_ user]]
                   (= email (:email user))))
         first
         second)))

(defn ^:private login!
  [fetch-fn identifier password]
  (let [user (fetch-fn identifier)]
    (println "got user:" user)
    (if (and user (password-matches? password (:password user)))
      user
      (throw (ex-info "Invalid credentials" {:identifier identifier
                                             :password   password})))))

(defn login-by-id!
  [id password]
  (println "Logging in:" id)
  (login! get-user-by-id id password))

(defn login-by-username!
  [username password]
  (println "Logging in:" username password)
  (login! get-user-by-username username password))

(defn login-by-email!
  [email password]
  (println "Logging in:" email)
  (login! get-user-by-email email password))
