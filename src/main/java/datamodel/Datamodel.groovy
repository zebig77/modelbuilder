package datamodel

import groovy.json.JsonSlurper
import org.apache.log4j.Logger

class Datamodel {

    final String model_name
    static final Logger logger = Logger.getLogger(Datamodel.class)


    Map<String, Entity> entities = [:] // entity maps
    Map<String, Relation> relations = [:] // relation maps

    Datamodel(String model_name) {
        this.model_name = model_name
    }

    // entity creation
    Entity e(String entity_name) {
        if (!entities.containsKey(entity_name)) {
            entities[entity_name] = new Entity(entity_name)
        }
        return entities[entity_name]
    }

    Relation r(String source_entity_name, String target_entity_name) {
        String relation_name = "$source_entity_name -> $target_entity_name"
        if (!relations.containsKey(relation_name)) {
            relations[relation_name] = new Relation(source_entity_name,target_entity_name)
        }
        return relations[relation_name]
    }

    boolean validate(messages = []) {

        boolean valid = true

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

        // Check the samples
        entities.each { String e_name, Entity e ->
            def keys = []
            e.samples.each { sample ->
                // check that sample size = properties
                if (sample.size() != e.p_names.size()) {
                    messages << "Invalid sample '$sample' : expecting a sample with ${e.p_names.size()} values"
                    valid = false
                    return // next sample no need to check the rest
                }
                // check that mandatory properties have a sample value
                for(int i = 0; i < e.p_names.size(); i++) {
                    String p_name = e.p_names[i]
                    Property p = e.properties[p_name]
                    if (!p.is_nullable && sample[i] == null) {
                        messages << "Invalid sample '$sample' : mandatory property '$p_name' has no sample value"
                        valid = false
                    }
                }
                // check that key values are unique
                def key = [:]
                for(int i = 0; i < e.p_names.size(); i++) {
                    String p_name = e.p_names[i]
                    def p_value = sample[i]
                    if (e.properties.get(p_name).is_key) {
                        key[p_name] = p_value
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
                // TODO check value type
            }
        }

        // TODO check relation samples
        relations.each { r_name, r ->


        }

        if (!valid) {
            messages.each { logger.error(it) }
        }
        return valid
    }
}
