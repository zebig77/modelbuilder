package datamodel

import static org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class PropertyTest {

    @Test
    void new_property() {
        new Datamodel("new_property").with {
            e("FK").with {
                p("k").as_key()
                s "fk1"
            }
            e("E1").with {
                p("unique_key").as_key()
                p("foreign_key").references("FK")
                p("plain_string")
                p("number").as_number()
                p("integer").as_integer()
                p("date").as_date()
                p("date_time").as_date("dd/MM/yyyy HH:mm:ss")
                p("flag").as_boolean()
                p("maybe_unknown").nullable()
                s "k1", "fk1", "hello", 1.23, 42, "10/10/2019", "10/10/2019 15:32:11", false, null
                s "k2", "fk1", "world", -1.23, 0, "10/05/2019", "06/05/2019 15:32:11", true, "I am here"
            }
            assert validate()
        }
    }

    @Test
    void invalid_type() {
        new Datamodel("new_property").with {
            e("E").with {
                assertThrows(Exception.class, { p("p").type = "no good" })
            }
        }
    }

}
