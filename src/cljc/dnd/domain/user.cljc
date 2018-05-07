(ns dnd.domain.user
  (:require [schema.core :as s])
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
   (s/optional-key :password) s/Str})
