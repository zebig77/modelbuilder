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
    // in a parent-child relation, this child property derives from a parent key
    boolean is_parent_key = false

    Property(String name) {
        this.name = name
    }

    void setType(String t) {
        if (!["string","number","integer","boolean","date"].contains(t)) {
            throw new Exception("Invalid type '$t'")
        }
    }

    // any kind of numeric value
    Property as_number() {
        this.type = "number"
        return this
    }

    Property as_integer() {
        this.type = "integer"
        return this
    }

    Property as_boolean() {
        this.type = "boolean"
        return this
    }

    Property as_date(String date_format = "dd/MM/yyyy") {
        this.type = "date"
        new SimpleDateFormat(date_format) // check is valid
        this.format = date_format
        return this
    }

    Property references(entity_name) {
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

    Property as_parent_key() {
        this.is_parent_key = true
        return this
    }

    Property as_sequence() {
        this.is_sequence = true
        this.type = "number"
        this.is_unique = true
        return this
    }

}


