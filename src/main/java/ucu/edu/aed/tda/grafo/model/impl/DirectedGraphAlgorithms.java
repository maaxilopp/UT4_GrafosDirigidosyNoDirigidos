package ucu.edu.aed.tda.grafo.model.impl;

import ucu.edu.aed.tda.grafo.IDirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.IDirectedIGraph;
import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.ArrayList;
import java.util.*;
import java.util.function.Consumer;

public class DirectedGraphAlgorithms implements IDirectedGraphAlgorithms {

    /**
     * Algoritmo de Dijkstra sobre el grafo desde el vértice "origen".
     * Calcula el camino mínimo desde el origen a todos los demás vértices.
     * Orden: O((V + A) log V) usando cola de prioridad para comparar caminos.
     *
     * @param source criterio para encontrar el vértice de origen
     * @param grafo  grafo dirigido sobre el que se ejecuta el algoritmo
     * @return resultado con las distancias mínimas y predecesores para recuperar caminos
     */
    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
        V origen = grafo.buscarVertice(source);
        if (origen == null) {
            return null;
        }
        Set<V> s = new HashSet<>(); // vértices cuya distancia mínima ya es conocida
        Map<V, Double> d = new HashMap<>(); // distancia mínima desde el origen a cada vértice
        Map<V, V> p = new HashMap<>(); // predecesor de cada vértice en el camino mínimo

        for (V vertice : grafo.vertices()) {
            d.put(vertice, Double.POSITIVE_INFINITY);
            p.put(vertice, null);
        }
        d.put(origen, 0.0);

        PriorityQueue<V> colaPrioridad = new PriorityQueue<>(Comparator.comparingDouble(d::get));
        colaPrioridad.add(origen);

        while (!colaPrioridad.isEmpty()) {
            V verticeMinimo = colaPrioridad.poll();
            if (s.contains(verticeMinimo)) continue;
            s.add(verticeMinimo);
            for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(verticeMinimo))) {
                V vecino = arista.target();
                double pesoArista = arista.dato().getWeight();
                if (d.get(verticeMinimo) + pesoArista < d.get(vecino)) {
                    d.put(vecino, d.get(verticeMinimo) + pesoArista);
                    p.put(vecino, verticeMinimo);
                    colaPrioridad.add(vecino);
                }
            }
        }
        return new DijkstraResult<>(d, p);
    }

    /**
     * El algoritmo calcula las distancias mínimas entre todos los pares de vértices.
     * Se inicializa una matriz de distancias con los pesos de las aristas, y luego se actualiza iterativamente considerando cada vértice como posible intermediario.
     * @param grafo
     * @param <V>
     * @param <D>
     * @return FloydWarshallResult con la matriz de distancias mínimas entre todos los pares de vértices.
     */
    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        List<V> vertices = new ArrayList<>(grafo.vertices());
        int n = vertices.size();

        double[][] dist = new double[n][n];

        // Inicialización
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (i == j) {
                    dist[i][j] = 0;
                    continue;
                }

                Edge<V, D> arista = grafo.obtenerArista(
                        (Comparable<V>) vertices.get(i),
                        (Comparable<V>) vertices.get(j)
                );

                if (arista != null) {
                    dist[i][j] = arista.dato().getWeight();
                } else {
                    dist[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        // Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (dist[i][k] != Double.POSITIVE_INFINITY
                            && dist[k][j] != Double.POSITIVE_INFINITY
                            && dist[i][k] + dist[k][j] < dist[i][j]) {

                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        return new FloydWarshallResult<>(dist);
    }

    /***
     * El algoritmo de Warshall se utiliza para determinar la transitividad en un grafo dirigido, es decir, si existe un camino entre dos vértices.
     * @param grafo
     * @return Una matriz booleana donde el valor en la posición (i, j) es true si existe un camino desde el vértice i al vértice j, y false en caso contrario.
     * @param <V>
     * @param <D>
     */
    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        return floyd(grafo);
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
     * Metodo recursivo auxiliar del recorrido en profundidad.
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
