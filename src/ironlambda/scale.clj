(ns ironlambda.scale
  "Functions for dealing with scales.

  Adapted from Chris Ford's Goldberg project https://github.com/ctford/goldberg."
  (:require [overtone.music.pitch :refer [note]]))

(defn- sum-n [series n] (reduce + (take n series)))

(defn scale
  "Takes a vector of semitone steps and returns a function that takes a number n and returns the
  the number of semitones between 0 and the nth note in the scale."
  [intervals]
   #(if-not (neg? %)
     (sum-n (cycle intervals) %)
     ((comp - (scale (reverse intervals)) -) %)))

(def major (scale [2 2 1 2 2 2 1]))
(def blues (scale [3 2 1 1 3 2]))
(def pentatonic (scale [3 2 2 3 2]))
(def chromatic (scale [1]))

(defn- start-from [base] (partial + base))

(defn mode [scale n] (comp #(- % (scale n)) scale (start-from n)))

(defs
  [ionian dorian phrygian lydian mixolydian aeolian locrian]
  (map (partial mode major) (range)))

(def minor aeolian)

(defn note-in-key
  "Takes a key's tonic, its mode, and an offset
  from the key's tonic and returns the corresponding MIDI number."
  [tonic mode offset & opts]
  (let [sopts (set opts)
        halftone (cond (sopts :flat) -1
                       (sopts :sharp) 1
                       :else 0)]
    (+ (note tonic) (mode offset) halftone)))

(defn offset-in-key
  "Return the number of regular steps in the mode between the tonic and the MIDI note n."
  [tonic mode n]
  (let [half-steps (- (note n) (note tonic))
        direction (if (neg? half-steps) -1 1)
        closest (->> (range direction (+ direction half-steps) direction)
                     (map mode)
                     (take-while #(<= (Math/abs %) (Math/abs half-steps)))
                     (#(* direction (count %))))]
    (if (= half-steps (mode closest)) [closest]
        (if (neg? half-steps) [closest :flat]
            [closest :sharp]))))
