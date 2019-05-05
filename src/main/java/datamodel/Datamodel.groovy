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
        def jsonSlurper = new JsonSlurper()
        entities.each { String e_name, Entity e ->
            def keys = []
            e.samples.each { jsonSample ->
                Map sample_map
                // check sample json format
                try {
                    def object = jsonSlurper.parseText(jsonSample as String)
                    if (!object instanceof Map) {
                        messages << "Invalid sample '$jsonSample' : not a map"
                        valid = false
                        return // next sample
                    }
                    sample_map = object as Map
                }
                catch (Exception json_e) {
                    messages << "Invalid sample '$jsonSample' : not a valid JSON"
                    valid = false
                    return // next sample
                }
                // check that sample property names match entity property names
                sample_map.each { p_name, p_value ->
                    if (!e.properties.containsKey(p_name)) {
                        messages << "Invalid sample '$jsonSample' : '$p_name' is not a property name"
                        valid = false
                    }
                }
                // check that mandatory entity properties have a sample value
                e.properties.each { String p_name, Property p ->
                    if (!p.is_nullable) {
                        if (!sample_map.containsKey(p_name)) {
                            messages << "Invalid sample '$jsonSample' : mandatory property '$p_name' has no sample value"
                            valid = false
                        }
                    }
                }
                // check that key values are unique
                def key = [:]
                sample_map.each { p_name, p_value ->
                    if (e.properties.get(p_name)?.is_key) {
                        key[p_name] = p_value
                    }
                }
                if (!key.isEmpty()) {
                    if (keys.any { it == key }) {
                        messages << "Invalid sample '$jsonSample' : duplicate key $key"
                        valid = false
                    } else {
                        keys << key
                    }
                }
            }
        }

        // TODO check relation samples

        if (!valid) {
            messages.each { logger.error(it) }
        }
        return valid
    }
}
