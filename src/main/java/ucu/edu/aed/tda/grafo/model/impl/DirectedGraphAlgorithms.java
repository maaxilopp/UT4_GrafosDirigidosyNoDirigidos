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
     * Recorrido en profundidad de un grafo dirigido, utilizando un hashset para controlar los vértices visitados y corroborar en O(1)
     * y evitar ciclos infinitos. Orden: O(Vertices + Aristas) ya que se visitan todos los vértices y aristas del grafo exactamente una vez.
     * @param grafo Grafo dirigido que se va a recorrer en profundidad
     * @param sourceCriteria Criterio para encontrar el vértice de origen del recorrido
     * @param consumer Funcion que se ejecuta al visitar cada vértice
     * @param <V> Tipo generico de vertices
     * @param <D> Tipo generico de arcos
     */

    @Override
    public <V, D> void recorridoEnProfundidad(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V verticeInicial = grafo.buscarVertice(sourceCriteria);
        if (verticeInicial == null) return;
        Set<V> visitados = new HashSet<>();
        recorridoEnProfundidadRecursivo(grafo, verticeInicial, consumer, visitados);
        for (V vertice : grafo.vertices()) {
            if (!visitados.contains(vertice)) {
                recorridoEnProfundidadRecursivo(grafo, vertice, consumer, visitados);
            }
        }
    }

    private <V, D> void recorridoEnProfundidadRecursivo(IGraph<V, D> grafo, V verticeActual, Consumer<V> consumer, Set<V> visitados) {
        visitados.add(verticeActual);
        consumer.accept(verticeActual);

        for(Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(verticeActual))) {
            V adyacente = arista.target();
            if (!visitados.contains(adyacente)) {
                recorridoEnProfundidadRecursivo(grafo, adyacente, consumer, visitados);
            }
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
