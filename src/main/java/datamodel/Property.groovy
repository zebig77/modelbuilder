package datamodel

import java.text.SimpleDateFormat

class Property {

    final String name
    String type = "string" // default
    String instance_of
    boolean is_unique = false
    boolean is_key = false
    boolean is_sequence = false
    boolean is_nullable = false
    String format

    Property(String name) {
        this.name = name
    }

    Property as_number() {
        this.type = "number"
        return this
    }

    Property as_date(String date_format = "dd/MM/yyyy") {
        this.type = "date"
        new SimpleDateFormat(date_format) // check is valid
        this.format = date_format
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

    Property nullable() {
        this.is_nullable = true
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


