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
                dist[i][j] = (i == j) ? 0 : Double.POSITIVE_INFINITY;
                next[i][j] = (i == j) ? j : -1;
            }
        }


        for (int i = 0; i < n; i++) {
            V origen = vertices.get(i);
            for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(origen))) {
                Integer j = indices.get(arista.target());
                if (j != null) {
                    dist[i][j] = arista.dato().getWeight();
                    next[i][j] = j;
                }
            }
        }


        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Double.POSITIVE_INFINITY
                            && dist[k][j] != Double.POSITIVE_INFINITY
                            && dist[i][k] + dist[k][j] < dist[i][j]) {

                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
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
                temporal = Math.max(temporal, distancia);

            }
            if (temporal < menorExcentricidad) { // precondicion: ante dos vértices con la misma excentricidad mínima nos quedamos con el primero que aparezca en la iteración.
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
            excentricidad = Math.max(excentricidad, distancia);

        }
        return excentricidad;
    }

    /**
     * Encuentra todos los caminos simples (sin ciclos) desde el vértice origen al destino.
     * Usa backtracking DFS: explora cada vecino no visitado, lo agrega al camino actual,
     * recurre, y al volver deshace la decisión para explorar otras ramas.
     * Orden: O(V!) en el peor caso, ya que puede haber un camino simple por cada
     * permutación de vértices. Solo es práctico en grafos pequeños o dispersos.
     *
     * @param source criterio para encontrar el vértice de origen
     * @param target criterio para encontrar el vértice de destino
     * @param grafo  grafo sobre el que se buscan los caminos
     * @return lista de todos los caminos simples encontrados, cada uno con su costo total;
     *         lista vacía si alguno de los vértices no existe o no hay caminos
     */
    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        V origen = grafo.buscarVertice(source);
        V destino = grafo.buscarVertice(target);
        if (origen == null || destino == null) return List.of();

        List<Path<V>> resultados = new ArrayList<>();
        Set<V> visitados = new HashSet<>();
        List<V> caminoActual = new ArrayList<>();

        caminoActual.add(origen);
        visitados.add(origen);
        buscarCaminosRecursivo(grafo, origen, destino, visitados, caminoActual, 0.0, resultados);
        return resultados;
    }

    /**
     * Método recursivo auxiliar para obtenerTodosLosCaminos.
     * Al llegar al destino guarda una copia del camino actual junto a su costo acumulado.
     * Para cada vecino no visitado: lo marca, lo agrega al camino y recurre;
     * al retornar lo desmarca y lo elimina del camino (backtrack).
     *
     * @param grafo          grafo sobre el que se buscan los caminos
     * @param actual         vértice que se está procesando en esta llamada
     * @param destino        vértice al que se quiere llegar
     * @param visitados      conjunto de vértices ya en el camino actual, evita ciclos
     * @param caminoActual   lista con los vértices del camino construido hasta ahora
     * @param costoAcumulado suma de los pesos de las aristas recorridas hasta ahora
     * @param resultados     lista compartida donde se agregan los caminos completos encontrados
     */
    private <V, D extends WeightedEdge> void buscarCaminosRecursivo(
            IGraph<V, D> grafo, V actual, V destino,
            Set<V> visitados, List<V> caminoActual, double costoAcumulado, List<Path<V>> resultados) {

        if (actual.equals(destino)) {
            resultados.add(new Path<>(new ArrayList<>(caminoActual), costoAcumulado));
            return;
        }
        for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(actual))) {
            V vecino = arista.target();
            if (!visitados.contains(vecino)) {
                visitados.add(vecino);
                caminoActual.add(vecino);
                buscarCaminosRecursivo(grafo, vecino, destino, visitados, caminoActual,
                        costoAcumulado + arista.dato().getWeight(), resultados);
                // backtrack
                caminoActual.remove(caminoActual.size() - 1);
                visitados.remove(vecino);
            }
        }
    }


    /**
     * Recorrido en profundidad del grafo comenzando desde el vértice que cumple con el criterio.
     * Orden: O(Vertices + Aristas), pasa por todos los vertices y aristas del grafo (alcanzables) una vez exactamente.
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

    /**
     * Recorrido en amplitud del grafo comenzando desde el vértice que cumple con el criterio.
     * Orden: O(Vertices^2 + Aristas), porque adyacencias() resuelve el vértice con buscarVertice()
     * en O(Vertices) y se invoca una vez por cada vértice desencolado.
     * @param grafo          grafo a recorrer
     * @param sourceCriteria criterio para encontrar el vértice de origen del recorrido
     * @param consumer       función que se ejecuta al visitar cada vértice
     */
    @Override
    public <V, D> void recorridoEnAmplitud(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V verticeInicial = grafo.buscarVertice(sourceCriteria);
        if (verticeInicial == null) return;
        Set<V> visitados = new HashSet<>();
        Queue<V> cola = new LinkedList<>();
        visitados.add(verticeInicial);
        cola.add(verticeInicial);
        while (!cola.isEmpty()) {
            V verticeActual = cola.poll();
            consumer.accept(verticeActual);
            for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(verticeActual))) {
                V vecino = arista.target();
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.add(vecino);
                }
            }
        }

    }

<<<<<<< HEAD
    public <V, D> void clasificacionTopologicaAuxPPT(V nodo,Set<V> visitados,LinkedList<V> listaNodos,IDirectedIGraph<V, D> grafo){
        if (!visitados.contains(nodo)){
            visitados.add(nodo);
            List<Edge<V, D>> aristas = grafo.adyacencias((Comparable<V>) nodo);

            for(Edge<V, D> w : aristas){
                V adyacente = w.target();
                clasificacionTopologicaAuxPPT(adyacente,visitados,listaNodos,grafo);
            }
            listaNodos.addFirst(nodo);
        }
    }

    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        Set<V> visitados = new HashSet<>();
        LinkedList<V> listaNodos = new LinkedList<>();

        for(V vertices : grafo.vertices()){
            clasificacionTopologicaAuxPPT(vertices,visitados,listaNodos,grafo);
        }

        return listaNodos;
=======
    /**
     * Clasificación topológica del grafo mediante DFS con stack de finalización.
     * Recorre todos los vértices; por cada uno no visitado lanza un DFS que apila
     * cada vértice al terminar de explorar todos sus descendientes. El orden topológico
     * es el inverso del orden de apilado.
     * Solo es válido para DAGs (grafos dirigidos sin ciclos).
     * Orden: O(V + A), igual que un DFS estándar.
     *
     * @param grafo grafo dirigido acíclico sobre el que se calcula el orden topológico
     * @return lista de vértices en orden topológico; si el grafo tiene ciclos el resultado
     *         no tiene validez semántica
     */
    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        Set<V> visitados = new HashSet<>();
        Deque<V> stack = new ArrayDeque<>();

        for (V vertice : grafo.vertices()) {
            if (!visitados.contains(vertice)) {
                topoRecursivo(grafo, vertice, visitados, stack);
            }
        }

        List<V> resultado = new ArrayList<>();
        while (!stack.isEmpty()) {
            resultado.add(stack.pop());
        }
        return resultado;
    }

    /**
     * Método recursivo auxiliar para calcularClasificacionTopologica.
     * Marca el vértice actual como visitado, recurre sobre cada vecino no visitado,
     * y al finalizar todos sus descendientes lo apila. Esto garantiza que un vértice
     * siempre quede por delante de todos los vértices a los que apunta.
     *
     * @param grafo     grafo dirigido sobre el que se ejecuta el DFS
     * @param actual    vértice que se está procesando en esta llamada
     * @param visitados conjunto compartido de vértices ya procesados
     * @param stack     pila compartida donde se apilan los vértices al finalizar
     */
    private <V, D> void topoRecursivo(
            IDirectedIGraph<V, D> grafo, V actual,
            Set<V> visitados, Deque<V> stack) {

        visitados.add(actual);
        for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(actual))) {
            V vecino = arista.target();
            if (!visitados.contains(vecino)) {
                topoRecursivo(grafo, vecino, visitados, stack);
            }
        }
        stack.push(actual);
>>>>>>> bbc448063b5e88006a39f0349ed3d46f17070a57
    }
}
