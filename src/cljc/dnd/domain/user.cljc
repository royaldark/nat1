(ns dnd.domain.user
  (:require [schema.core :as s]
            #?@(:clj [[buddy.core.codecs :as codec]
                      [buddy.core.hash :as hash]
                      [buddy.core.nonce :as nonce]]))
  (:import #?(:clj (org.apache.commons.validator.routines EmailValidator))))

#?(:cljs
    ;; Taken from https://github.com/reagent-project/reagent-utils/blob/93668d46134c1e4697b0c097dcf0faccd6722900/src/reagent/validation.cljs#L46
   (def email-regex
     #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"))

(defn valid-username?
  [s]
  (re-matches #"[a-zA-Z0-9-]{3,64}" s))

(defn valid-email?
  [s]
  #?(:clj  (-> (EmailValidator/getInstance) (.isValid s))
     :cljs (re-matches email-regex s)))

#?(:clj
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

   (s/defschema HashedPassword
     (s/constrained s/Str valid-hashed-password? ::valid-hashed-password)))

(s/defschema UUID
  (s/constrained s/Str #(= 25 (count %)) ::correct-uuid-length))

(s/defschema Username
  (s/constrained s/Str valid-username? ::valid-username))

(s/defschema EmailAddress
  (s/constrained s/Str valid-email? ::valid-email))

(s/defschema User
  {(s/optional-key :id)       UUID
   :username                  Username
   :email                     EmailAddress
   (s/optional-key :password) #?(:clj HashedPassword
                                 :cljs s/Str)})
