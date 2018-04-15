(ns dnd.engine.dice)

(defn roll-die
  [sides]
  (inc (rand-int sides)))

(defn roll
  ([sides]
   (roll-die sides))
  ([sides modifier]
   (roll 1 sides modifier))
  ([n sides modifier]
   (apply + modifier (repeatedly n #(roll-die sides)))))
