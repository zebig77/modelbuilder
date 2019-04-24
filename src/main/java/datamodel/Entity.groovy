package datamodel

class Entity {

    final String name

    Map<String,Property> properties = [:]
    Map<String,Relation> relations = [:]

    Entity(String name) {
        this.name = name
    }

    // property creation
    Property p(String property_name) {
        if (!properties.containsKey(property_name)) {
            properties[property_name] = new Property(property_name)
        }
        return properties[property_name]
    }

    // relation creation
    Relation r(String target_name) {
        if (!relations.containsKey(target_name)) {
            relations[target_name] = new Relation(target_name)
        }
        return relations[target_name]
    }

    void sample(Object ... values) {
        // TODO
    }

}
