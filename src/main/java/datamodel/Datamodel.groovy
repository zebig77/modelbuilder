package datamodel


import datamodel.relational.RelationalModel
import datamodel.relational.Table
import org.apache.log4j.Logger

import java.text.SimpleDateFormat

class Datamodel {

    final String model_name
    static final Logger logger = Logger.getLogger(Datamodel.class)

    Map<String, Entity> entities = [:]

    Datamodel(String model_name) {
        this.model_name = model_name
    }

    // entity creation or get
    Entity e(String entity_name) {
        if (!entities.containsKey(entity_name)) {
            entities[entity_name] = new Entity(entity_name)
        }
        return entities[entity_name]
    }

    boolean validate_entities(messages) {
        boolean valid = true
        entities.values().each { Entity e ->
            if (e.properties.isEmpty()) {
                messages << "Invalid entity '$e' : no property defined"
                valid = false
                return
            }
            e.relations.each { Relation r ->
                if (entities[r.target_name] == null) {
                    messages << "Invalid entity '$e' : relation '$r' targets a non-existant entity '$r.target_name'"
                    valid = false
                    return
                }
            }
        }
        return valid
    }


    boolean validate_samples(messages) {
        def valid = true
        def dm = this
        // Check entity samples
        entities.each { String e_name, Entity e ->
            def key_values = [] // all current key values for the sample
            // TODO manage several keysets
            e.samples.each { sample ->
                // check that sample size = properties
                if (sample.size() != e.properties.size()) {
                    messages << "Invalid sample '$sample' for entity '$e': expecting a sample with ${e.properties.size()} values"
                    valid = false
                    return // next sample no need to check the rest
                }
                // check that mandatory properties have a sample value
                for (int i = 0; i < e.properties.size(); i++) {
                    Property p = e.properties[i]
                    if (!p.is_nullable && sample[i] == null) {
                        messages << "Invalid sample '$sample' for entity '$e' : mandatory property '$p_name' has no sample value"
                        valid = false
                    }
                }
                // check that key values are unique
                def key_value = [:]
                for (int i = 0; i < e.properties.size(); i++) {
                    if (e.properties[i].is_key) {
                        key_value[e.properties[i].name] = sample[i]
                    }
                }
                if (!key_value.isEmpty()) {
                    if (key_values.any { it == key_value }) {
                        messages << "Invalid sample '$sample' for entity '$e' : duplicate key '$key_value'"
                        valid = false
                    } else {
                        key_values << key_value
                    }
                }
                // check sample value types
                for (int i = 0; i < e.properties.size(); i++) {
                    Property p = e.properties[i]
                    def sample_value = sample[i]
                    valid &= check_sample_value_type(p, sample_value, messages, "entity '$e'")
                }
            }
            // samples for ..to_many relationships (except parent-child where samples are provided with child)
            e.relations.each { Relation r ->
                if (r.target_max != Relation.N || r.parent_child) {
                    return // ignore
                }
                // collect source and target keys
                def r_keys = dm.entities[r.source_name].keys + dm.entities[r.target_name].keys
                r.samples.each {
                    List<Object> sample_values = it
                    if (sample_values.size() != r_keys.size()) {
                        messages << "Invalid sample '$sample_values' for relation '$r': expecting a sample with ${r_keys.size()} values"
                        valid = false
                        return // next sample no need to check the rest

                    }
                    // check sample value types
                    for (int i = 0; i < r_keys.size(); i++) {
                        Property p = r_keys[i]
                        def sample_value = sample_values[i]
                        valid &= check_sample_value_type(p, sample_value, messages, "relation '$r'")
                    }
                }
            }
        }

        return valid
    }

    // don't stop at the first problem, tries to report as many problems as possible
    boolean validate(messages = []) {
        def valid = validate_entities(messages)
        if (valid) {
            valid = validate_samples(messages)
        }
        if (!valid) {
            messages.each { logger.error(it) }
        }
        return valid
    }

    RelationalModel relational() {
        def dm = this
        def rm = new RelationalModel()
        entities.values().each { Entity e ->
            // create 1 table for each entity E
            def table_name = Table.normalize(e.name)
            if (e.parent_entity_name != null) {
                // child table, get parent keys
                def parent_keys = []
                getParentKeys(e.parent_entity_name, parent_keys)
                rm.tables[table_name] = new Table(parent_keys, e)
            } else {
                rm.tables[table_name] = new Table(e)
            }
            // create 1 table each time E has a "...to many" relation
            e.relations.find { it.target_max == Relation.N && !it.parent_child }.each { Relation r ->
                def r_keys = dm.entities[r.source_name].keys + dm.entities[r.target_name].keys
                def tr = new Table(r_keys, r)
                rm.tables[tr.table_name] = tr
            }
        }
        return rm
    }

    void getParentKeys(String parent_entity_name, List<Property> parent_keys = []) {
        Entity e = entities[parent_entity_name]
        if (e.parent_entity_name != null) {
            getParentKeys(e.parent_entity_name, parent_keys)
        }
        parent_keys << e.getKeys()
    }

    static boolean check_sample_value_type(Property p, Object sample_value, List messages, String context) {
        if (sample_value == null || p.type == "string") {
            return true
        }
        switch (p.type) {
            case "date":
                try {
                    new SimpleDateFormat(p.format).parse(sample_value.toString())
                }
                catch (Exception err) {
                    messages << "Invalid sample '$sample_value' for $context : expected format '${p.format}' for '${p.name}'"
                    return false
                }
                return true
            case "number":
                if (!(sample_value instanceof Number)) {
                    messages << "Invalid sample '$sample_value' : '$sample_value' is not a number"
                    return false
                }
                return true
            case "integer":
                if (!(sample_value instanceof Integer)) {
                    messages << "Invalid sample '$sample_value' : '$sample_value' is not an integer"
                    return false
                }
                return true
            case "boolean":
                if (!(sample_value instanceof Boolean)) {
                    messages << "Invalid sample '$sample_value' : '$sample_value' is not a boolean"
                    return false
                }
                return true
        }
        throw new Exception("Unknow data type for $p : $p.type")
    }


}
