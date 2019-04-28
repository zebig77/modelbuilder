package datamodel

import javax.xml.crypto.Data

class DatamodelTest extends GroovyTestCase {

    void test_datamodel() {

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
            r("TV Series","Author").one_to_many()
            r("TV Series","Season").parent_child()
            r("Season", "Actor").one_to_many()
            r("Actor", "Season").zero_to_many()

            // Samples
            e("TV Series").s '{ "Name":"Game of Throne", "Genre":"Fantasy" }'
            e("TV Series").s '{ "Name":"Breaking Bad", "Genre":"Crime" }'
            e("TV Series").s '{ "Name":"Better Call Saul", "Genre":"Crime" }'

            e("Author").s    '{ "First name":"David", "Last name":"Benioff", "Birth date":"25/09/1970" }'
            e("Author").s    '{ "First name":"Daniel Brett", "Last name":"Weiss", "Birth date":"23/04/1971" }'
            e("Author").s    '{ "First name":"Vince", "Last name":"Gilligan", "Birth date":"10/02/1967" }'
            e("Author").s    '{ "First name":"Peter", "Last name":"Gould", "Birth date":"" }'

            e("Actor").s     '{ "First name":"Bob", "Last name":"Odenkirk", "Birth date":"22/10/1962" }'
            e("Actor").s     '{ "First name":"Jonathan", "Last name":"Banks", "Birth date":"31/01/1947" }'
            e("Actor").s     '{ "First name":"Rhea", "Last name":"Seehorn", "Birth date":"12/05/1972" }'
            e("Actor").s     '{ "First name":"Bryan", "Last name":"Cranston", "Birth date":"07/03/1956" }'
            e("Actor").s     '{ "First name":"Anna", "Last name":"Gunn", "Birth date":"11/08/1968" }'
            e("Actor").s     '{ "First name":"Kit", "Last name":"Harington", "Birth date":"26/12/1986" }'
            e("Actor").s     '{ "First name":"Peter", "Last name":"Dinklage", "Birth date":"11/06/1969" }'
            e("Actor").s     '{ "First name":"Emilia", "Last name":"Clarke", "Birth date":"23/10/1986" }'

            r("TV Series","Season").s '{ "Game of Throne", 1, "17/04/2011", 10 }'
            r("TV Series","Season").s '{ "Game of Throne", 2, "01/04/2012", 10 }'
            r("TV Series","Season").s '{ "Game of Throne", 3, "31/03/2013", 10 }'
            r("TV Series","Season").s '{ "Game of Throne", 4, "06/04/2014", 10 }'
            r("TV Series","Season").s '{ "Game of Throne", 5, "12/04/2015", 10 }'
            r("TV Series","Season").s '{ "Game of Throne", 6, "24/04/2016", 10 }'
            r("TV Series","Season").s '{ "Game of Throne", 7, "16/07/2017", 7 }'
            r("TV Series","Season").s '{ "Game of Throne", 8, "14/04/2019", 6 }'

            r("TV Series","Season").s '{ "Breaking Bad", 1, "20/01/2008", 7 }'
            r("TV Series","Season").s '{ "Breaking Bad", 2, "08/03/2009", 13 }'
            r("TV Series","Season").s '{ "Breaking Bad", 3, "21/03/2010", 13 }'
            r("TV Series","Season").s '{ "Breaking Bad", 4, "17/07/2011", 13 }'
            r("TV Series","Season").s '{ "Breaking Bad", 5, "15/07/2012", 16 }'

            r("TV Series","Season").s '{ "Better Call Saul", 1, "08/02/2015", 10 }'
            r("TV Series","Season").s '{ "Better Call Saul", 2, "15/02/2016", 10 }'
            r("TV Series","Season").s '{ "Better Call Saul", 3, "10/04/2017", 10 }'
            r("TV Series","Season").s '{ "Better Call Saul", 4, "06/08/2018", 10 }'

            (1..8).each { season ->
                r("Season", "Actor").s '{ "Game of Throne", '+season+', "Kit", "Harington" }'
                r("Season", "Actor").s '{ "Game of Throne", '+season+', "Peter", "Dinklage" }'
                r("Season", "Actor").s '{ "Game of Throne", '+season+', "Emilia", "Clarke" }'
            }

            (1..5).each { season ->
                r("Season", "Actor").s '{ "Breaking Bad", '+season+', "Bryan", "Cranston" }'
                r("Season", "Actor").s '{ "Breaking Bad", '+season+', "Anna", "Gunn" }'
                r("Season", "Actor").s '{ "Breaking Bad", '+season+', "Bob", "Odenkirk" }'
            }

            (1..4).each { season ->
                r("Season", "Actor").s '{ "Better Call Saul", $season, "Bob", "Odenkirk" }'
                r("Season", "Actor").s '{ "Better Call Saul", $season, "Rhea", "Seehorn" }'
            }

            assert validate()
        }

    }

}
