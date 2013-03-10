(ns ironlambda.score-test
  (:use ironlambda.score
        ironlambda.notes
        expectations))

(expect 60 (midi C4))

(expect [{:pitch {:letter \A, :accidental nil, :octave 4}, :duration 2}
         {:pitch {:letter \D, :accidental nil, :octave 5}, :duration 2}
         {:pitch {:letter \C, :accidental nil, :octave 5}, :duration 2}
         {:pitch {:letter \A, :accidental nil, :octave 4}, :duration 2}]
        (notes A4 2 D5 2 C5 2 A4 2))

(expect [{:pitch {:letter \A, :accidental nil, :octave 4}, :duration 2}
         {:playables
          '({:pitch {:letter \D, :accidental nil, :octave 5}, :duration 2}
            {:pitch {:letter \D, :accidental nil, :octave 4}, :duration 2})
          :duration 2}
         {:pitch {:letter \C, :accidental nil, :octave 5}, :duration 2}
         {:pitch {:letter \A, :accidental nil, :octave 4}, :duration 2}]
        (notes A4 2 (chord 2 D5 D4) C5 2 A4 2))

(expect "(notes A4 2 D5 2 C5 2 A4 2)"
        (notation (notes A4 2 D5 2 C5 2 A4 2)))

(expect "(chord [(note C4 2) (note G4 1)] 2)"
        (notation (chord [(note C4 2) (note G4 1)])))

(expect "(chord 4 C4 E4 G4 B4)"
        (notation (chord 4 C4 E4 G4 B4)))

(expect 1 (interval [C4 :major] 0))

(expect 2 (interval [C4 :major] 2))

(expect 3 (interval [C4 :major] 4))

(expect -2 (interval [C4 :major] -1))

(expect -8 (interval [C4 :major] -12))

(expect 0 (semitones [C4 :major] 1))

(expect -1 (semitones [C4 :major] -2))

(expect 7 (semitones [C4 :major] 5))

(expect 14 (semitones [C4 :major] (interval [C4 :major] 14)))

(expect 3 (interval [A4 :minor] 3))

(expect 3 (semitones [A4 :minor] 3))

(expect 4 (semitones [A4 :major] 3))

(expect (note C4 1) (into-scale [C4 :major] (rel-note 1 0 1)))

(expect (note B3 1) (into-scale [C4 :major] (rel-note -2 0 1)))

(expect (note C5 1) (into-scale [C4 :major] (rel-note 8 0 1)))

(expect (note F3 1) (into-scale [C4 :major] (rel-note -5 0 1)))

(expect (note A4 1) (into-scale [D4 :minor] (rel-note 5 0 1)))

(expect (note Bb4 1) (into-scale [D4 :minor] (rel-note 6 0 1)))

(expect (note F#4 1) (into-scale [G3 :major] (rel-note 7 0 1)))

(expect (note B4 1) (into-scale [D4 :minor] (rel-note 6 1 1)))

(expect (note A#3 1) (into-scale [A3 :minor] (rel-note 1 1 1)))

(expect (note Bb3 1) (into-scale [A3 :minor] (rel-note 2 -1 1)))

(expect (rel-note 1 0 1)
        (relative-to [C4 :major] (note C4 1)))

(expect (rel-note 2 0 1)
        (relative-to [C4 :major] (note D4 1)))

(expect (rel-note -2 0 1)
        (relative-to [C4 :major] (note B3 1)))

(expect (rel-note 8 0 1)
        (relative-to [C4 :major] (note C5 1)))

(expect (rel-note -8 0 1)
        (relative-to [C4 :major] (note C3 1)))

(expect (rel-note 2 1 1)
        (relative-to [C4 :major] (note D#4 1)))

(expect (rel-note 2 -1 1)
        (relative-to [C4 :major] (note Db4 1)))

(expect (rel-note -2 1 2)
        (relative-to [D4 :minor] (note C#4 2)))

(expect (rel-note -1 -1 2)
        (relative-to [D4 :minor] (note Db4 2)))

(expect (note Db4 2)
        (into-scale [D4 :minor] (relative-to [D4 :minor] (note Db4 2))))

(expect (note C#4 2)
        (into-scale [D4 :minor] (relative-to [D4 :minor] (note C#4 2))))

(expect (note Db4 2)
        (into-scale [C4 :major] (relative-to [C4 :major] (note Db4 2))))

(expect [(rel-note 1 0 1) (rel-note 2 0 1) (rel-note 3 0 1)]
        (relative-to [C4 :major] (notes C4 1 D4 1 E4 1)))

(expect (notes A3 1 B3 1 E4 2)
        (into-scale [A3 :minor] [(rel-note 1 0 1) (rel-note 2 0 1) (rel-note 5 0 2)]))

(expect [(rel-note 1 0 1) (rel-note 2 0 1) (rel-note 5 0 2)]
        (relative-to [A3 :minor] (into-scale [A3 :minor] [(rel-note 1 0 1) (rel-note 2 0 1) (rel-note 5 0 2)])))

(expect (notes C4 1 D4 1 E4 1 F4 1 G4 1 A4 1 B4 1 C5 1)
        (into-scale [C4 :major] (relative-to [C4 :major] (notes C4 1 D4 1 E4 1 F4 1 G4 1 A4 1 B4 1 C5 1))))

(expect (notes D3 1 E3 1 F3 1 G3 1 A3 1 Bb3 1 C4 1 D4 1)
        (into-scale [D3 :minor] (relative-to [C4 :major] (notes C4 1 D4 1 E4 1 F4 1 G4 1 A4 1 B4 1 C5 1))))

(expect (notes D3 1 Eb3 1 F3 1 G#3 1 A3 1 Bb3 1 C4 1 D4 1)
        (into-scale [D4 :minor] (relative-to [C5 :major] (notes C4 1 Db4 1 E4 1 F#4 1 G4 1 A4 1 B4 1 C5 1))))

(expect (notes A4 2 E5 2 C5 2 A4 2 G#4 2 A4 1 B4 1 C5 2.5 D5 0.5 C5 0.5 B4 0.5 A4 1)
        (transpose [D4 :minor] [A4 :minor]
                   (notes D4 2 A4 2 F4 2 D4 2 C#4 2 D4 1 E4 1 F4 2.5 G4 0.5 F4 0.5 E4 0.5 D4 1)))
