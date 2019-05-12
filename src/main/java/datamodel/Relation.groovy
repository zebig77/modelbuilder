package datamodel

import datamodel.relational.Table

class Relation {

    final String source_name // source entity name
    final String target_name // target entity name
    final String relation_name
    int source_min
    int source_max
    int target_min
    int target_max
    final static int N =  Integer.MAX_VALUE
    boolean parent_child = false
    def samples = []

    Relation(String source_name, String target_name, String relation_name) {
        this.source_name = source_name
        this.target_name = target_name
        if (relation_name == "") {
            this.relation_name = Table.normalize("$source_name to $target_name")
        }
        else {
            this.relation_name = Table.normalize(relation_name)
        }
        source_min = 1
        source_max = 1
    }

    Relation one_to_many() {
        target_min = 1
        target_max = N
        return this
    }

    Relation zero_to_many() {
        target_min = 0
        target_max = N
        return this
    }

    Relation parent_child() {
        one_to_many()
        this.parent_child = true
        return this
    }

    // for ...to_many relationships
    void s(Object... sample) {
        if (target_max != N) {
            throw new Exception("Invalid sample '$sample' : only 'X to many' relationships may have samples")
        }
        samples << sample
    }

    String toString() {
        return relation_name
    }
}
