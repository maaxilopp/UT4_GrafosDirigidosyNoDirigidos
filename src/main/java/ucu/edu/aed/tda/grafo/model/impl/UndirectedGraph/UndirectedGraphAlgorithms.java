package ucu.edu.aed.tda.grafo.model.impl.UndirectedGraph;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.IUndirectedGraphAlgorithm;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.*;
import java.util.function.Consumer;

public class UndirectedGraphAlgorithms implements IUndirectedGraphAlgorithm {

    /**
     * Algoritmo de Kruskal para hallar un Árbol Abarcador de costo Mínimo.
     * Parte de M con los vértices de G y sin aristas, y de la lista A con todas las
     * aristas de G. En cada iteración elige la arista de costo mínimo de A, la quita
     * de A y la agrega a M; si al agregarla M queda con un ciclo la remueve, y si no,
     * cuenta esa arista como parte del árbol. Repite hasta haber agregado V-1 aristas.
     * Orden: O(V x A x (V + A)) en el peor caso, por la búsqueda de la mínima y la
     * detección de ciclos en cada una de las iteraciones.
     *
     * @param graph grafo no dirigido y ponderado de entrada
     * @param <V>   tipo genérico de los vértices
     * @param <D>   tipo del dato de la arista, debe ser ponderado (tener peso)
     * @return un nuevo grafo con los vértices de graph y las aristas del AAM
     */
    @Override
    @SuppressWarnings("unchecked")
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> kruskal(IUndirectedGraph<V, D> graph) {
        List<Edge<V, D>> a = new ArrayList<>();
        graph.aristas().forEach(e -> a.add((Edge<V, D>) e));

        UndirectedGraph<V, D> m = new UndirectedGraph<>();
        graph.vertices().forEach(m::agregarVertice);

        int n = graph.cantidadDeVertices() - 1;
        int i = 0;

        while (i < n) {
            Edge<V, D> arista = elegirAristaMinima(a);
            a.remove(arista);
            m.agregarArista(arista.source(), arista.target(), arista.dato());
            if (m.tieneCiclos()) {
                m.eliminarArista(m.construirComparable(arista.source()), m.construirComparable(arista.target()));
            } else {
                i++;
            }
        }
        return m;
    }

    /**
     * Retorna la arista de menor costo de la lista dada.
     * Orden: O(A), recorre todas las aristas una vez.
     *
     * @param aristas lista de aristas candidatas
     * @param <V>     tipo genérico de los vértices
     * @param <D>     tipo del dato de la arista, debe ser ponderado (tener peso)
     * @return la arista de costo mínimo, o null si la lista está vacía
     */
    private <V, D extends WeightedEdge> Edge<V, D> elegirAristaMinima(List<Edge<V, D>> aristas) {
        Edge<V, D> minima = null;
        double min = Double.MAX_VALUE;
        for (Edge<V, D> arista : aristas) {
            if (arista.dato().getWeight() < min) {
                min = arista.dato().getWeight();
                minima = arista;
            }
        }
        return minima;
    }

    /**
     * Algoritmo de Prim para hallar un Árbol Abarcador de costo Mínimo.
     * Arranca con el conjunto U conteniendo solo el vértice origen y hace "crecer" el
     * árbol arista por arista: en cada paso busca la arista de costo mínimo que conecta
     * un vértice de U con uno de V-U la agrega al árbol y pasa
     * ese nuevo vértice de V-U a U. Repite hasta que U contiene todos los vértices.
     * Orden: en cada una de las V-1 iteraciones searchMinEdge recorre U x V, por lo que
     * resulta O(V^3) con esta representación basada en búsqueda directa de aristas.
     *
     * @param graph  grafo no dirigido y ponderado de entrada (se asume conexo)
     * @param source criterio para encontrar el vértice de origen
     * @param <V>    tipo genérico de los vértices
     * @param <D>    tipo del dato de la arista, debe ser ponderado (tener peso)
     * @return un nuevo grafo con los vértices de graph y las aristas del AAM, o null si
     * el vértice origen no existe
     */
    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> prim(IUndirectedGraph<V, D> graph, Comparable<V> source) {
        V origen = graph.buscarVertice(source);
        if (origen == null) return null;

        UndirectedGraph<V, D> arbol = new UndirectedGraph<>();
        graph.vertices().forEach(arbol::agregarVertice);

        Set<V> u = new HashSet<>();
        Set<V> v = new HashSet<>(graph.vertices());
        u.add(origen);
        v.remove(origen);

        while (!v.isEmpty()) {
            Edge<V, D> minima = searchMinEdge(graph, u, v);
            if (minima == null) break;
            V nuevo = u.contains(minima.source()) ? minima.target() : minima.source();
            arbol.agregarArista(minima.source(), minima.target(), minima.dato());
            u.add(nuevo);
            v.remove(nuevo);
        }
        return arbol;
    }

    /**
     * Retorna la arista de costo mínimo que conecta un vértice del conjunto U con un
     * vértice del conjunto V (típicamente V-U). Recorre cada par (u,v) con u en U y v
     * en V, consulta la arista entre ambos y se queda con la de menor peso.
     * Es el método auxiliar central del algoritmo de Prim.
     * Orden: O(|U| x |V| x n), por los dos bucles anidados y el costo O(n) de buscar
     * cada arista en la representación por lista de adyacencia.
     *
     * @param graph grafo no dirigido y ponderado
     * @param U     conjunto de vértices ya incorporados
     * @param V     conjunto de vértices candidatos a incorporar
     * @param <Ve>  tipo genérico de los vértices
     * @param <D>   tipo del dato de la arista, debe ser ponderado (tener peso)
     * @return la arista de menor costo entre U y V, o null si no existe ninguna
     */
    @Override
    public <Ve, D extends WeightedEdge> Edge<Ve, D> searchMinEdge(IUndirectedGraph<Ve, D> graph, Collection<Ve> U, Collection<Ve> V) {
        double min = Double.MAX_VALUE;
        Edge<Ve, D> mejorArista = null;
        for (Ve u : U) {
            for (Ve v : V) {
                Edge<Ve, D> arista = graph.obtenerArista(graph.construirComparable(u), graph.construirComparable(v));
                if (arista != null && arista.dato().getWeight() < min) {
                    min = arista.dato().getWeight();
                    mejorArista = arista;
                }
            }
        }
        return mejorArista;
    }

    /**
     * Búsqueda en amplitud sobre todo el grafo no Para cubrir grafos no
     * conexos arranca un recorrido desde cada vértice aún no visitado. Desde cada vértice
     * encolado visita todos sus adyacentes no visitados antes de avanzar en profundidad,
     * usando una cola FIFO. Aplica el consumer al desencolar cada vértice.
     * Orden: O(vértices + aristas), cada vértice se encola una vez y cada arista se
     * examina una vez.
     *
     * @param graph    grafo no dirigido a recorrer
     * @param consumer función que se ejecuta sobre cada vértice visitado
     * @param <V>      tipo genérico de los vértices
     * @param <D>      tipo genérico del dato de la arista
     */
    @Override
    public <V, D> void bea(IUndirectedGraph<V, D> graph, Consumer<V> consumer) {
        Set<V> visitados = new HashSet<>();
        Queue<V> cola = new LinkedList<>();
        for (V vertice : graph.vertices()) {
            if (!visitados.contains(vertice)) {
                cola.offer(vertice);
                visitados.add(vertice);
                while (!cola.isEmpty()) {
                    V actual = cola.poll();
                    consumer.accept(actual);
                    for (Edge<V, D> edge : graph.adyacencias(graph.construirComparable(actual))) {
                        // como la relación es bidireccional, el vecino es el extremo que no es actual
                        V y = edge.source().equals(actual) ? edge.target() : edge.source();
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