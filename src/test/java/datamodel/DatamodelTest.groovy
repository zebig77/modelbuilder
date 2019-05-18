package datamodel

import datamodel.samples.TVSeriesDatamodel
import org.junit.jupiter.api.Test

class DatamodelTest {

    @Test
    void missing_properties() {
        new Datamodel("missing_props").with {
            e("E1")
            def errors = []
            assert !validate(errors)
            assert errors.size() == 1
            assert errors.any { it.contains("Invalid entity 'E1' : no property defined") }
        }
    }

    @Test
    void bad_relation_missing_target() {
        new Datamodel("bad_relation").with {
            e("E1").with {
                p("Key").as_key()
                has_one_to_many("E2")
            }
            def errors = []
            assert !validate(errors)
            assert errors.size() == 1
            assert errors.any { it.contains("relation 'E1_TO_E2' targets a non-existant entity 'E2'") }
        }
    }

    @Test
    void good_map_sample() {
        new Datamodel("good_map_sample").with {
            e("E1").with {
                p("AAA")
                p("BB")
                s 42, "Brigitte Bardot"
                assert samples.size() == 1
                assert samples[0] == [42, "Brigitte Bardot"]
            }
            assert validate()
        }
    }

    @Test
    void good_list_sample() {
        new Datamodel("good_list_sample").with {
            e("E1").with {
                p("AAA")
                p("BB")
                s 42, "Brigitte Bardot"
                s 54, "Didier Legrand"
                assert samples.size() == 2
                assert samples[0] == [42, "Brigitte Bardot"]
                assert samples[1] == [54, "Didier Legrand"]
            }
            assert validate()
        }
    }

    @Test
    void bad_sample_missing_value() {
        new Datamodel("bad_sample_missing_value").with {
            e("E1").with {
                p("A")
                p("B").nullable()
                p("C")
                s(1, null, "trois") // should cause no problem
                s(2, "BBB") // one is missing
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("expecting a sample with 3 values") }
            assert errors.size() == 1
        }
    }

    @Test
    void bad_sample_duplicate_keys() {
        new Datamodel("bad_sample_duplicate_keys").with {
            e("E1").with {
                p("A").as_key()
                p("B").as_key()
                p("C")
                s 1, "2", "XXX"
                s 1, "22", "ZZZ"
                s 1, "2", "YYY" // duplicate key
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("duplicate key '[A:1, B:2]'") }
            assert errors.size() == 1
        }
    }

    @Test
    void bad_sample_date_format() {
        new Datamodel("bad_sample_date_format").with {
            e("E1").with {
                p("A")
                p("D").as_date("dd/MM/yyyy")
                s 1, "10/06/2000" // ok
                s 2, "10/06+2000"  // ko
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("expected format") }
            assert errors.size() == 1
        }
    }

    @Test
    void bad_sample_number_format() {
        new Datamodel("bad_sample_date_format").with {
            e("E1").with {
                p("N").as_number()
                s 1  // ok
                s 1.2 // ok
                s(1.22f) // ok
                s(-10) // ok
                s "XX" // ko
            }
            def errors = []
            assert !validate(errors)
            assert errors.any { it.contains("'XX' is not a number") }
            assert errors.size() == 1
        }
    }

    @Test
    void relation_keys_and_samples() {
        new Datamodel("relation_keys_and_samples").with {

            e("E1").with {
                p("K1").as_key()
                p("NK1")
                has_one_to_many("E2").with {
                    // good sample
                    s "k1A", "k2A", "k3A"
                    s "k1B", "k2B", "k3B"
                }
            }
            e("E2").with {
                p("K2").as_key()
                p("K3").as_key()
                p("NK2")
            }
            e("E3").with {
                p("K4").as_key()
                p("NK3")
                has_parent("E1")
            }
            assert validate()

            // bad sample
            e("E1").relations[0].with { // to E2
                s "k1A", "k2A" // missing key K3
                s "k1B", "k2B", "k3B", "Boom" // one value in excess
            }

            List<String> errors = []
            assert !validate(errors)
            assert errors.size() == 2
            errors.each { String error_msg ->
                assert error_msg.contains("expecting a sample with 3 values")
            }
        }
    }

    @Test
    void good_datamodel() {
        new TVSeriesDatamodel().with {
            assert validate()
        }
    }

    @Test
    void dependecy_order() {
        new TVSeriesDatamodel().with {
            println getEntitiesByDependencyOrder()
        }
    }

}
