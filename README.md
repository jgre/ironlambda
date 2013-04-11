# Iron Lambda

Experiments with music in Clojure using [Overtone](https://github.com/overtone/overtone).

This project provides a DSL for describing and manipulating musical
scores. It tries to use musical terminology to make it easy to apply
knowledge about music theory (or alternatively rip off Wikipedia
articles about music theory). The goal of the project is to
programmatically compose music by combining melodies and various
transformations.

To try it out, check out the code from GitHub and start a REPL.

    lein repl

To get access to the DSL use the notes and score namespaces which will
also start Overtone.

    (use 'ironlambda.score)
    (use 'ironlambda.notes)

In order to play stuff on a piano, use the performance namespace.

    (use 'ironlambda.performance)

Now you can start to play.

    (piano (note C4 2))

This plays a C in the fourth octave for two beats. Melodies can be
written using the notes function and giving it a series of note names
and durations.

    (def subject (notes D4 2 A4 2 F4 2 D4 2 C#4 2 D4 1 E4 1 F4 2.5 G4 0.5
                        F4 0.5 E4 0.5 D4 1))
    (piano subject)

So far this is merely an ASCII notation for notes. Where it gets
interesting is when we add transformations. So far there is only
transposition, but more are coming.

    (def response (transpose [D4 :minor] [A4 :minor] subject))

Inspecting the results of the transposition visually using the internal
representation of the notes is not great, so we'd rather see it in the
same notation we use to define notes.

    (notation response)
    ;=> "(notes A4 2 E5 2 C5 2 A4 2 G#4 2 A4 1 B4 1 C5 2.5 D5 0.5 C5 0.5 B4 0.5 A4 1)"

Playing notes in sequence is not enough, though. We also need to be able
to play multiple voices simulaneously.

    (piano (voices [subject response]))

The `ironlambda.scores.kunst-der-fuge` namespace contains another
example, expressing the beginning of "Die Kunst der Fuge" ("The Art of
Fuge") by J.S. Bach in this syntax.

## License

Copyright © 2013 Janico Greifenberg

Distributed under the Eclipse Public License, the same as Clojure.
