package datamodel

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.fail

class EntityTest {

    @Test
    void new_entity() {
        new Entity("test").with {
            assert name == "test"
            assert properties == []
        }
    }

    @Test
    void add_property() {
        new Entity("test").with {
            p("Nom")
            p("Prénom")
            assert properties.size() == 2
            assert properties.any { it.name == "Nom" }
            assert properties.any { it.name == "Prénom" }
        }
    }

    @Test
    void set_type() {
        new Entity("test").with {
            p "Age"
            assert p("Age").type == "string"
            p("Age").as_number()
            assert p("Age").type == "number"
            def dn = p("Date de naissance").as_date("DD/MM/YYYY")
            assert dn.type == "date"
            assert dn.format == "DD/MM/YYYY"
        }
    }

    @Test
    void test_date() {
        new Entity("test").with {
            p("date1").with {
                as_date()
                assert type == "date"
                assert format == "dd/MM/yyyy"
            }
            p("date2").with {
                as_date("dd/MM HH:mm:ss")
                assert format == "dd/MM HH:mm:ss"
            }
            try {
                p("date3").with {
                    as_date("Bozo Le Clown")
                    fail("should have detected invalid date format")
                }
            }
            catch (Exception err) {
                assert err.message.contains("Illegal pattern")
            }
        }
    }

    @Test
    void test_unique() {
        new Entity("test").with {
            p("Rang")
            assert !p("Rang").is_unique
            p("Rang").as_number().unique()
            assert p("Rang").is_unique
        }
    }

    @Test
    void test_nullable() {
        new Entity("test").with {
            p("A")
            assert !p("A").is_nullable
            p("B").nullable()
            assert p("B").is_nullable
        }
    }

    @Test
    void test_sequence() {
        new Entity("test").with {
            p("Rang").as_sequence()
            assert p("Rang").is_sequence
            assert p("Rang").is_unique
            assert p("Rang").type == "number"
        }
    }

    @Test
    void test_key() {
        new Entity("test").with {
            p("Rang").as_key()
            assert p("Rang").is_key
            assert !p("Rang").is_unique
            p("K2").as_key()
            p("X") // non-key
            assert keys == [p("Rang"), p("K2")]
        }
    }

    @Test
    void test_reference() {
        new Entity("test").with {
            assertNull(p("dummy").instance_of)
            p("FK").references("Foreign Key")
            assert p("FK").instance_of == "Foreign Key"
        }
    }

    @Test
    void test_good_sample() {
        new Entity("test").with {
            p("A")
            p("B")
            s "1X", "2Y"
            s "2Z", "3W"
            assert samples.size() == 2
            assert samples[0][0] == "1X"
            assert samples[0][1] == "2Y"
            assert samples[1][0] == "2Z"
            assert samples[1][1] == "3W"
        }
    }

}