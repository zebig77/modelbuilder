package datamodel.relational

import datamodel.Entity
import datamodel.Property
import datamodel.Relation

class Table {

    final String table_name
    List<Column> cols = []

    Table(List<Property> parent_keys = [], Entity e) {
        table_name = normalize(e.name)
        parent_keys.each { Property p ->
            def c = new Column(Table.normalize(p.entity_name)+"_",p)
            c.is_parent_key = true
            cols << c
        }
        e.properties.each {Property p ->
            if (!p.is_parent_key) { // parent keys have already been added
                cols << new Column(p)
            }
        }
    }

    Table(Relation r) {
        table_name = normalize(r.relation_name)
        // TODO create columns
    }

    static String normalize(String s) {
        return s.replaceAll("[^A-Za-z0-9]", "_").toUpperCase()
    }

    String toString() {
        return table_name
    }
}
