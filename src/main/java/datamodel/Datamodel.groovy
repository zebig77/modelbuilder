package datamodel

class Datamodel {

    final String model_name

    def entities = []
    def relations = []

    Datamodel(String model_name) {
        this.model_name = model_name
    }

    // entity creation
    Entity e(String entity_name) {
        def new_e = new Entity(entity_name)
        entities << new_e
        return new_e
    }

    Relation r(String source_entity_name, String target_entity_name) {
        def new_r = new Relation(source_entity_name,target_entity_name)
        relations << new_r
        return new_r
    }
}
