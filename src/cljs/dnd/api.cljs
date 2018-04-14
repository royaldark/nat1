(ns dnd.api)

(defn roll-die [sides]
  (js/alert (rand-int sides)))