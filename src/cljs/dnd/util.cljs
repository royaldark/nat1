(ns dnd.util
  (:require [goog.string :as gstring]))

(defn nbsp []
  (gstring/unescapeEntities "&nbsp;"))