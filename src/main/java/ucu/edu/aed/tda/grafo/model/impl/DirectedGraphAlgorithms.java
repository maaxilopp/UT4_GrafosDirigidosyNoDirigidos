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
     * Se inicializa una matriz de distancias con los pesos de las aristas, y luego
     * se actualiza iterativamente considerando cada vértice como posible intermediario.
     * Orden: O(vertices al cubo), hay tres bucles "for" anidadados. Es extremadamente
     * costoso para grafos grandes, pero es un algoritmo clásico para este problema.
     * @param grafo grafo al que se le calculará las distancias mínimas de los vertices.
     * @param <V> tipo genérico de los vértices del grafo
     * @param <D> tipo genérico de los datos asociados a las aristas, que deben ser ponderados (tener peso).
     * @return  las distancias mínimas entre todos los pares de vértices.
     */
    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        List<V> vertices = new ArrayList<>(grafo.vertices());
        int n = vertices.size();

        Map<V, Integer> indices = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indices.put(vertices.get(i), i);
        }

        double[][] dist = new double[n][n];
        int[][] next = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                next[i][j] = -1; // por defecto, sin camino conocido

                if (i == j) {
                    dist[i][j] = 0;
                    next[i][j] = j;
                    continue;
                }

                Edge<V, D> arista = grafo.obtenerArista(
                        grafo.construirComparable(vertices.get(i)),
                        grafo.construirComparable(vertices.get(j))
                );

                if (arista != null) {
                    dist[i][j] = arista.dato().getWeight();
                    next[i][j] = j; // el siguiente paso para ir de i a j es j, porque hay una arista directa
                } else {
                    dist[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        // probar cada k como intermedio
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (dist[i][k] != Double.POSITIVE_INFINITY
                            && dist[k][j] != Double.POSITIVE_INFINITY
                            && dist[i][k] + dist[k][j] < dist[i][j]) {

                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k]; // el siguiente paso para ir de i a j es el mismo que el siguiente paso para ir de i a k, porque k es el nuevo intermedio más corto
                    }
                }
            }
        }

        return new FloydWarshallResult<>(dist, null, next, indices, vertices);
    }

    /**
     * El algoritmo de Warshall se utiliza para determinar Si existe un camino entre dos vértices.
     * Al ser basicamente un Floyd booleano, su orden es O(vertices al cubo), al igual que Floyd.
     * @param grafo grafo de prueba
     * @return Una matriz booleana donde el valor en la posición (i, j) es true si existe un camino desde el vértice i al vértice j, y false en caso contrario.
     * @param <V> generico de los vertices del grafo.
     * @param <D> genérico de los datos asociados a las aristas, que deben ser ponderados (tener peso).
     */
    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        List<V> vertices = new ArrayList<>(grafo.vertices());
        int n = vertices.size();
        Map<V, Integer> indices = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indices.put(vertices.get(i), i);
        }

        boolean[][] hayCamino = new boolean[n][n];

        // hay camino directo si existe una arista, y hay camino a sí mismo
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    hayCamino[i][j] = true;
                    continue;
                }
                Edge<V, D> arista = grafo.obtenerArista(
                        grafo.construirComparable(vertices.get(i)),
                        grafo.construirComparable(vertices.get(j))
                );
                hayCamino[i][j] = (arista != null);
            }
        }

        // hay camino de i a j si ya había uno, o si hay camino de i a k y de k a j
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    hayCamino[i][j] = hayCamino[i][j] || (hayCamino[i][k] && hayCamino[k][j]);
                }
            }
        }

        return new FloydWarshallResult<>(null, hayCamino, null, indices, vertices);
    }

    /**
     * El centro del grafo es el vértice de menor excentricidad, es decir, el que está
     * "mejor ubicado", el que en su peor caso llega más cerca que cualquier otro.
     * Ejecuta Floyd una sola vez para tener todas las distancias mínimas, y luego
     * calcula la excentricidad de cada vértice consultando ese resultado.
     * Orden: O(vertices al cubo), dominado por la única ejecución de Floyd.
     * @param grafo grafo al que se le calcula el centro.
     * @return el vértice de menor excentricidad, o null si el grafo está vacío.
     * @param <V> genérico de los vértices del grafo.
     * @param <D> genérico de los datos asociados a las aristas, que deben ser ponderados.
     */
    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        IFloydWarshallResult<V> resultadoFloyd = floyd(grafo);
        V centro = null;
        double menorExcentricidad = Double.POSITIVE_INFINITY;

        for(V vertice : grafo.vertices()) {
            double temporal = 0;
            for(V otroVertice : grafo.vertices()) {
                double distancia = resultadoFloyd.getCost(vertice, otroVertice);
                if(distancia != Double.POSITIVE_INFINITY){
                    temporal = Math.max(temporal, distancia);
                }
            }
            if (temporal < menorExcentricidad) {
                menorExcentricidad = temporal;
                centro = vertice;
            }
        }
        return centro;
    }

    /**
     * La excentricidad de un vértice es la distancia máxima desde ese vértice a cualquier otro vértice alcanzable en el grafo.
     * Para calcularla, se puede ejecutar el algoritmo de Floyd para obtener las distancias mínimas entre todos los pares de vértices,
     * y luego encontrar la distancia máxima desde el vértice dado a cualquier otro vértice alcanzable. Al usar Floyd para obtener todas
     * las conexiones de un grafo, el orden es O(vertices al cubo).
     * @param grafo grafo al que se le desea obtener la excentricidad de un vertice.
     * @param vertexCriteria vertice del grafo que cumple con el criterio de busqueda.
     * @return la distancia al vértice más lejano alcanzable, o infinito si el vértice no existe en el grafo.
     * @param <V> generico de los vertices de un grafo.
     * @param <D> genérico de los datos asociados a las aristas, que deben ser ponderados (tener peso).
     */
    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        V vertice = grafo.buscarVertice(vertexCriteria);
        if(vertice == null){
            return Double.POSITIVE_INFINITY;
        }
        IFloydWarshallResult<V> resultadoFloyd = floyd(grafo);
        double excentricidad = 0;
        for(V otroVertice : grafo.vertices()){
            double distancia = resultadoFloyd.getCost(vertice, otroVertice);
            if(distancia != Double.POSITIVE_INFINITY){
                excentricidad = Math.max(excentricidad, distancia);
            }

        }
        return excentricidad;
    }

    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        return List.of();
    }

    /**
     * Recorrido en profundidad del grafo comenzando desde el vértice que cumple sourceCriteria.
     * Si el grafo no es conexo, continúa el recorrido desde los vértices no alcanzados,
     * garantizando que todos los vértices sean visitados exactamente una vez.
     * Orden: O(Vertices + Aristas), pasa por todos los vertices y aristas del grafo una vez exactamente.
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
