package ucu.edu.aed.tda.grafo.model.impl;

import ucu.edu.aed.tda.grafo.IDirectedIGraph;
import ucu.edu.aed.tda.grafo.model.edge.DirectedEdge;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

import java.util.*;

public class DirectedIGraph<V, D> implements IDirectedIGraph<V, D> {
    private final Map<V, Set<Edge<V, D>>> listaDeAdyacencia = new HashMap<>();

    @Override
    public Set<V> successors(Comparable<V> criteria) { return Set.of(); }

    @Override
    public Set<V> predecessors(Comparable<V> criteria) { return Set.of(); }

    /**
     * Intenta agregar un vertice al nodo validando que no sea nulo y
     * que ya la clave no este en la lista de adyacencia orden O(1),
     * calcula el indice directo mediante hash sin recorrer toda la lista.
     * @param vertex vertice a agregar
     * @return true si lo pudo agregar, false en caso contrario
     */
    @Override
    public boolean agregarVertice(V vertex) {
        if (vertex == null || listaDeAdyacencia.containsKey(vertex)) {
            return false;
        }
        listaDeAdyacencia.put(vertex, new HashSet<>());
        return true;
    }

    /**
     * Recorre todos los vértices, filtra el que cumple el criterio de
     * comparación, y retorna el primero que encuentra. Si no encuentra
     * ninguno retorna null, orden O(n) ya que en el peor caso, recorre
     * todos los vertices y ninguno cumple con el criterio de busqueda
     * @param criterio criterio de busqueda
     * @return null si no hay tal vertice que cumpla el criterio, sino
     * el primer vértice que lo cumpla.
     */
    @Override
    public V buscarVertice(Comparable<V> criterio) {
        return vertices().stream()
                .filter(v -> criterio.compareTo(v) == 0)
                .findFirst()
                .orElse(null);
    }

    /**
     * Inserta un valor (arista) a una clave(vertice de origen) si y solo si
     * tanto el origen como el destino de la arista son validos (parte de la lista),
     * en caso de que si, también valida su inserción correcta, es decir, que no fuera parte.
     * del hashmap previamente. Orden O(1) ya que se calcula el indice directo mediante hash
     * sin recorrer toda la lista.
     * @param source Vertice de origen de la arista
     * @param target Vertice destino de la arista
     * @param dato info (peso) de la arista
     * @return
     */
    @Override
    public boolean agregarArista(V source, V target, D dato) {
        if (!listaDeAdyacencia.containsKey(source) || !listaDeAdyacencia.containsKey(target)) {
            return false;
        }
        return listaDeAdyacencia.get(source).add(new DirectedEdge<>(source, target, dato));
    }

    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) { return false; }

    @Override
    public boolean removerVertice(Comparable<V> criteria) { return false; }

    @Override
    public Set<V> vertices() { return Set.of(); }

    @Override
    public Set<Edge> aristas() { return Set.of(); }

    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) { return false; }

    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) { return null; }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) { return List.of(); }

    @Override
    public boolean esConexo() { return false; }

    @Override
    public void vaciar() {}

    @Override
    public boolean tieneCiclos() { return false; }
}