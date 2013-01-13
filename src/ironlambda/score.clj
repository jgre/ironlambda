(ns ironlambda.score
  "Defining, manipulating, and playing scores."
  (:require [overtone.core :as ot]))

(defn play-one
  "Play a single note defined in the score DSL on an instrument at the given beat of the metronome."
  [metronome beat instrument [n len]]
  (let [end (+ beat len)]
    (if n
      (let [id (at (metronome beat) (instrument (ot/note n) :release 0.1))]
        (ot/at (metronome end) (ot/ctl id :gate 0))))
    end))

(defn- play-internal
  [metronome beat instrument score]
  (let [cur-note (first score)]
    (when cur-note
      (let [next-beat (play-one metronome beat instrument cur-note)]
        (ot/apply-at (metronome next-beat) play-internal metronome next-beat instrument (next score) [])))))

(defn play
  "Play a score defined in the score DSL on a given instrument using a metronome to define the tempo."
  [metronome inst score]
  (play-internal metronome (metronome) inst score))
