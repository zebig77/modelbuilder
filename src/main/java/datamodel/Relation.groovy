package datamodel

class Relation {

    final String source_name // source entity name
    final String target_name // target entity name
    int source_min
    int source_max
    int target_min
    int target_max
    final static int N =  Integer.MAX_VALUE
    boolean parent_child = false
    def samples = []

    Relation(String source_name, String target_name) {
        this.source_name = source_name
        this.target_name = target_name
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

    // for many_to_many relationships
    void s(String jsonSample) {
        samples << jsonSample
    }

    String toString() {
        return "$source_name -> $target_name"
    }
}
