package datamodel

class Entity {

    final String name

    def p_names = []
    Map<String,Property> p_map = [:]
    def samples = []

    Entity(String name) {
        this.name = name
    }

    // property creation
    Property p(String property_name) {
        if (!p_map.containsKey(property_name)) {
            p_names << property_name
            p_map[property_name] = new Property(property_name)
        }
        return p_map[property_name]
    }

    // sample creation
    void s(String jsonSample) {
        samples << jsonSample
    }

}
