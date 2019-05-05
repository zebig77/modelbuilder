package datamodel

import org.apache.log4j.BasicConfigurator
import org.junit.Before
import org.junit.Test

class DatamodelTest {

    @Test
    void bad_relation() {
        new Datamodel("bad_relation").with {
            r("E1", "E2")
            def errors = []
            assert !validate(errors)
            assert errors.size() == 2
            assert errors.any { it.contains("Relation E1 -> E2 refers to non-existent source entity 'E1'") }
            assert errors.any { it.contains("Relation E1 -> E2 refers to non-existent target entity 'E2'") }
        }
    }

    @Test
    void good_map_sample() {
        new Datamodel("good_map_sample").with {
            e("E1").with {
                p("AAA")
                p("BB")
                s 42, "Brigitte Bardot"
                assert samples.size() == 1
                assert samples[0] == [42, "Brigitte Bardot"]
            }
            assert validate()
        }
    }

    @Test
    void good_list_sample() {
        new Datamodel("good_list_sample").with {
            e("E1").with {
                p("AAA")
                p("BB")
                s 42, "Brigitte Bardot"
                s 54, "Didier Legrand"
                assert samples.size() == 2
                assert samples[0] == [42, "Brigitte Bardot"]
                assert samples[1] == [54, "Didier Legrand"]
            }
            assert validate()
        }
    }

    @Test
    void bad_sample_missing_value() {
        new Datamodel("bad_sample_missing_value").with {
            e("E1").with {
                p("A")
                p("B").nullable()
                p("C")
                s(1, null, "trois") // should cause no problem
                s(2, "BBB") // one is missing
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("expecting a sample with 3 values") }
            assert errors.size() == 1
        }
    }

    @Test
    void bad_sample_duplicate_keys() {
        new Datamodel("bad_sample_duplicate_keys").with {
            e("E1").with {
                p("A").as_key()
                p("B").as_key()
                p("C")
                s 1, "2", "XXX"
                s 1, "22", "ZZZ"
                s 1, "2", "YYY" // duplicate key
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("duplicate key [A:1, B:2]") }
            assert errors.size() == 1
        }
    }

    @Test
    void bad_sample_date_format() {
        new Datamodel("bad_sample_date_format").with {
            e("E1").with {
                p("A")
                p("D").as_date("dd/MM/yyyy")
                s 1, "10/06/2000" // ok
                s 2, "10/06+2000"  // ko
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("expected format") }
            assert errors.size() == 1
        }
    }

    @Test
    void bad_sample_number_format() {
        new Datamodel("bad_sample_date_format").with {
            e("E1").with {
                p("N").as_number()
                s 1  // ok
                s 1.2 // ok
                s(1.22f) // ok
                s(-10) // ok
                s "XX" // ko
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("'XX' is not a number") }
            assert errors.size() == 1
        }
    }

    // TODO relation samples

    @Test
    void good_datamodel() {

        new Datamodel("TV Series").with {

            // Entities
            e("TV Series").with {
                p("Name").as_key()
                p("Genre").instance_of("TV Genre") // foreign key
            }
            e("Author").with {
                p("Name").as_key() // composite key
                p("Birth date").as_date('DD/MM/YYYY').nullable()
            }
            e("Actor").with {
                p("Name").as_key()
                p("Birth date").as_date('DD/MM/YYYY').nullable()
            }
            e("Season").with {
                p("Season number").as_number().as_key()
                p("Release date").as_date()
                p("Number of episodes").as_number()
            }
            e("TV Genre").with {
                p("Name").as_key()
                p("Description").nullable()
            }

            // Relations
            r("TV Series", "Author").one_to_many()
            r("TV Series", "Season").parent_child()
            r("Season", "Actor").one_to_many()
            r("Actor", "Season").zero_to_many()

            // Samples
            e("TV Series").with {
                s "Game of Throne",     "Fantasy"
                s "Breaking Bad",       "Crime"
                s "Better Call Saul",   "Crime"
            }

            e("Author").with {
                s "David Benioff",      "25/09/1970"
                s "Daniel Brett Weiss", "23/04/1971"
                s "Vince Gilligan",     "10/02/1967"
                s "Peter Gould",        null
            }

            e("Actor").with {
                s "Bob Odenkirk",       "22/10/1962"
                s "Jonathan Banks",     "31/01/1947"
                s "Rhea Seehorn",       "12/05/1972"
                s "Bryan Cranston",     "07/03/1956"
                s "Anna Gunn",          "11/08/1968"
                s "Kit Harington",      "26/12/1986"
                s "Peter Dinklage",     "11/06/1969"
                s "Emilia Clarke",      "23/10/1986"
            }

            r("TV Series", "Season").with {
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

            r("Season", "Actor").with {
                (1..8).each { season ->
                    s "Game of Throne", season, "Kit Harington"
                    s "Game of Throne", season, "Peter Dinklage"
                    s "Game of Throne", season, "Emilia Clarke"
                }
                (1..5).each { season ->
                    s "Breaking Bad", season, "Bryan Cranston"
                    s "Breaking Bad", season, "Anna Gunn"
                    s "Breaking Bad", season, "Bob Odenkirk"
                }
                (1..4).each { season ->
                    s "Better Call Saul", season, "Bob Odenkirk"
                    s "Better Call Saul", season, "Rhea Seehorn"
                }
            }

            assert validate()
        }

    }

}
