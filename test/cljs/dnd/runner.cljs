(ns dnd.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [dnd.core-test]))

(doo-tests 'dnd.core-test)
