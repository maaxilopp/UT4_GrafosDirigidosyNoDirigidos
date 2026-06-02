package ucu.edu.aed.tda.grafo.model.impl;

import ucu.edu.aed.tda.grafo.IDirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.IDirectedIGraph;
import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DirectedGraphAlgorithms implements IDirectedGraphAlgorithms {
    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        return 0;
    }

    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        return List.of();
    }

    /**
     * Recorrido en profundidad del grafo comenzando desde el vértice que cumple sourceCriteria.
     * Si el grafo no es conexo, continúa el recorrido desde los vértices no alcanzados,
     * garantizando que todos los vértices sean visitados exactamente una vez.
     * Orden: O(Vertices + Aristas)
     *
     * @param grafo          grafo a recorrer
     * @param sourceCriteria criterio para encontrar el vértice de origen del recorrido
     * @param consumer       función que se ejecuta al visitar cada vértice
     */
    @Override
    public <V, D> void recorridoEnProfundidad(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V verticeInicial = grafo.buscarVertice(sourceCriteria);
        if (verticeInicial == null) return;
        Set<V> visitados = new HashSet<>();
        recorridoEnProfundidadRecursivo(grafo, verticeInicial, consumer, visitados);
        for (V vertice : grafo.vertices()) {
            recorridoEnProfundidadRecursivo(grafo, vertice, consumer, visitados);
        }
    }

    /**
     * Método recursivo auxiliar del recorrido en profundidad.
     * Si el vértice ya fue visitado retorna inmediatamente.
     * Si no, lo marca como visitado, aplica el consumer y recurre sobre sus adyacentes.
     * @param grafo         grafo a recorrer
     * @param verticeActual vértice que se está procesando en esta llamada
     * @param consumer      función que se ejecuta al visitar cada vértice
     * @param visitados     conjunto compartido de vértices ya visitados, consultado en O(1)
     */
    private <V, D> void recorridoEnProfundidadRecursivo(IGraph<V, D> grafo, V verticeActual, Consumer<V> consumer, Set<V> visitados) {
        if (visitados.contains(verticeActual)) return;
        visitados.add(verticeActual);
        consumer.accept(verticeActual);
        for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(verticeActual))) {
            recorridoEnProfundidadRecursivo(grafo, arista.target(), consumer, visitados);
        }
    }

    @Override
    public <V, D> void recorridoEnAmplitud(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {

    }

    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        return List.of();
    }
}
