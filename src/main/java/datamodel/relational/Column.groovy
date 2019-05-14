package datamodel.relational

import datamodel.Property

class Column {

    final String name
    final String type = "string" // default
    String foreign_key_table
    String foreign_key_col
    boolean is_unique = false
    boolean is_key = false
    boolean is_parent_key = false
    boolean is_sequence = false
    boolean is_nullable = false
    String format

    Column(String prefix="", Property p) {
        this.name = Table.normalize(prefix+p.name)
        this.type = p.type
        this.is_key = p.is_key
        this.is_parent_key = p.is_parent_key
    }

}
