(ns dnd.engine.dice-test
  (:use clojure.test)
  (:require [dnd.engine.dice :as dice])
  (:import (org.apache.commons.math3.stat.inference ChiSquareTest)))

(defn ^:private counts->freqs
  [a fe]
  (let [fa (frequencies a)
        keys (into (sorted-set) (concat (keys fa) (keys fe)))]
    [(for [key keys] (or (get fa key) 0))
     (for [key keys] (or (get fe key) 0))]))

(defn ^:private expected-freqs
  [n sides]
  (let [closest-n  (* sides (long (Math/floor (/ n sides))))
        iterations (/ closest-n sides)]
    [closest-n
     (into {} (for [i (range 1 (inc sides))]
                [i iterations]))]))

(defn ^:private uniform-distribution?
  [sides observed]
  (let [n       (count observed)
        alpha   0.01
        tester  (ChiSquareTest.)
        [n* ef] (expected-freqs n sides)
        [fo fe] (counts->freqs (take n* observed) ef)]
    (not (.chiSquareTest tester
                         (double-array fe)
                         (long-array fo)
                         alpha))))

(deftest test-roll-die
  (let [n 10000]
    (doseq [sides [3 4 6 8 10 12 20 100]]
      (let [observed (repeatedly n #(dice/roll-die sides))]
        (is (uniform-distribution? sides observed)
            (format "Not uniform: sides=%s" sides))))))
