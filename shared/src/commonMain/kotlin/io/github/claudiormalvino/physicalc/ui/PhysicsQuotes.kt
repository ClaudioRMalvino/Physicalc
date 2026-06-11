package io.github.claudiormalvino.physicalc.ui

/*
 * Quote of the day — fully offline.
 *
 * A curated list of physics quotes; the day's quote is picked deterministically
 * from the date (epoch day modulo list size), so it rotates at UTC midnight,
 * needs no network, and every user sees the same quote on the same day.
 */

data class PhysicsQuote(val text: String, val author: String)

object PhysicsQuotes {

    /** The quote for the calendar day containing [epochMillis] (UTC). */
    fun forDay(epochMillis: Long): PhysicsQuote {
        val epochDay = epochMillis / 86_400_000L
        val index = ((epochDay % all.size) + all.size) % all.size // safe for pre-1970 too
        return all[index.toInt()]
    }

    val all: List<PhysicsQuote> = listOf(
        PhysicsQuote("What I cannot create, I do not understand.", "Richard Feynman"),
        PhysicsQuote("The first principle is that you must not fool yourself — and you are the easiest person to fool.", "Richard Feynman"),
        PhysicsQuote("I would rather have questions that can't be answered than answers that can't be questioned.", "Richard Feynman"),
        PhysicsQuote("Study hard what interests you the most, in the most undisciplined, irreverent and original manner possible.", "Richard Feynman"),
        PhysicsQuote("There's plenty of room at the bottom.", "Richard Feynman"),
        PhysicsQuote("The most incomprehensible thing about the universe is that it is comprehensible.", "Albert Einstein"),
        PhysicsQuote("Imagination is more important than knowledge.", "Albert Einstein"),
        PhysicsQuote("The important thing is not to stop questioning. Curiosity has its own reason for existing.", "Albert Einstein"),
        PhysicsQuote("Pure mathematics is, in its way, the poetry of logical ideas.", "Albert Einstein"),
        PhysicsQuote("Life is like riding a bicycle. To keep your balance you must keep moving.", "Albert Einstein"),
        PhysicsQuote("If I have seen further it is by standing on the shoulders of giants.", "Isaac Newton"),
        PhysicsQuote("Nothing in life is to be feared, it is only to be understood.", "Marie Curie"),
        PhysicsQuote("I am among those who think that science has great beauty.", "Marie Curie"),
        PhysicsQuote("Be less curious about people and more curious about ideas.", "Marie Curie"),
        PhysicsQuote("A scientist in his laboratory is also a child confronting natural phenomena that impress him as though they were fairy tales.", "Marie Curie"),
        PhysicsQuote("An expert is a person who has made all the mistakes that can be made in a very narrow field.", "Niels Bohr"),
        PhysicsQuote("Every great and deep difficulty bears in itself its own solution.", "Niels Bohr"),
        PhysicsQuote("All science is either physics or stamp collecting.", "Ernest Rutherford"),
        PhysicsQuote("The book of nature is written in the language of mathematics.", "Galileo Galilei"),
        PhysicsQuote("I much prefer the sharpest criticism of a single intelligent man to the thoughtless approval of the masses.", "Johannes Kepler"),
        PhysicsQuote("Nothing is too wonderful to be true, if it be consistent with the laws of nature.", "Michael Faraday"),
        PhysicsQuote("Remember to look up at the stars and not down at your feet.", "Stephen Hawking"),
        PhysicsQuote("However difficult life may seem, there is always something you can do and succeed at.", "Stephen Hawking"),
        PhysicsQuote("It would not be much of a universe if it wasn't home to the people you love.", "Stephen Hawking"),
        PhysicsQuote("We are a way for the cosmos to know itself.", "Carl Sagan"),
        PhysicsQuote("If you wish to make an apple pie from scratch, you must first invent the universe.", "Carl Sagan"),
        PhysicsQuote("The cosmos is within us. We are made of star-stuff.", "Carl Sagan"),
        PhysicsQuote("What we observe is not nature itself, but nature exposed to our method of questioning.", "Werner Heisenberg"),
        PhysicsQuote("A physical law must possess mathematical beauty.", "Paul Dirac"),
        PhysicsQuote("Pick a flower on Earth and you move the farthest star.", "Paul Dirac"),
        PhysicsQuote("That is not only not right; it is not even wrong.", "Wolfgang Pauli"),
        PhysicsQuote("Where is everybody?", "Enrico Fermi"),
        PhysicsQuote("Spacetime tells matter how to move; matter tells spacetime how to curve.", "John Archibald Wheeler"),
        PhysicsQuote("As our island of knowledge grows, so does the shore of our ignorance.", "John Archibald Wheeler"),
        PhysicsQuote("More is different.", "Philip W. Anderson"),
        PhysicsQuote("Give me a place to stand and I will move the Earth.", "Archimedes"),
        PhysicsQuote("Life need not be easy, provided only that it is not empty.", "Lise Meitner"),
        PhysicsQuote("The black holes of nature are the most perfect macroscopic objects there are in the universe.", "Subrahmanyan Chandrasekhar"),
        PhysicsQuote("Science cannot solve the ultimate mystery of nature, because in the last analysis we ourselves are a part of the mystery.", "Max Planck"),
        PhysicsQuote("Science advances one funeral at a time.", "Max Planck"),
        PhysicsQuote("The present is theirs; the future, for which I really worked, is mine.", "Nikola Tesla"),
        PhysicsQuote("Science is built up of facts, as a house is of stones; but an accumulation of facts is no more a science than a heap of stones is a house.", "Henri Poincaré"),
        PhysicsQuote("The important thing in science is not so much to obtain new facts as to discover new ways of thinking about them.", "William Lawrence Bragg"),
        PhysicsQuote("Equipped with his five senses, man explores the universe around him and calls the adventure Science.", "Edwin Hubble"),
        PhysicsQuote("Science progresses best when observations force us to alter our preconceptions.", "Vera Rubin"),
        PhysicsQuote("The true logic of this world is in the calculus of probabilities.", "James Clerk Maxwell"),
        PhysicsQuote("When you can measure what you are speaking about, and express it in numbers, you know something about it.", "Lord Kelvin"),
        PhysicsQuote("It is difficult to say what is impossible, for the dream of yesterday is the hope of today and the reality of tomorrow.", "Robert H. Goddard"),
        PhysicsQuote("The good thing about science is that it's true whether or not you believe in it.", "Neil deGrasse Tyson"),
        PhysicsQuote("The universe is under no obligation to make sense to you.", "Neil deGrasse Tyson"),
    )
}
