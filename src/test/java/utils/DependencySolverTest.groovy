package utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DependencySolverTest {

    @Test
    void simple_dep_ok() {
        def a = new DependencyNode(name: 'a')
        def b = new DependencyNode(name: 'b')
        def c = new DependencyNode(name: 'c')
        def d = new DependencyNode(name: 'd')
        def e = new DependencyNode(name: 'e')

        a.addEdge(b)    // a depends on b
        a.addEdge(d)    // a depends on d
        b.addEdge(c)    // b depends on c
        b.addEdge(e)    // b depends on e
        c.addEdge(d)    // c depends on d
        c.addEdge(e)    // c depends on e

        assert DependencySolver.dep_resolve(a) == [d, e, c, b, a]
    }

    @Test
    void circular_dep_detection() {
        def a = new DependencyNode(name: 'a')
        def b = new DependencyNode(name: 'b')
        def c = new DependencyNode(name: 'c')
        def d = new DependencyNode(name: 'd')
        def e = new DependencyNode(name: 'e')

        a.addEdge(b)    // a depends on b
        a.addEdge(d)    // a depends on d
        b.addEdge(c)    // b depends on c
        b.addEdge(e)    // b depends on e
        c.addEdge(d)    // c depends on d
        c.addEdge(e)    // c depends on e

        // circular d->b->c->d
        d.addEdge(b)

        // should fail
        Assertions.assertThrows(Exception.class, {
            DependencySolver.dep_resolve(a)
        })

    }
}
