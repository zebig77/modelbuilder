package datamodel

class Relation {

    final String target_name // target entity name
    String target_min // 0,1,N
    String target_max // 1,N
    boolean is_parent = false

    Relation(String target_name) {
        this.target_name = target_name
    }

    Relation one_to_many() {
        target_min = "1"
        target_max = "N"
        return this
    }

    Relation as_parent() {
        is_parent = true
        target_min = "1"
        target_max = "1"
        return this
    }
}
