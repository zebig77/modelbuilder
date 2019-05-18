package utils

class DependencyNode {
    String name
    List<Node> edges = []

    void addEdge(DependencyNode n) {
        edges << n
    }

    String toString() { return name }
}
