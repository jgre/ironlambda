(ns ironlambda.scores.bouree
  "Simple piece for piano in e minor"
  (:use [ironlambda.notes])
  (:require [ironlambda.score :refer [notes]]))

(def melody1 (notes B4 1 G5 1 F#5 1 F#5 1 E5 0.5 D#5 0.5
                    E5 1 B4 1 B4 1 C5 1
                    B4 1 A4 1 G4 1 F#4 1
                    G4 1 F#4 0.5 G4 0.5 E4 1))

(comment
  (use 'ironlambda.performance)
  (piano melody1 100))
