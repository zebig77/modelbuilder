package datamodel

class DatamodelTest extends GroovyTestCase {

    void testNew() {
        new Datamodel().with {
            e("Série").with {
                p("Titre").as_key().unique()
                p("Genre").instance_of("Genre")
                p("Auteur")
                r("Saison").one_to_many()
                sample("Game of Throne","Heroic Fantasy","17/04/2011","David Benioff")
                sample("Breaking Bad", "Drame", "20/01/2008", "Vince Gilligan")
                sample("Better Call Saul", "Drame", "08/02/2015", "Vince Gilligan")
            }
            e("Saison").with {
                p("Numéro").as_sequence().as_key()
                p("Date de première diffusion").as_date()
                r("Série").as_parent()
                r("Acteur").one_to_many()
                sample("Game of Throne",1,"17/04/2011")
                sample("Breaking Bad",1,"20/01/2008")
                sample("Better Call Saul", 1, "08/02/2015")
            }
            e("Acteur").with {
                p("Nom").as_key()
                p("Prénom").as_key()
                p("Date de naissance").as_date()
                p("Date de décès").as_date().nullable()
                r("Saison").one_to_many().with {
                    // sample() TODO
                }
                sample("Harington", "Kit", "26/12/1986")
                sample("Dinklage", "Peter", "11/06/1969")
                sample("Clarke", "Emilia", "23/10/1986")
                sample("Cranston", "Bryan", "07/03/1956")
                sample("Gunn", "Anna", "11/08/1968")
            }
            e("Diffuseur")
            e("Genre").with {
                p("Genre").as_key().unique()
                sample("Heroic Fantasy")
                sample("Policier")
                sample("SF")
                sample("Drame")
            }
        }
    }


}
