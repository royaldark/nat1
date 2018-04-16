(ns dnd.engine.dice
  #?(:clj (:require [dnd.api :as api])))

(defn roll-die
  [sides]
  (#?(:clj api/assert :cljs assert)
   (and (number? sides) (pos? sides))
   "Number of sides must be a positive number.")
  (inc (rand-int sides)))

(defn roll
  ([sides]
   (roll 1 sides))
  ([n sides]
   (roll n sides 0))
  ([n sides modifier]
   (#?(:clj api/assert :cljs assert)
    (and (number? n) (pos? n))
    "Number of dice must be a positive number.")
   (#?(:clj api/assert :cljs assert)
    (number? modifier)
    "Modifier must be a number.")

   (let [rolls (repeatedly n #(roll-die sides))]
     {:rolls rolls
      :result (apply + modifier rolls)})))
