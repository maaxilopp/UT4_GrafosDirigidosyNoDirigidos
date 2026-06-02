package ucu.edu.aed.tda.grafo.model.impl;

import ucu.edu.aed.tda.grafo.IDirectedIGraph;
import ucu.edu.aed.tda.grafo.model.edge.DirectedEdge;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

import java.util.*;

public class DirectedIGraph<V, D> implements IDirectedIGraph<V, D> {
    private final Map<V, Set<Edge<V, D>>> listaDeAdyacencia = new HashMap<>();

    /**
     * Busca el vertice que cumple el criterio de comparación, y si lo encuentra,
     * retorna un hashset con los sucesores de ese vertice, si no lo encuentra,
     * retorna un hashset vacío. Orden O(n), ya que usa buscarVertice que es O(n)
     * y luego recorre las aristas del vertice encontrado para obtener sus sucesores,
     * lo que es O(grado del vertice), en total quedaría O(n + grado del vertice), se redondea, como vimos en clase, en O(n).
     *
     * @param criteria criterio de busca del nodo al que se le listarán los sucesores
     * @return un hashset con los sucesores del nodo que cumple el criterio de comparación, o un hashset vacío
     * dependiendo de si existe o no el nodo en la lista de adyacencia, respectivamente.
     */
    @Override
    public Set<V> successors(Comparable<V> criteria) {
        V vertice = buscarVertice(criteria);
        if (vertice == null) {
            return Set.of();
        }
        Set<V> sucesores = new HashSet<>();
        listaDeAdyacencia.get(vertice).forEach(e -> sucesores.add(e.target()));
        return sucesores;
    }

    /**
     * Busca el vertice que cumple el criterio de comparación, y si lo encuentra,
     * retorna un hashset con los predecesores de ese vertice, si no lo encuentra,
     * retorna un hashset vacío. El orden acá es O(Vertices +Aristas) porque recorre
     * todos los vértices y todas sus aristas, no solo las del vértice buscado.
     *
     * @param criteria criterio de busca del nodo al que se le listará los predecesores.
     * @return un hashset con los predecesores del nodo que cumple el criterio de comparación, o un hashset vacío
     */
    @Override
    public Set<V> predecessors(Comparable<V> criteria) {
        V vertice = buscarVertice(criteria);
        if (vertice == null) {
            return Set.of();
        }
        Set<V> predecesores = new HashSet<>();
        listaDeAdyacencia.forEach((v, aristas) -> {
            aristas.forEach(e -> {
                if (e.target().equals(vertice)) {
                    predecesores.add(v);
                }
            });
        });
        return predecesores;
    }

    /**
     * Intenta agregar un vertice al nodo validando que no sea nulo y
     * que ya la clave no este en la lista de adyacencia orden O(1),
     * calcula el indice directo mediante hash sin recorrer toda la lista.
     *
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
     *
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
     *
     * @param source Vertice de origen de la arista
     * @param target Vertice destino de la arista
     * @param dato   info (peso) de la arista
     * @return true o false de acuerdo a si pudo o no agregarla.
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
     *
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
     *
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

    /**
     * Devuelve las claves del hashmap de la lista de adyacencia, osea los vertices.
     * Orden: O(1) ya que se devuelve una vista inmodificable de las claves del hashmap sin necesidad de recorrerlo.
     *
     * @return conjunto de claves de la lista de adyacencia, osea los vertices del grafo.
     */
    @Override
    public Set<V> vertices() {
        return Collections.unmodifiableSet(listaDeAdyacencia.keySet());
    }

    @Override
    public Set<Edge> aristas() {
        return Set.of();
    }

    /**
     * Verifica si existe una arista entre dos vertices, validando que ambos
     * existan en la lista de adyacencia y que la arista exista entre ellos.
     * Orden: O(n), usa buscarVertice, el contains es O(grado del vertice),
     * en total quedaría O(n + grados del vertice), se redondea, como vimos en clase, en O(n).
     *
     * @param sourceCriteria criterio de origen de la arista
     * @param targetCriteria criterio de a donde apunta la arista
     * @return true o false, depende de si existe o no la arista.
     */
    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        return obtenerArista(sourceCriteria, targetCriteria) != null;
    }

    /**
     * Si existe tanto el origen como el destino de una arista en la lista de adyacencia,
     * busca si esa arista existe entre ambos, y si existe la retorna, en caso de que no exista, se retorna null.
     * Orden: O(n), usa buscarVertice, el filter es O(grado del vertice), en total quedaría O(n + grados del vertice),
     * se redondea, como vimos en clase, en O(n).
     *
     * @param sourceCriteria criterio de origen de la arista.
     * @param targetCriteria criterio de a donde apunta la arista.
     * @return null o una arista en consecuencia a si existe o no en el grafo.
     */
    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V origen = buscarVertice(sourceCriteria);
        V destino = buscarVertice(targetCriteria);
        if (origen == null || destino == null) return null;
        return listaDeAdyacencia.get(origen).stream()
                .filter(e -> targetCriteria.compareTo(e.target()) == 0)
                .findFirst()
                .orElse(null);
    }

    /**
     * Si existe el vertice del criterio en la lista de adyacencia, retorna una lista
     * con todas las aristas salientes de el. Orden O(n), usa buscarVertice, el filter
     * es O(grado del vertice), en total quedaría O(n + grados del vertice),
     *
     * @param verticeCriteria criterio de busqueda del vertice al que se le listarán las adyacencias
     * @return null si no existe el vertice en la lista de adyacencia, retorna una lista vacia, sino
     * una lista con las aristas salientes de el vertice.
     */
    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        V vertice = buscarVertice(verticeCriteria);
        if (vertice == null) return List.of();
        return new ArrayList<>(listaDeAdyacencia.get(vertice));
    }

    /**
     * Usa un metodo recursivo de recorrer en profundidad un grafo dirigido, utilizando un hashset para controlar los vértices visitados y corroborar en O(1)
     * y evitar ciclos infinitos. Orden: O(Vertices + Aristas) ya que se visitan todos los vértices y aristas del grafo exactamente una vez.
     *
     * @return true o false, true si es conexo o la lista es vacia, false en caso contrario.
     */
    @Override
    public boolean esConexo() {
        if (listaDeAdyacencia.isEmpty()) return true;
        Set<V> visitados = new HashSet<>();
        V inicio = listaDeAdyacencia.keySet().iterator().next();
        profundidad(inicio, visitados);
        return visitados.size() == listaDeAdyacencia.size();
    }

    private void profundidad(V v, Set<V> visitados) {
        visitados.add(v);
        listaDeAdyacencia.get(v).forEach(e -> {
            if (!visitados.contains(e.target())) profundidad(e.target(), visitados);
        });
    }


    /**
     * Vacia la lista de adyacencia eliminando todos los vertices y aristas que existían en ella.
     * Orden: O(n), recorre todo el hashmap y poniendole null a todos los pares clave-valor.
     */
    @Override
    public void vaciar() {
        listaDeAdyacencia.clear();
    }

    /**
     * Verifica si un grafo, representado en lista de adyacencia, tiene ciclos usando un recorrido
     * en profundidad. Manteniene un hashset de vertices en curso para detectar si se vuelve a visitar
     * un vertice ya visitado en la recursion actual.
     * Orden: O(vertices + aristas) ya que recorre todos los vertices y aristas del grafo.
     * @return true o false, en base a si tiene ciclos o no el grafo.
     */
    @Override
    public boolean tieneCiclos() {
        Set<V> yaVisitados = new HashSet<>();
        Set<V> visitando = new HashSet<>();
        for (V v : listaDeAdyacencia.keySet()) {
            if (ciclosProfundidad(v, yaVisitados, visitando)) return true;
        }
        return false;
    }

    private boolean ciclosProfundidad(V v, Set<V> yaVisitados, Set<V> visitando) {
        if (visitando.contains(v)) return true;
        if (yaVisitados.contains(v)) return false;
        yaVisitados.add(v);
        visitando.add(v);
        for (Edge<V, D> e : listaDeAdyacencia.get(v)) {
            if (ciclosProfundidad(e.target(), yaVisitados, visitando)) return true;
        }
        visitando.remove(v);
        return false;
    }
}