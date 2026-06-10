package test;

import junit.framework.TestCase;
import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.impl.UndirectedGraph.UndirectedGraph;
import ucu.edu.aed.tda.grafo.model.impl.UndirectedGraph.UndirectedGraphAlgorithms;

import java.util.*;

public class UndirectedGraphTests extends TestCase {

    private UndirectedGraph<Integer, WeightedEdge> g;          // grafo del PPT (pentágono, pág. 8/10)
    private UndirectedGraphAlgorithms algos;

    @Override
    protected void setUp() {
        algos = new UndirectedGraphAlgorithms();
        g = new UndirectedGraph<>();
        g.agregarVertices(List.of(1, 2, 3, 4, 5, 6));
        g.agregarArista(1, 2, new WeightedEdge(6));
        g.agregarArista(1, 3, new WeightedEdge(1));
        g.agregarArista(1, 4, new WeightedEdge(5));
        g.agregarArista(2, 3, new WeightedEdge(5));
        g.agregarArista(2, 5, new WeightedEdge(3));
        g.agregarArista(3, 4, new WeightedEdge(5));
        g.agregarArista(3, 5, new WeightedEdge(6));
        g.agregarArista(3, 6, new WeightedEdge(4));
        g.agregarArista(4, 6, new WeightedEdge(2));
        g.agregarArista(5, 6, new WeightedEdge(6));
    }

    public void testCantidadVerticesYAristas() {
        assertEquals(6, g.cantidadDeVertices());
        assertEquals(10, g.cantidadDeAristas());
    }

    public void testAristaEsSimetrica() {
        assertTrue(g.existeArista(g.construirComparable(1), g.construirComparable(2)));
        assertTrue(g.existeArista(g.construirComparable(2), g.construirComparable(1)));
        assertNotNull(g.obtenerArista(2, 1));
    }

    public void testAdyacenciasIncluyenAmbosSentidos() {
        List<Edge<Integer, WeightedEdge>> ady = g.adyacencias(g.construirComparable(3));
        assertEquals(5, ady.size());
    }

    public void testNoAgregaAristaConVerticeInexistente() {
        assertFalse(g.agregarArista(1, 99, new WeightedEdge(1)));
    }

    public void testNoDuplicaVertice() {
        assertFalse(g.agregarVertice(1));
        assertEquals(6, g.cantidadDeVertices());
    }

    public void testEliminarAristaLaQuitaDeAmbosLados() {
        assertTrue(g.eliminarArista(g.construirComparable(1), g.construirComparable(2)));
        assertFalse(g.existeArista(g.construirComparable(1), g.construirComparable(2)));
        assertFalse(g.existeArista(g.construirComparable(2), g.construirComparable(1)));
        assertEquals(9, g.cantidadDeAristas());
    }

    public void testRemoverVerticeBorraSusAristas() {
        assertTrue(g.removerVertice(g.construirComparable(3))); // 3 tenía grado 5
        assertEquals(5, g.cantidadDeVertices());
        assertEquals(5, g.cantidadDeAristas()); // 10 - 5 incidentes
    }

    public void testEsConexo() {
        assertTrue(g.esConexo());
        UndirectedGraph<Integer, WeightedEdge> desconexo = new UndirectedGraph<>();
        desconexo.agregarVertices(List.of(1, 2, 3));
        desconexo.agregarArista(1, 2, new WeightedEdge(1));
        assertFalse(desconexo.esConexo()); // 3 queda aislado
    }

    public void testTieneCiclos() {
        assertTrue(g.tieneCiclos());
        UndirectedGraph<Integer, WeightedEdge> arbol = new UndirectedGraph<>();
        arbol.agregarVertices(List.of(1, 2, 3));
        arbol.agregarArista(1, 2, new WeightedEdge(1));
        arbol.agregarArista(2, 3, new WeightedEdge(1));
        assertFalse(arbol.tieneCiclos());
    }

    public void testVaciar() {
        g.vaciar();
        assertEquals(0, g.cantidadDeVertices());
        assertEquals(0, g.cantidadDeAristas());
    }


    private double costoTotal(IUndirectedGraph<Integer, WeightedEdge> arbol) {
        double total = 0;
        for (Edge e : arbol.aristas()) {
            total += ((WeightedEdge) e.dato()).getWeight();
        }
        return total;
    }

    public void testKruskalAAM() {
        IUndirectedGraph<Integer, WeightedEdge> aam = algos.kruskal(g);
        assertEquals(6, aam.cantidadDeVertices());
        assertEquals(5, aam.cantidadDeAristas());
        assertEquals(15.0, costoTotal(aam));
        assertFalse(aam.tieneCiclos());
        assertTrue(aam.esConexo());
    }

    public void testPrimAAM() {
        IUndirectedGraph<Integer, WeightedEdge> aam = algos.prim(g, g.construirComparable(1));
        assertEquals(6, aam.cantidadDeVertices());
        assertEquals(5, aam.cantidadDeAristas());
        assertEquals(15.0, costoTotal(aam));
        assertFalse(aam.tieneCiclos());
        assertTrue(aam.esConexo());
    }

    public void testPrimYKruskalMismoCosto() {
        assertEquals(costoTotal(algos.kruskal(g)),
                costoTotal(algos.prim(g, g.construirComparable(1))));
    }

    public void testPrimOrigenInexistente() {
        assertNull(algos.prim(g, g.construirComparable(99)));
    }

    public void testSearchMinEdge() {
        Set<Integer> u = new HashSet<>(List.of(1));
        Set<Integer> v = new HashSet<>(List.of(2, 3, 4, 5, 6));
        Edge<Integer, WeightedEdge> min = algos.searchMinEdge(g, u, v);
        assertNotNull(min);
        assertEquals(1.0, min.dato().getWeight());
    }

    public void testBeaVisitaTodos() {
        List<Integer> visitados = new ArrayList<>();
        algos.bea(g, visitados::add);
        assertEquals(6, visitados.size());
        assertTrue(visitados.containsAll(List.of(1, 2, 3, 4, 5, 6)));
    }

    public void testKruskalSegundoGrafo() {
        UndirectedGraph<String, WeightedEdge> h = new UndirectedGraph<>();
        h.agregarVertices(List.of("A", "B", "C", "D", "E", "F", "G"));
        h.agregarArista("A", "B", new WeightedEdge(5));
        h.agregarArista("A", "D", new WeightedEdge(3));
        h.agregarArista("B", "D", new WeightedEdge(2));
        h.agregarArista("D", "E", new WeightedEdge(7));
        h.agregarArista("D", "F", new WeightedEdge(4));
        h.agregarArista("E", "F", new WeightedEdge(5));
        h.agregarArista("E", "G", new WeightedEdge(2));
        h.agregarArista("F", "G", new WeightedEdge(9));
        h.agregarArista("F", "C", new WeightedEdge(1));
        h.agregarArista("G", "C", new WeightedEdge(3));

        IUndirectedGraph<String, WeightedEdge> aam = algos.kruskal(h);
        assertEquals(7, aam.cantidadDeVertices());
        assertEquals(6, aam.cantidadDeAristas());
        assertFalse(aam.tieneCiclos());
        assertTrue(aam.esConexo());

        double total = 0;
        for (Edge e : aam.aristas()) total += ((WeightedEdge) e.dato()).getWeight();
        assertEquals(15.0, total);
        double primTotal = 0;
        for (Edge e : algos.prim(h, h.construirComparable("A")).aristas())
            primTotal += ((WeightedEdge) e.dato()).getWeight();
        assertEquals(15.0, primTotal);
    }
}