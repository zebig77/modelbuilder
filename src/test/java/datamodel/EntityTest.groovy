package datamodel

class EntityTest extends GroovyTestCase {

   void testNew() {
      new Entity("test").with {
         assert name == "test"
         assert p_names == []
         assert p_map == [:]
      }
   }

   void testAddProperty() {
      new Entity("test").with {
         p("Nom")
         p("Prénom")
         assert p_names.size() == 2
         assert p_map.size() == 2
         assert p_names.contains("Nom")
         assert p_names.contains("Prénom")
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

   void testNullable() {
      new Entity("test").with {
         p("A")
         assert !p("A").is_nullable
         p("B").nullable()
         assert p("B").is_nullable
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

   void testReference() {
      new Entity("test").with {
         assertNull( p("dummy").instance_of )
         p("FK").instance_of("Foreign Key")
         assert p("FK").instance_of == "Foreign Key"
      }
   }

}
