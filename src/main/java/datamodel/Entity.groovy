package datamodel

class Entity {

    final String name

    Map<String,Property> properties = [:]

    Entity(String dg_name) {
        this.name = dg_name
    }

    // property creation
    Property p(String dgp_name) {
        if (!properties.containsKey(dgp_name)) {
            properties[dgp_name] = new Property(dgp_name)
        }
        return properties[dgp_name]
    }

    // relation creation
    Relation r(String ename) {

    }


}
