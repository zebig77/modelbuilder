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
        // TODO validate_entities and properties
        return true
    }

    boolean validate_relations(messages) {
        def valid = true
        // check that relations refer to existing entities
        relations.each { String relation_name, Relation r ->
            if (!entities.containsKey(r.source_name)) {
                messages << "Relation $r refers to non-existent source entity '$r.source_name'"
                valid = false
            }
            if (!entities.containsKey(r.target_name)) {
                messages << "Relation $r refers to non-existent target entity '$r.target_name'"
                valid = false
            }
        }
        return valid

    }

    boolean validate_samples(messages) {
        def valid = true
        // Check entity samples
        entities.each { String e_name, Entity e ->
            def keys = []
            e.samples.each { sample ->
                // check that sample size = properties
                if (sample.size() != e.properties.size()) {
                    messages << "Invalid sample '$sample' : expecting a sample with ${e.properties.size()} values"
                    valid = false
                    return // next sample no need to check the rest
                }
                // check that mandatory properties have a sample value
                for (int i = 0; i < e.properties.size(); i++) {
                    Property p = e.properties[i]
                    if (!p.is_nullable && sample[i] == null) {
                        messages << "Invalid sample '$sample' : mandatory property '$p_name' has no sample value"
                        valid = false
                    }
                }
                // check that key values are unique
                def key = [:]
                for (int i = 0; i < e.properties.size(); i++) {
                    if (e.properties[i].is_key) {
                        key[e.properties[i].name] = sample[i]
                    }
                }
                if (!key.isEmpty()) {
                    if (keys.any { it == key }) {
                        messages << "Invalid sample '$sample' : duplicate key $key"
                        valid = false
                    } else {
                        keys << key
                    }
                }
                // check non-string types
                for (int i = 0; i < e.properties.size(); i++) {
                    Property p = e.properties[i]
                    def sample_value = sample[i]
                    if (p.type == "date" && sample_value != null) {
                        try {
                            new SimpleDateFormat(p.format).parse(sample_value.toString())
                        }
                        catch (Exception err) {
                            messages << "Invalid sample '$sample' : expected format '${p.format}' for '${p.name}'"
                            valid = false
                        }
                    }
                    if (p.type == "number" && sample_value != null) {
                        if (!(sample_value instanceof Number)) {
                            messages << "Invalid sample '$sample' : '$sample_value' is not a number"
                            valid = false
                        }
                    }
                }
            }
            // TODO validate relation samples
        }

        return valid
    }


    boolean validate(messages = []) {
        def valid = validate_entities(messages) & validate_relations(messages) & validate_samples(messages)
        if (!valid) {
            messages.each { logger.error(it) }
        }
        return valid
    }

    RelationalModel relational() {
        def dm = new RelationalModel()
        entities.values().each { Entity e ->
            dm.tables[Table.normalize(e.name)] = new Table(e)
            e.relations.each {
                if (it.target_max == Relation.N) {
                    dm.tables[Table.normalize(it.relation_name)] = new Table(it)
                }
            }
        }
        return dm
    }
}
