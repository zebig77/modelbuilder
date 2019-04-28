package datamodel

class RelationTest extends GroovyTestCase {

   void testNew() {
      new Relation("s","t").with {
         assert source_name == "s"
         assert target_name == "t"
         assert source_min == 1
         assert source_max == 1
      }
   }
}
