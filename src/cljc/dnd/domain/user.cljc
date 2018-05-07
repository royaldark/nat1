(ns dnd.domain.user
  (:require [clojure.string :as str]
            [dnd.domain.common :refer [UUID]]
            [schema.core :as s])
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

(defn valid-user-id?
  [id]
  (let [[type uuid] (str/split id #":")]
    (and (= "user" type)
         (nil? (s/check UUID uuid)))))

(s/defschema UserId
  (s/constrained s/Str valid-user-id? ::valid-user-id))

(s/defschema Username
  (s/constrained s/Str valid-username? ::valid-username))

(s/defschema EmailAddress
  (s/constrained s/Str valid-email? ::valid-email))

(s/defschema User
  {(s/optional-key :id)       UserId
   :username                  Username
   :email                     EmailAddress
   (s/optional-key :password) s/Str})
