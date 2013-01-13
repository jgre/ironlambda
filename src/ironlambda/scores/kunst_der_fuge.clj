(ns ironlambda.scores.kunst-der-fuge
  "Experiments with parts of 'Die Kunst der Fuge' ('The Art of Fugue') by J.S. Bach."
  (:require [ironlambda.score :refer [play]]
            [overtone.core :refer [metronome]]
            [overtone.inst.sampled-piano :refer [sampled-piano]]))

(def subject [[:d4 2] [:a4 2] [:f4 2] [:d4 2] [:c#4 2] [:d4 1] [:e4 1]
              [:f4 2.5] [:g4 0.5] [:f4 0.5] [:e4 0.5] [:d4 1]])

(comment
  (play (metronome 120) sampled-piano subject))
