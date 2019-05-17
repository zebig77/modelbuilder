package datamodel.relational

import datamodel.Datamodel
import org.junit.jupiter.api.Test

class TableTest {

    @Test
    void create_table_from_relation() {
        new Datamodel("Books").with {
            e("Author").with {
                p("Name").as_key()
                has_one_to_many("Book", "has_written")
            }
            e("Book").with {
                p("Title").as_key()
            }
            relational().with {
                def t = tables["HAS_WRITTEN"]
                assert t != null
                assert t.cols.size() == 2
                assert t.cols[0].name == "AUTHOR_NAME"
                assert t.cols[1].name == "BOOK_TITLE"
            }
        }
    }
}
