package ucu.edu.aed.tda.grafo.model.impl;

import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementación del resultado de ejecutar el algoritmo de Dijkstra.
 * Almacena las distancias mínimas y los predecesores de cada vértice
 * para poder recuperar el camino mínimo desde el origen a cualquier vértice.
 *
 * @param <V> tipo genérico de los vértices
 */
public class DijkstraResult<V> implements IDijkstraResult<V> {
    private final Map<V, Double> distances;   // distancia mínima desde el origen a cada vértice
    private final Map<V, V> predecessors;     // predecesor de cada vértice en el camino mínimo

    public DijkstraResult(Map<V, Double> distances, Map<V, V> predecessors) {
        this.distances = distances;
        this.predecessors = predecessors;
    }

    /**
     * Devuelve la distancia mínima desde el origen hasta el otro vertice.
     * Si no es alcanzable retorna infinito.
     *
     * @param otherVertex vértice destino
     * @return costo del camino mínimo, o infinito si no es alcanzable
     */
    @Override
    public double getCost(V otherVertex) {
        return distances.getOrDefault(otherVertex, Double.POSITIVE_INFINITY);
    }

    /**
     * Retorna el camino mínimo desde el origen hasta otro vertice
     * reconstruyendo los predecesores en orden inverso.
     * Si el vértice no es alcanzable retorna una lista vacía.
     *
     * @param otherVertex vértice destino
     * @return lista de vértices que forman el camino mínimo, o lista vacía si no es alcanzable
     */
    @Override
    public List<V> getPath(V otherVertex) {
        if (distances.getOrDefault(otherVertex, Double.POSITIVE_INFINITY) == Double.POSITIVE_INFINITY)
            return List.of();
        LinkedList<V> path = new LinkedList<>();
        V current = otherVertex;
        while (current != null) {
            path.addFirst(current);
            current = predecessors.get(current);
        }
        return path;
    }
}