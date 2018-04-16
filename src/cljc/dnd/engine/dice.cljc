(ns dnd.engine.dice
  (:require [dnd.api :as api]))

(defn roll-die
  [sides]
  (inc (rand-int sides)))

(defn roll
  ([sides]
   (roll-die sides))
  ([sides modifier]
   (roll 1 sides modifier))
  ([n sides modifier]
   (api/assert (pos? n) "Number of dice must be at least 1.")
   (println "n=" n)
   (apply + modifier (repeatedly n #(roll-die sides)))))
