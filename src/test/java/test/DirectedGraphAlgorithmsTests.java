package test;

import junit.framework.TestCase;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.impl.DirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.impl.DirectedIGraph;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;

import java.util.List;

public class DirectedGraphAlgorithmsTests extends TestCase {

    private DirectedGraphAlgorithms algos;
    private DirectedIGraph<Integer, WeightedEdge> grafoFloydPPT;

    @Override
    protected void setUp() {
        algos = new DirectedGraphAlgorithms();
        grafoFloydPPT = new DirectedIGraph<>();
        grafoFloydPPT.agregarVertices(List.of(1, 2, 3));
        grafoFloydPPT.agregarArista(1, 2, new WeightedEdge(8));
        grafoFloydPPT.agregarArista(2, 1, new WeightedEdge(3));
        grafoFloydPPT.agregarArista(3, 2, new WeightedEdge(2));
        grafoFloydPPT.agregarArista(1, 3, new WeightedEdge(5));
    }

    public void testFloydCalculaDistancias() {
        IFloydWarshallResult<Integer> r = algos.floyd(grafoFloydPPT);
        assertEquals(0.0, r.getCost(1, 1));
        assertEquals(7.0, r.getCost(1, 2));
        assertEquals(5.0, r.getCost(1, 3));
        assertEquals(3.0, r.getCost(2, 1));
        assertEquals(8.0, r.getCost(2, 3));
        assertEquals(5.0, r.getCost(3, 1));
        assertEquals(2.0, r.getCost(3, 2));
    }

    public void testFloydRecuperaCamino() {
        IFloydWarshallResult<Integer> r = algos.floyd(grafoFloydPPT);
        assertEquals(List.of(1, 3, 2), r.getPath(1, 2));
    }

    public void testWarshallAlcanzabilidad() {
        IFloydWarshallResult<Integer> r = algos.warshall(grafoFloydPPT);
        assertTrue(r.connected(1, 2));
        assertTrue(r.connected(3, 1));
        assertTrue(r.connected(2, 3));
    }

    public void testDijkstra() {
        IDijkstraResult<Integer> r = algos.dijkstra(grafoFloydPPT.construirComparable(1), grafoFloydPPT);
        assertEquals(0.0, r.getCost(1));
        assertEquals(7.0, r.getCost(2));
        assertEquals(5.0, r.getCost(3));
        assertEquals(List.of(1, 3, 2), r.getPath(2));
    }

    public void testDijkstraOrigenInexistente() {
        assertNull(algos.dijkstra(grafoFloydPPT.construirComparable(99), grafoFloydPPT));
    }

    public void testTodosLosCaminos() {
        List<?> caminos = algos.obtenerTodosLosCaminos(
                grafoFloydPPT.construirComparable(1),
                grafoFloydPPT.construirComparable(2),
                grafoFloydPPT);
        assertEquals(2, caminos.size());
    }

    public void testClasificacionTopologica() {
        DirectedIGraph<Integer, WeightedEdge> dag = new DirectedIGraph<>();
        dag.agregarVertices(List.of(1, 2, 3, 4));
        dag.agregarArista(1, 2, new WeightedEdge(1));
        dag.agregarArista(1, 3, new WeightedEdge(1));
        dag.agregarArista(3, 4, new WeightedEdge(1));
        dag.agregarArista(2, 4, new WeightedEdge(1));

        List<Integer> orden = algos.calcularClasificacionTopologica(dag);

        assertTrue(orden.indexOf(1) < orden.indexOf(2));
        assertTrue(orden.indexOf(1) < orden.indexOf(3));
        assertTrue(orden.indexOf(3) < orden.indexOf(4));
        assertTrue(orden.indexOf(2) < orden.indexOf(4));
    }
}