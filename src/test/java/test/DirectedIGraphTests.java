package test;

import junit.framework.TestCase;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.impl.DirectedIGraph;

import java.util.List;
import java.util.Set;

public class DirectedIGraphTests extends TestCase {

    private DirectedIGraph<Integer, WeightedEdge> g;

    @Override
    protected void setUp() {
        g = new DirectedIGraph<>();
        g.agregarVertices(List.of(1, 2, 3));
        g.agregarArista(1, 2, new WeightedEdge(8));
        g.agregarArista(2, 1, new WeightedEdge(3));
        g.agregarArista(3, 2, new WeightedEdge(2));
        g.agregarArista(1, 3, new WeightedEdge(5));
    }

    public void testAgregarVerticeNoDuplica() {
        assertFalse(g.agregarVertice(1));
        assertEquals(3, g.cantidadDeVertices());
    }

    public void testAgregarVerticeNuloRetornaFalse() {
        assertFalse(g.agregarVertice(null));
    }

    public void testNoPermiteAristaConVerticeInexistente() {
        assertFalse(g.agregarArista(1, 99, new WeightedEdge(1)));
    }

    public void testSuccessors() {
        assertEquals(Set.of(2, 3), g.successors(g.construirComparable(1)));
    }

    public void testPredecessors() {
        assertEquals(Set.of(1, 3), g.predecessors(g.construirComparable(2)));
    }

    public void testGrados() {
        assertEquals(2, g.gradoDeSalida(g.construirComparable(1)));
        assertEquals(2, g.gradoDeEntrada(g.construirComparable(2)));
    }

    public void testExisteArista() {
        assertTrue(g.existeArista(g.construirComparable(1), g.construirComparable(2)));
        assertFalse(g.existeArista(g.construirComparable(3), g.construirComparable(1)));
    }

    public void testEliminarArista() {
        assertTrue(g.eliminarArista(g.construirComparable(1), g.construirComparable(2)));
        assertFalse(g.existeArista(g.construirComparable(1), g.construirComparable(2)));
    }

    public void testRemoverVertice() {
        assertTrue(g.removerVertice(g.construirComparable(2)));
        assertEquals(2, g.cantidadDeVertices());
        assertFalse(g.existeArista(g.construirComparable(1), g.construirComparable(2)));
    }

    public void testTieneCiclos() {
        assertTrue(g.tieneCiclos());
    }

    public void testGrafoAciclicoNoTieneCiclos() {
        DirectedIGraph<Integer, WeightedEdge> dag = new DirectedIGraph<>();
        dag.agregarVertices(List.of(1, 2, 3));
        dag.agregarArista(1, 2, new WeightedEdge(1));
        dag.agregarArista(2, 3, new WeightedEdge(1));
        assertFalse(dag.tieneCiclos());
    }

    public void testVaciar() {
        g.vaciar();
        assertEquals(0, g.cantidadDeVertices());
    }
}