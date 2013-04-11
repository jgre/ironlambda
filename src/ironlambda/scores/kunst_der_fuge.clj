(ns ironlambda.scores.kunst-der-fuge
  "Experiments with parts of 'Die Kunst der Fuge' ('The Art of Fugue') by J.S. Bach."
  (:use [ironlambda.notes]
        [ironlambda.score])
  (:require [ironlambda.performance :refer [piano]]))

(def subject (notes D4 2 A4 2 F4 2 D4 2 C#4 2 D4 1 E4 1 F4 2.5 G4 0.5 F4 0.5 E4 0.5 D4 1))

(def response (transpose [D4 :minor] [A4 :minor] subject))

(def counterpoint (notes
                   E4 1 F4 1 G4 1 A4 1
                   A3 0.5 B3 0.5 C4 0.5 A3 0.5
                   F4 1.5 B3 0.5 E4 1.5 F4 0.5 E4 0.5 D4 0.5
                   E4 1 F#4 1 G4 3))

(defn play-exposition
  []
  (piano (voices [(notes subject counterpoint)
                  (notes nil (dec (duration subject)) response)])))


(comment
  (piano subject)
  (piano (notes subject counterpoint))
  (play-exposition)
  )
