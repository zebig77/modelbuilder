package datamodel

class Datamodel {

    def datagroups = [:]

    // entity creation
    Entity e(String entity_name) {
        datagroups[entity_name] = new Entity(entity_name)
    }
}
