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
            e.samples.each { jsonSample ->
                Map sample_map
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
                sample_map.each { p_name, p_value ->
                    if (!e.p_map.containsKey(p_name)) {
                        messages << "Invalid sample '$jsonSample' : '$p_name' is not a property name"
                        valid = false
                        return
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
