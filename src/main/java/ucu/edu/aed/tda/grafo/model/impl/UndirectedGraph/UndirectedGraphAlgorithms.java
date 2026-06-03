package ucu.edu.aed.tda.grafo.model.impl.UndirectedGraph;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.IUndirectedGraphAlgorithm;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.*;
import java.util.function.Consumer;

public class UndirectedGraphAlgorithms implements IUndirectedGraphAlgorithm {


    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> kruskal(IUndirectedGraph<V, D> graph) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> prim(IUndirectedGraph<V, D> graph, Comparable<V> source) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> Edge<V, D> searchMinEdge(IUndirectedGraph<V, D> graph, Collection<V> U, Collection<V> V) {
        return null;
    }

    @Override
    public <V, D> void bea(IUndirectedGraph<V, D> graph, Consumer<V> consumer) {
        Set<V> visitados = new HashSet<>();
        Queue<V> cola = new LinkedList<>();
        for (V vertice : graph.vertices()) {
            if (!visitados.contains(vertice)) {
                cola.offer(vertice);
                while (!cola.isEmpty()) {
                    V actual = cola.poll();
                    visitados.add(actual);
                    consumer.accept(actual);
                    for (Edge<V, D> edge : graph.adyacencias(graph.construirComparable(actual))) {
                        V y = edge.source().equals(actual) ? edge.target() : edge.source(); // necesito obtener el vecino que no es actual ya que la relacion es bidireccional
                        if (!visitados.contains(y)) {
                            cola.offer(y);
                            visitados.add(y);
                        }
                    }

                }
            }
        }

    }

}
