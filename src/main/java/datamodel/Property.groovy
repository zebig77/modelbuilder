package datamodel

class Property {

    final String name
    String type = "string" // default
    String instance_of
    boolean is_unique = false
    boolean is_key = false
    boolean is_sequence = false

    Property(String name) {
        this.name = name
    }

    Property as_number() {
        this.type = "number"
        return this
    }

    Property as_date() {
        this.type = "date"
        return this
    }

    Property instance_of(entity_name) {
        this.instance_of = entity_name
        return this
    }

    Property unique() {
        this.is_unique = true
        return this
    }

    Property as_key() {
        this.is_key = true
        return this
    }

    Property as_sequence() {
        this.is_sequence = true
        this.type = "number"
        this.is_unique = true
        return this
    }

}


