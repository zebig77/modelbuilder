package datamodel

class Entity {

    final String name

    List<Property> properties = []
    String parent_entity_name
    def samples = []
    List<Relation> relations = []

    Entity(String name) {
        this.name = name
    }

    // property creation
    Property p(String property_name) {
        if (!properties.any {it.name == property_name}) {
            properties << new Property(this.name, property_name)
        }
        return properties.find {it.name == property_name}
    }

    List<Property> getKeys() {
        return properties.findAll {it.is_key}
    }

    // sample creation based on named columns
    void s(Object... sample) {
        samples << sample
    }

    // declare parent entity
    Entity has_parent(String entity_name) {
        this.parent_entity_name = entity_name
        return this
    }

    // declare a relation to another entity 1-N
    Relation has_one_to_many(String target_entity_name, String relation_name = "") {
        def r = new Relation(this.name, target_entity_name, relation_name)
        r.one_to_many()
        relations << r
        return r
    }

    // declare a relation to another entity 0-N
    Relation has_zero_to_many(String target_entity_name, String relation_name = "") {
        def r = new Relation(this.name, target_entity_name, relation_name)
        r.zero_to_many()
        relations << r
        return r
    }

}
