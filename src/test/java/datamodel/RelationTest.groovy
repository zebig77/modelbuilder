package datamodel

class RelationTest extends GroovyTestCase {

   void testNew() {
      def r = new Relation("s","t")
      r.with {
         assert source_name == "s"
         assert target_name == "t"
         assert source_min == 1
         assert source_max == 1
      }
      assert r.toString() == "s -> t"

   }
}
