package datamodel

class DatamodelTest extends GroovyTestCase {

   void testNew() {
      new Datamodel().with {
         e("Série").with {
            p("Titre")
            p("Genre").instance_of("Genre")
            p("Date de création").as_date()
            r("Saison").one_to_many()
         }
         e("Saison")
         e("Acteur")
         e("Diffuseur")
         e("Genre")
      }
   }


}
