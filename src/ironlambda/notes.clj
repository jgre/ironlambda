(ns ironlambda.notes
  "Convenient definition of notes."
  (:require [ironlambda.score :refer [pitch notation pitch-range]]))

(doseq [p (pitch-range (range 8))] (intern *ns* (symbol (notation p)) p))
