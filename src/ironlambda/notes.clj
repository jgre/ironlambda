(ns ironlambda.notes
  "Convenient definition of notes."
  (:require [ironlambda.score :refer [pitch pitch->str]]))

(doall (for [letter [\a \b \c \d \e \f \g] acc [:flat nil :sharp] octave (range 8)]
         (let [p (pitch letter acc octave)]
           (intern *ns* (symbol (pitch->str p)) p))))
