package datamodel.relational

import datamodel.Datamodel
import org.junit.jupiter.api.Test
import persistence.sqlite.SQLiteDB

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
            e("PARENT").with {
                p("PKEY").as_key()
                p("PCOL").as_number()
            }
            e("CHILD").with {
                p("PKEY").as_parent_key()
                p("CKEY").as_key()
                p("CCOL")
                has_parent("PARENT")
            }
            relational().with {
                assert tables.size() == 2
                def t1 = tables["PARENT"]
                def t2 = tables["CHILD"]
                assert t1.cols.size() == 2
                assert t2.cols.size() == 3
                Column t1k = t1.cols[0]
                Column t1c = t1.cols[1]
                assert t1c.type == "number"
                assert t1k.is_key
                Column t2t1k = t2.cols[0]
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
                assert tr.cols[0].name == "AUTHOR_NAME"
                assert tr.cols[1].name == "BOOK_TITLE"
            }
        }
    }

    @Test
    void test_parent_child() {
        new Datamodel("M4").with {
            e("Author").with {
                p("Name").as_key()
            }
            e("Book").with {
                p("Title").as_key()
                has_parent("Author")
            }
            relational().with {
                assert tables.size() == 2
                def tb = tables["BOOK"]
                assert tb != null
                assert tb.cols.size() == 2 // parent key (author name) + book key (title)
                assert tb.cols[0].name == "AUTHOR_NAME"
                assert tb.cols[1].name == "TITLE"
            }
        }
    }

    @Test
    void relational_to_sqlite() {
        new Datamodel("M5").with {
            e("Author").with {
                p("Name").as_key()
            }
            e("Book").with {
                p("Title").as_key()
                has_parent("Author")
            }
            def rm = relational()
            def db_file_name = "/tmp/relational_to_sqlite.db"
            def db = new SQLiteDB(db_file_name)
            db.create_schema(rm)
            // TODO injecter des données et relire
            // TODO vérifier la contrrainte d'intégrité père fils
        }
    }

}
