package utils

/**
 * Based on the elegant algorithm described by Ferry Boender @
 * https://www.electricmonk.nl/docs/dependency_resolving_algorithm/dependency_resolving_algorithm.html
 */
class DependencySolver {

    // returns the less-dependant nodes first, most-dependant last
    // detects and rejects circular dependencies (ex: a->b->c->a)
    static List<Node> dep_resolve(DependencyNode node, List<DependencyNode> resolved = [], List<DependencyNode> unresolved = []) {
        unresolved << node
        node.edges.each { DependencyNode edge ->
            if (!resolved.contains(edge)) {
                if (unresolved.contains(edge)) {
                    throw new Exception("Circular reference detected '$node' -> '$edge'")
                }
                dep_resolve(edge, resolved, unresolved)
            }
        }
        resolved.add(node)
        unresolved.remove(node)
        return resolved
    }

}
