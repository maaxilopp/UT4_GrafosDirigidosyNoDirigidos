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
        UndirectedGraph<V, D> arbol = new UndirectedGraph<>();
        graph.vertices().forEach(arbol::agregarVertice);
        List<Edge<V, D>> aristasOrdenadas = new ArrayList<>();
        graph.aristas().forEach(e -> aristasOrdenadas.add((Edge<V, D>) e));
        aristasOrdenadas.sort(Comparator.comparingDouble(e -> e.dato().getWeight()));

        UnionFind<V> componentes = new UnionFind<>(graph.vertices());

        for (Edge<V, D> arista : aristasOrdenadas) {
            V u = arista.source();
            V v = arista.target();
            if (componentes.find(u) != componentes.find(v)) {
                componentes.union(u, v);
                arbol.agregarArista(u, v, arista.dato());
            }
        }
        return arbol;
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

    /**
     * Permite, en tiempo casi constante, saber si dos vértices están en el mismo
     * componente conexo y unir dos componentes. La usa Kruskal para descartar las
     * aristas que cerrarían un ciclo.
     *
     * @param <T> tipo de los elementos (vértices)
     */
    private static final class UnionFind<T> {
        private final Map<T, T> padre = new HashMap<>();

        UnionFind(Collection<T> elementos) {
            elementos.forEach(e -> padre.put(e, e));
        }


        T find(T x) {
            T raiz = x;
            while (!raiz.equals(padre.get(raiz))) {
                raiz = padre.get(raiz);
            }
            T actual = x;
            while (!actual.equals(raiz)) {
                T siguiente = padre.get(actual);
                padre.put(actual, raiz);
                actual = siguiente;
            }
            return raiz;
        }

        void union(T x, T y) {
            padre.put(find(x), find(y));
        }
    }
}