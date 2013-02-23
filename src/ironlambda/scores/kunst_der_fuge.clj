(ns ironlambda.scores.kunst-der-fuge
  "Experiments with parts of 'Die Kunst der Fuge' ('The Art of Fugue') by J.S. Bach."
  (:use [ironlambda.notes])
  (:require [ironlambda.score :refer [notes]]))

(def subject (notes D4 2 A4 2 F4 2 D4 2 C#4 2 D4 1 E4 1 F4 2.5 G4 0.5 F4 0.5 E4 0.5 D4 1))

(comment
  (use 'ironlambda.performance)
  (piano subject))
