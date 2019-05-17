package datamodel.samples

import datamodel.Datamodel
import datamodel.Relation

class TVSeriesDatamodel extends Datamodel {

    TVSeriesDatamodel() {
        super("TV Series")
        build()
    }

    void build() {

        e("TV Genre").with {
            p("Name").as_key()
            p("Description").nullable()

            s "Fantasy", "A fantasy story is about magic or supernatural forces, rather than technology (as science fiction) if it happens to take place in a modern or future era"
            s "Crime", "A crime story is about a crime that is being committed or was committed. It can also be an account of a criminal's life."
        }

        e("Author").with {
            p("Name").as_key() // composite key
            p("Birth date").as_date('DD/MM/YYYY').nullable()

            s "David Benioff",      "25/09/1970"
            s "Daniel Brett Weiss", "23/04/1971"
            s "Vince Gilligan",     "10/02/1967"
            s "Peter Gould",        null
        }

        e("TV Series").with {
            p("Name").as_key()
            p("Genre").references("TV Genre") // foreign key
            s "Game of Throne",     "Fantasy"
            s "Breaking Bad",       "Crime"
            s "Better Call Saul",   "Crime"
            has_one_to_many("Author", "Written by").with {
                s "Game of Throne",     "David Benioff"
                s "Game of Throne",     "Daniel Brett Weiss"
                s "Breaking Bad",       "Vince Gilligan"
                s "Better Call Saul",   "Vince Gilligan"
                s "Better Call Saul",   "Peter Gould"
            }
        }

        e("Series Season").with {
            has_parent("TV Series")
            p("Series Name").as_parent_key()
            p("Number").as_number().as_key()
            p("Release date").as_date()
            p("Number of episodes").as_number()

            s "Game of Throne",     1, "17/04/2011", 10
            s "Game of Throne",     2, "01/04/2012", 10
            s "Game of Throne",     3, "31/03/2013", 10
            s "Game of Throne",     4, "06/04/2014", 10
            s "Game of Throne",     5, "12/04/2015", 10
            s "Game of Throne",     6, "24/04/2016", 10
            s "Game of Throne",     7, "16/07/2017", 7
            s "Game of Throne",     8, "14/04/2019", 6
            s "Breaking Bad",       1, "20/01/2008", 7
            s "Breaking Bad",       2, "08/03/2009", 13
            s "Breaking Bad",       3, "21/03/2010", 13
            s "Breaking Bad",       4, "17/07/2011", 13
            s "Breaking Bad",       5, "15/07/2012", 16
            s "Better Call Saul",   1, "08/02/2015", 10
            s "Better Call Saul",   2, "15/02/2016", 10
            s "Better Call Saul",   3, "10/04/2017", 10
            s "Better Call Saul",   4, "06/08/2018", 10
        }

        e("Actor").with {
            p("Name").as_key()
            p("Birth date").as_date('DD/MM/YYYY').nullable()
            s "Bob Odenkirk",       "22/10/1962"
            s "Jonathan Banks",     "31/01/1947"
            s "Rhea Seehorn",       "12/05/1972"
            s "Bryan Cranston",     "07/03/1956"
            s "Anna Gunn",          "11/08/1968"
            s "Kit Harington",      "26/12/1986"
            s "Peter Dinklage",     "11/06/1969"
            s "Emilia Clarke",      "23/10/1986"
            has_zero_to_many("Series Season", "Plays in").with { Relation r ->
                (1..8).each { season ->
                    r.s "Kit Harington", "Game of Throne", season
                    r.s "Peter Dinklage", "Game of Throne", season
                    r.s "Emilia Clarke", "Game of Throne", season
                }
                (1..5).each { season ->
                    r.s "Bryan Cranston", "Breaking Bad", season
                    r.s "Anna Gunn", "Breaking Bad", season
                    r.s "Bob Odenkirk", "Breaking Bad", season
                }
                (1..4).each { season ->
                    r.s "Bob Odenkirk", "Better Call Saul", season
                    r.s "Rhea Seehorn", "Better Call Saul", season
                }
            }
        }
    }
}


