package datamodel

import org.junit.Test

class RelationTest {

   @Test
   void new_relation() {
      def r = new Relation("s","t")
      r.with {
         assert source_name == "s"
         assert target_name == "t"
         assert source_min == 1
         assert source_max == 1
      }
      assert r.toString() == "s -> t"
   }

   @Test
   void duplicate() {
      def r = new Relation("s","t")
      try {
         def r2 = new Relation("s","t2")
         fail("Should detect a duplicate")
      }
      catch (Exception e) {
         // OK
         println e
      }
   }

}
