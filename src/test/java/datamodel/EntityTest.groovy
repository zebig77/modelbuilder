package datamodel

class EntityTest extends GroovyTestCase {

   void testNew() {
      new Entity("test").with {
         assert name == "test"
         assert properties == [:]
      }
   }

   void testAddProperty() {
      new Entity("test").with {
         p("Nom")
         p("Prénom")
         assert properties.size() == 2
      }
   }

   void testSetType() {
      new Entity("test").with {
         p "Age"
         assert p("Age").type == "string"
         p("Age").as_number()
         assert p("Age").type == "number"
         def dn = p("Date de naissance").as_date()
         assert dn.type == "date"
      }
   }

   void testUnique() {
      new Entity("test").with {
         p("Rang")
         assert !p("Rang").is_unique
         p("Rang").as_number().unique()
         assert p("Rang").is_unique
      }
   }

   void testSequence() {
      new Entity("test").with {
         p("Rang").as_sequence()
         assert p("Rang").is_sequence
         assert p("Rang").is_unique
         assert p("Rang").type == "number"
      }
   }

   void testKey() {
      new Entity("test").with {
         p("Rang").as_key()
         assert p("Rang").is_key
         assert !p("Rang").is_unique
      }
   }

   void testRelation() {
      new Entity("test").with {
         r("test2").one_to_many()
         assert relations.containsKey("test2")
         assert r("test2").target_min == "1"
         assert r("test2").target_max == "N"
      }
      new Entity("test2").with {
         r("test").as_parent().with {
            assert is_parent
            assert target_min == "1"
            assert target_max == "1"
         }
      }
   }

}
