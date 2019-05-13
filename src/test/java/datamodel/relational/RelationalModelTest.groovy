package datamodel.relational

import datamodel.Datamodel
import org.junit.jupiter.api.Test

class RelationalModelTest {

    @Test
    void simple_relational_model() {
        new Datamodel("M1").with {
            e("T1's table").with {
                p("C1's column")
            }
            relational().with {
                assert tables.size() == 1
                assert tables["T1_S_TABLE"] != null
                Table t = tables["T1_S_TABLE"]
                assert t.table_name == "T1_S_TABLE"
                assert t.cols.size() == 1
                Column c = t.cols[0]
                assert c.name == "C1_S_COLUMN"
                assert c.type == "string"
            }
        }
    }

    @Test
    void parent_child() {
        new Datamodel("M2").with {
            e("T1").with {
                p("T1K").as_key()
                p("T1C").as_number()
            }
            e("T2").with {
                p("T1K").as_parent_key()
                p("T2K").as_key()
                p("T2C")
                has_parent("T1")
            }
            relational().with {
                assert tables.size() == 2
                def t1 = tables["T1"]
                def t2 = tables["T2"]
                assert t1.cols.size() == 2
                assert t2.cols.size() == 3
                Column t1k = t1.cols[0]
                Column t1c = t1.cols[1]
                assert t1c.type == "number"
                assert t1k.is_key
                Column t2t1k = t2.cols[0]
                Column t2k = t2.cols[1]
                Column t2c = t2.cols[2]
                assert t2t1k.is_parent_key
            }
        }
    }

    @Test
    void test_one_to_many() {
        new Datamodel("M3").with {
            e("Author").with {
                p("Name").as_key()
                has_one_to_many("Book")
            }
            e("Book").with {
                p("Title").as_key()
            }
            relational().with {
                assert tables.size() == 3
                assert tables["AUTHOR_TO_BOOK"] != null
                def tr = tables["AUTHOR_TO_BOOK"]
                assert tr.cols.size() == 2 // author key + book key
            }
        }
    }

}
