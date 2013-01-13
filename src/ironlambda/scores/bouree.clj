(ns ironlambda.scores.bouree
  "Simple piece for piano in e minor"
  (:require [ironlambda.score :refer [play]]
            [overtone.core :refer [metronome]]
            [overtone.inst.sampled-piano :refer [sampled-piano]]))

(def melody1 [[:b4 1]
              [:g5 1] [:f#5 1] [:f#5 1] [:e5 0.5] [:d#5 0.5]
              [:e5 1] [:b4 1] [:b4 1] [:c5 1]
              [:b4 1] [:a4 1] [:g4 1] [:f#4 1]
              [:g4 1] [:f#4 0.5] [:g4 0.5] [:e4 1]])
(comment
  (play (metronome 100) sampled-piano melody1))
