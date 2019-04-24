package datamodel

class EntityTest extends GroovyTestCase {

   void testNew() {
      def dg = new Entity("test")
      assert dg.name == "test"
      assert dg.properties == [:]
   }

   void testAddProperty() {
      def dg = new Entity("test")
      dg.with {
         p("Nom")
         p("Prénom")
      }
      assert dg.properties.size() == 2
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

}
