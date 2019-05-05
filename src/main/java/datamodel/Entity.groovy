package datamodel

class Entity {

    final String name

    def p_names = [] // record property definition order
    Map<String,Property> properties = [:]
    def samples = []

    Entity(String name) {
        this.name = name
    }

    // property creation
    Property p(String property_name) {
        if (!properties.containsKey(property_name)) {
            p_names << property_name
            properties[property_name] = new Property(property_name)
        }
        return properties[property_name]
    }

    // sample creation based on named columns
    void s(Object... sample) {
        samples << sample
    }

}
