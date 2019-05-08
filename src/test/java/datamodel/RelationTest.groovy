package datamodel

import org.junit.jupiter.api.Test

class RelationTest {

   @Test
   void new_relation() {
      new Datamodel("").with {
         r("s","t").with {
            assert source_name == "s"
            assert target_name == "t"
            assert source_min == 1
            assert source_max == 1
         }
         assert r("s","t").toString() == "s -> t"
      }
   }

}
