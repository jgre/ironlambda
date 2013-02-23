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
