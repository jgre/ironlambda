(ns ironlambda.score
  "Defining, manipulating, and playing scores."
  (:require [overtone.core :as ot]
            [overtone.inst.sampled-piano :refer [sampled-piano]]))

(defprotocol MidiValue
  "A protocol for things that can be turned into a single MIDI pitch value."
  (midi [note-or-pitch] "Return the MIDI pitch value of a note or pitch record."))

(defn pitch->str
  "String representation for a pitch."
  [{:keys [letter accidental octave]}]
  (str letter (cond (= :sharp accidental) "#" (= :flat accidental) "b") octave))

(defrecord Pitch [letter accidental octave]
  MidiValue
  (midi [p]
    (ot/note (pitch->str p))))

(defprotocol Playable
  "A protocol for musical constructs that can be played on an instrument."
  (play [music metronome beat instrument]
    "Play a musical construct on 'instrument' when 'metronome' is at 'beat'"))

(defrecord Note [pitch duration]
  MidiValue
  (midi [{pitch :pitch}] (midi pitch))
  Playable
  (play [n metronome beat instrument]
    (let [end (+ beat (:duration n))]
      (if n
        (let [id (ot/at (metronome beat) (instrument (midi n)))]
          (ot/at (metronome end) (ot/ctl id :gate 0))))
      end)))

(defn pitch
  "Return a new pitch record"
  [letter accidental octave]
  (ironlambda.score.Pitch. (Character/toUpperCase letter) accidental octave))

(defn note
  "Return a new note record."
  ([pitch duration]
     (Note. pitch duration))
  ([letter accidental octave duration]
     (note (pitch letter accidental octave) duration)))

(defn notes
  "Return a sequence of note records from a flat argument list of pitches and durations."
  [& notes]
  (->> notes
       (reduce
        (fn [{:keys [notes cur]} x]
          (cond
           (empty? cur)
           (cond (isa? (type x) ironlambda.score.Pitch)  {:notes notes :cur [x]}
                 (satisfies? ironlambda.score.Playable x) {:notes (conj (vec notes) x) :cur nil}
                 :else (throw (RuntimeException. (str "Unexpected value " x " of type " (type x)
                                                      " where a pitch or a playable construct was expected."))))
           (= 1 (count cur))
           (if (isa? (type x) java.lang.Number)
             {:notes notes :cur (conj cur x)}
             (throw (RuntimeException. (str "Unexpected value " x " of type " (type x)
                                            " where a numerical duration was expected."))))
           :else
           (cond (= ironlambda.score.Pitch (type x))  {:notes (conj (vec notes) (apply note cur)) :cur [x]}
                 (satisfies? ironlambda.score.Playable x)
                 {:notes (conj (vec notes) (apply note cur) x) :cur nil}
                 :else {:notes notes :cur (conj cur x)})))
        {})
       ((fn [{:keys [notes cur]}] (conj (vec notes) (apply note cur))))))

(extend-type clojure.lang.Sequential
  Playable
  (play [score metronome beat instrument]
    (if-let [cur-note (first score)]
      (let [next-beat (play cur-note metronome beat instrument)]
        (ot/apply-at (metronome next-beat) play (next score) metronome next-beat instrument [])))))

(defrecord Simultaneity [playables duration]
  Playable
  (play [sim metronome beat instrument]
    (doseq [p (:playables sim)] (play p metronome beat instrument))
    (+ beat (:duration sim))))

(defn chord
  "Return a chord record containing an arbitrary number of notes. If no
  duration is specified, the chord lasts as long as the longest note."
  ([notes]
     (let [duration (apply max (map :duration notes))]
       (chord notes duration)))
  ([notes duration]
     (Simultaneity. notes duration))
  ([duration pitch1 pitch2 & pitches]
     (chord (map #(note % duration) (concat [pitch1 pitch2] pitches)) duration)))

(defn duration
  "Calculate the duration of a sequence of playables."
  [ps]
  (reduce + (map :duration ps)))

(defn voices
  "Return a structure of multiple sequences of notes to be played simulaneously."
  ([vs duration]
     (Simultaneity. vs duration))
  ([vs]
     (let [durations (map duration vs)]
       (voices vs (apply max durations)))))

(defn perform
  "Play a playable structure on an instrument with a number of beats per minute (default is 120)."
  ([instrument playable bpm]
     (let [m (ot/metronome bpm)]
       (play playable m (m) instrument)))
  ([instrument playable]
     (perform instrument playable 120)))

(def piano (partial perform sampled-piano))

(comment
  (def c (chord 3 C4 G4))
  (piano-play c)

  (satisfies? Play c)

  (= (type C3) ironlambda.score.Pitch)
  (def soprano (notes A4 2 D5 2 C5 2 A4 2))
  (def alto (notes D4 1 E4 1 F4 1 G4 1 A4 1 A3 0.5 B3 0.5 C4 0.5 A3 0.5 F4 1))

  (pprint alto)
  (piano-play soprano)
  (piano-play alto)
  (piano-play (voices [soprano alto]))

  (piano-play (chord 4 C4 E4 G4 B4) )

  (play (notes C4 1 D4 1 E4 1) m (m) sampled-piano)

  (play (chord (notes C4 1 C3 4)) m (m) sampled-piano)

  (piano-play (notes (chord (notes C4 1 C3 4) 1) D4 1 E4 0.5 D4 0.5 C4 1)))
