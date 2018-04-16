(ns dnd.api
  (:refer-clojure :exclude [assert])
  (:require [clojure.core :as core]))

(defn http-error
  ([status msg] (http-error status msg nil))
  ([status msg data] (http-error status msg data nil))
  ([status msg data cause]
   (let [data* (assoc data
                      :type ::http-error
                      ::http-status status)]
     (ex-info msg data* cause))))

(defmacro assert
  [& forms]
  `(try
     (core/assert ~@forms)
     (catch AssertionError ae#
       (throw (http-error 500 (.getMessage ae#) nil ae#)))))
