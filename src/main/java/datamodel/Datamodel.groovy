package datamodel

class Datamodel {

    def datagroups = [:]

    // entity creation
    Entity e(String dg_name) {
        datagroups[dg_name] = new Entity(dg_name)
    }
}
