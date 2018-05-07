(ns dnd.domain.common
  (:require [schema.core :as s]))

(s/defschema UUID
  (s/constrained s/Str #(= 25 (count %)) ::correct-uuid-length))

