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
    void bad_json_sample() {
        new Datamodel("bad_json_sample").with {
            e("E1").with {
                s("zboub")
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("not a valid JSON") }
            assert errors.size() == 1
        }
    }

    @Test
    void good_json_sample() {
        new Datamodel("good_json_sample").with {
            e("E1").with {
                p("AAA")
                p("BB")
                s('{ "AAA":42, "BB":"Brigitte Bardot" }')
                assert samples.size() == 1
                assert samples[0] == '{ "AAA":42, "BB":"Brigitte Bardot" }'
            }
            assert validate()
        }
    }

    @Test
    void good_named_args_sample() {
        new Datamodel("good_named_args_sample").with {
            e("E1").with {
                p("ABC")
                p("DEF")
                s(ABC: 1, DEF: "plouf")
                s(ABC: 2, DEF: "plouf plouf")
                assert samples.size() == 2
                assert samples[0] == '{ "ABC":"1", "DEF":"plouf" }'
                assert samples[1] == '{ "ABC":"2", "DEF":"plouf plouf" }'
            }
            assert validate()
        }
    }

    @Test
    void bad_sample_unknown_property() {
        new Datamodel("bad_sample_unknown_property").with {
            e("E1").with {
                p("A")
                p("B")
                s(A:"hello", B:"World") // should be OK
                s(A:"hello2", B:"OK", C:"my bad!") // should not be OK
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("'C' is not a property name") }
            assert errors.size() == 1
        }
    }

    @Test
    void bad_sample_missing_value() {
        new Datamodel("bad_sample_missing_value").with {
            e("E1").with {
                p("A")
                p("B").nullable()
                p("C")
                s(A:1, C:"trois") // should cause no problem
                s(A:2, B:"two") // C is missing
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("mandatory property 'C' has no sample value") }
            assert errors.size() == 1
        }
    }

    @Test
    void good_datamodel() {

        new Datamodel("TV Series").with {

            // Entities
            e("TV Series").with {
                p("Name").as_key()
                p("Genre").instance_of("TV Genre") // foreign key
            }
            e("Author").with {
                p("First name").as_key() // composite key
                p("Last name").as_key() // composite key
                p("Birth date").as_date().nullable()
            }
            e("Actor").with {
                p("First name").as_key()
                p("Last name").as_key()
                p("Birth date").as_date().nullable()
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
            e("TV Series").s '{ "Name":"Game of Throne", "Genre":"Fantasy" }'
            e("TV Series").s '{ "Name":"Breaking Bad", "Genre":"Crime" }'
            e("TV Series").s '{ "Name":"Better Call Saul", "Genre":"Crime" }'

            e("Author").s '{ "First name":"David", "Last name":"Benioff", "Birth date":"25/09/1970" }'
            e("Author").s '{ "First name":"Daniel Brett", "Last name":"Weiss", "Birth date":"23/04/1971" }'
            e("Author").s '{ "First name":"Vince", "Last name":"Gilligan", "Birth date":"10/02/1967" }'
            e("Author").s '{ "First name":"Peter", "Last name":"Gould", "Birth date":"" }'

            e("Actor").s '{ "First name":"Bob", "Last name":"Odenkirk", "Birth date":"22/10/1962" }'
            e("Actor").s '{ "First name":"Jonathan", "Last name":"Banks", "Birth date":"31/01/1947" }'
            e("Actor").s '{ "First name":"Rhea", "Last name":"Seehorn", "Birth date":"12/05/1972" }'
            e("Actor").s '{ "First name":"Bryan", "Last name":"Cranston", "Birth date":"07/03/1956" }'
            e("Actor").s '{ "First name":"Anna", "Last name":"Gunn", "Birth date":"11/08/1968" }'
            e("Actor").s '{ "First name":"Kit", "Last name":"Harington", "Birth date":"26/12/1986" }'
            e("Actor").s '{ "First name":"Peter", "Last name":"Dinklage", "Birth date":"11/06/1969" }'
            e("Actor").s '{ "First name":"Emilia", "Last name":"Clarke", "Birth date":"23/10/1986" }'

            r("TV Series", "Season").s '{ "Game of Throne", 1, "17/04/2011", 10 }'
            r("TV Series", "Season").s '{ "Game of Throne", 2, "01/04/2012", 10 }'
            r("TV Series", "Season").s '{ "Game of Throne", 3, "31/03/2013", 10 }'
            r("TV Series", "Season").s '{ "Game of Throne", 4, "06/04/2014", 10 }'
            r("TV Series", "Season").s '{ "Game of Throne", 5, "12/04/2015", 10 }'
            r("TV Series", "Season").s '{ "Game of Throne", 6, "24/04/2016", 10 }'
            r("TV Series", "Season").s '{ "Game of Throne", 7, "16/07/2017", 7 }'
            r("TV Series", "Season").s '{ "Game of Throne", 8, "14/04/2019", 6 }'

            r("TV Series", "Season").s '{ "Breaking Bad", 1, "20/01/2008", 7 }'
            r("TV Series", "Season").s '{ "Breaking Bad", 2, "08/03/2009", 13 }'
            r("TV Series", "Season").s '{ "Breaking Bad", 3, "21/03/2010", 13 }'
            r("TV Series", "Season").s '{ "Breaking Bad", 4, "17/07/2011", 13 }'
            r("TV Series", "Season").s '{ "Breaking Bad", 5, "15/07/2012", 16 }'

            r("TV Series", "Season").s '{ "Better Call Saul", 1, "08/02/2015", 10 }'
            r("TV Series", "Season").s '{ "Better Call Saul", 2, "15/02/2016", 10 }'
            r("TV Series", "Season").s '{ "Better Call Saul", 3, "10/04/2017", 10 }'
            r("TV Series", "Season").s '{ "Better Call Saul", 4, "06/08/2018", 10 }'

            (1..8).each { season ->
                r("Season", "Actor").s '{ "Game of Throne", ' + season + ', "Kit", "Harington" }'
                r("Season", "Actor").s '{ "Game of Throne", ' + season + ', "Peter", "Dinklage" }'
                r("Season", "Actor").s '{ "Game of Throne", ' + season + ', "Emilia", "Clarke" }'
            }

            (1..5).each { season ->
                r("Season", "Actor").s '{ "Breaking Bad", ' + season + ', "Bryan", "Cranston" }'
                r("Season", "Actor").s '{ "Breaking Bad", ' + season + ', "Anna", "Gunn" }'
                r("Season", "Actor").s '{ "Breaking Bad", ' + season + ', "Bob", "Odenkirk" }'
            }

            (1..4).each { season ->
                r("Season", "Actor").s '{ "Better Call Saul", $season, "Bob", "Odenkirk" }'
                r("Season", "Actor").s '{ "Better Call Saul", $season, "Rhea", "Seehorn" }'
            }

            assert validate()
        }

    }

}
