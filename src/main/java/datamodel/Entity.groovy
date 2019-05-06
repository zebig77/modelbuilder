package datamodel

class Entity {

    final String name

    List<Property> properties = []
    def samples = []

    Entity(String name) {
        this.name = name
    }

    // property creation
    Property p(String property_name) {
        if (!properties.any {it.name == property_name}) {
            properties << new Property(property_name)
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

}
