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

    /**
     * Intenta eliminar una arista del grafo, validando que tanto el origen como el destino
     * sean parte de la lista de adyacencia, en caso de que si, si existe esa arista se retorna true,
     * en caso de que no exista ya sea el destino o origen o la arista en la lista, se retorna false.
     * Orden: O(n), usa buscarVertice en dos for no anidadados, el removeIf es O(grado del vertice),
     * en total quedaría O(n + n + grados del vertice), se redondea, como vimos en clase, en O(n).
     * @param source origen de la arista
     * @param target destino de la arista
     * @return true o false dependiendo de si pudo eliminar la arista o no, respectivamente.
     */
    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        V origen = buscarVertice(source);
        V destino = buscarVertice(target);
    if (origen == null || destino == null) return false;
    return listaDeAdyacencia.get(origen).removeIf(e -> target.compareTo(e.target()) == 0);
    }

    /**
     * Intenta eliminar un vertice y todas sus aristas asociadas, validando que el vertice exista en
     * la lista de adyacencia, si no existe se retorna false, en caso de que exista, se eliminan todas
     * las aristas que tengan como destino a ese vertice, y luego se elimina el vertice de la lista de
     * adyacencia. Orden: O(vertices + aristas) porque revisa todas las aristas y vertices de la lista
     * para asegurarse de que  sea exterminado por completo, no dejando rastros de su existencia en el grafo.
     * @param criteria criterio de busqueda
     * @return true o false, dependiendo de las circunstancias.
     */
    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        V vertice = buscarVertice(criteria);
        if (vertice == null) return false;
        listaDeAdyacencia.values().forEach(aristas -> aristas.removeIf(e -> e.target().equals(vertice)));
        return listaDeAdyacencia.remove(vertice) != null;
    }

    @Override
    public Set<V> vertices() { return Set.of(); }

    @Override
    public Set<Edge> aristas() { return Set.of(); }

    /**
     *Verifica si existe una arista entre dos vertices, validando que ambos
     *existan en la lista de adyacencia y que la arista exista entre ellos.
     * Orden: O(n), usa buscarVertice, el contains es O(grado del vertice),
     * en total quedaría O(n + grados del vertice), se redondea, como vimos en clase, en O(n).
     * @param sourceCriteria criterio de origen de la arista
     * @param targetCriteria criterio de a donde apunta la arista
     * @return true o false, depende de si existe o no la arista.
     */
    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        return obtenerArista(sourceCriteria, targetCriteria) != null;
    }

    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) { return null; }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) { return List.of(); }

    @Override
    public boolean esConexo() { return false; }


    /**
     * Vacia la lista de adyacencia eliminando todos los vertices y aristas que existían en ella.
     * Orden: O(n), recorre todo el hashmap y poniendole null a todos los pares clave-valor.
     */
    @Override
    public void vaciar() {
        listaDeAdyacencia.clear();
    }

    @Override
    public boolean tieneCiclos() { return false; }
}