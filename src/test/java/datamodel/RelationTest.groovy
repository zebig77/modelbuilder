package datamodel

import junit.framework.TestCase
import org.junit.Test

class RelationTest extends GroovyTestCase {

   void test_new_relation() {
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
