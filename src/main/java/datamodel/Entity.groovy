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

    // sample creation with json input s('{ "A":1, "B":"deux", etc. }')
    void s(String jsonSample) {
        samples << jsonSample
    }

    // sample creation with named arguments s( A:1, B:"deux", etc. )
    void s(Map args) {
        StringBuffer sb = new StringBuffer()
        boolean first = true
        args.each { k, v ->
            if (!first) { sb << ', '  }
            first = false
            sb << '"'+k+'":"'+v.toString()+'"'
        }
        s("{ ${sb.toString()} }")
    }

}
