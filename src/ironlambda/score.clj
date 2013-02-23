(ns ironlambda.score
  "Defining, manipulating, and playing scores."
  (:require [overtone.core :as ot]))

(defmulti notation
  "Return a string representing the musical structure in the DSL format."
  type)

(defmulti midi
  "Return the MIDI pitch value of a note or pitch record."
  type)

(defmulti play
  "(play instrument metronome beat music)
  Play a 'music' on an 'instrument' when 'metronome' is at 'beat'."
  (fn [instrument metonome beat music] (type music)))

(defn pitch
  "Return a new pitch structure."
  [letter accidental octave]
  (with-meta {:letter (Character/toUpperCase letter) :accidental accidental :octave octave}
    {:type ::Pitch}))

(defmethod notation ::Pitch
  [{:keys [letter accidental octave]}]
  (str letter (cond (= :sharp accidental) "#" (= :flat accidental) "b") octave))

(defmethod midi ::Pitch
  [pitch]
  (ot/note (notation pitch)))

(defn note
  "Return a new note structure."
  ([pitch duration]
     (with-meta {:pitch pitch :duration duration}
       {:type ::Note}))
  ([letter accidental octave duration]
     (note (pitch letter accidental octave) duration)))

(defmethod notation ::Note
  [{:keys [pitch duration]}]
  (str "(note " (notation pitch) " " duration ")"))

(defmethod midi ::Note
  [{pitch :pitch}]
  (midi pitch))

(defmethod play ::Note
  [instrument metronome beat n]
  (let [end (+ beat (:duration n))]
    (if n
      (let [id (ot/at (metronome beat) (instrument (midi n)))]
        (ot/at (metronome end) (ot/ctl id :gate 0))))
    end))

(derive ::Note ::Playable)

(derive ::Notes ::Playable)

(defn notes
  "Return a sequence of note records from a flat argument list of pitches and durations."
  [& notes]
  (->> notes
       (reduce
        (fn [{:keys [notes cur]} x]
          (cond
           (empty? cur)
           (cond (isa? (type x) ::Pitch)    {:notes notes :cur [x]}
                 (isa? (type x) ::Playable) {:notes (conj notes x) :cur nil}
                 :else (throw (RuntimeException. (str "Unexpected value " x " of type " (type x)
                                                      " where a pitch or a playable construct was expected."))))
           (= 1 (count cur))
           (if (isa? (type x) java.lang.Number)
             {:notes notes :cur (conj cur x)}
             (throw (RuntimeException. (str "Unexpected value " x " of type " (type x)
                                            " where a numerical duration was expected."))))
           :else
           (cond (isa? (type x) ::Pitch) {:notes (conj notes (apply note cur)) :cur [x]}
                 (isa? (type x) ::Playable)
                 {:notes (conj notes (apply note cur) x) :cur nil}
                 :else {:notes notes :cur (conj cur x)})))
        {:notes [] :cur nil})
       ((fn [{:keys [notes cur]}] (with-meta (conj notes (apply note cur)) {:type ::Notes})))))


(defmethod play ::Notes
  [instrument metronome beat notes]
  (if-let [cur-note (first notes)]
    (let [next-beat (play instrument metronome beat cur-note)]
      (ot/apply-at (metronome next-beat) play instrument metronome next-beat
                   (with-meta (next notes) (meta notes)) []))))

(defmethod notation ::Notes
  [notes]
  (apply str "(notes"
         (concat (map (fn [{:keys [pitch duration]}] (str " " (notation pitch) " " duration)) notes)
                 [")"])))

(derive ::Simultaneity ::Playable)
(derive ::Chord ::Simultaneity)

(defn chord
  "Return a chord record containing an arbitrary number of notes. If no
  duration is specified, the chord lasts as long as the longest note."
  ([notes]
     (let [duration (apply max (map :duration notes))]
       (chord notes duration)))
  ([notes duration]
     (with-meta {:playables notes :duration duration} {:type ::Chord}))
  ([duration pitch1 pitch2 & pitches]
     (chord (map #(note % duration) (concat [pitch1 pitch2] pitches)) duration)))

(defmethod play ::Simultaneity
  [instrument metronome beat sim]
  (doseq [p (:playables sim)] (play instrument metronome beat p))
  (+ beat (:duration sim)))

(defmethod notation ::Simultaneity
  [{:keys [playables duration]}]
  (apply str "(voices " (concat (interpose " " (map notation playables)) [" " duration ")"])))

(defmethod notation ::Chord
  [{:keys [playables duration]}]
  (if (every? #(= duration (:duration %)) playables)
    (apply str "(chord " duration
           (concat (map (fn [{:keys [pitch]}] (str " " (notation pitch))) playables)
                   [")"]))
    (apply str "(chord ["
           (concat (interpose " " (map (fn [note] (notation note)) playables))
                   ["] " duration ")"]))))

(defn duration
  "Calculate the duration of a sequence of playables."
  [ps]
  (reduce + (map :duration ps)))

(defn voices
  "Return a structure of multiple sequences of notes to be played simulaneously."
  ([vs duration]
     (with-meta {:playables vs :duration duration} {:type ::Simultaneity}))
  ([vs]
     (let [durations (map duration vs)]
       (voices vs (apply max durations)))))

(comment
  (use 'ironlambda.notes)
  (use 'ironlambda.performance)

  (def c (chord 3 C4 G4))
  (piano c)

  (def soprano (notes A4 2 (chord 2 D5 D4) C5 2 A4 2))

  (def alto (notes D4 1 E4 1 F4 1 G4 1 A4 1 A3 0.5 B3 0.5 C4 0.5 A3 0.5 F4 1))
  (type alto)
  (pprint soprano)
  (piano soprano)
  (piano alto)
  (piano (voices [soprano alto]))
  (notation (voices [soprano alto]))

  (notation alto)

  (piano (chord 4 C4 E4 G4 B4))
  (notation (chord 4 C4 E4 G4 B4))

  (notation (chord [(note C4 2) (note G4 1)])))
