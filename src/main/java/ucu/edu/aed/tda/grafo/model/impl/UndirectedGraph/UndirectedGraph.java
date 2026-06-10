package ucu.edu.aed.tda.grafo.model.impl.UndirectedGraph;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.UndirectedEdge;

import java.util.*;

public class UndirectedGraph<V, D> implements IUndirectedGraph<V, D> {
    private final Map<V, Set<Edge<V, D>>> listaDeAdyacencia = new HashMap<>();

    /**
     * Agrega un vértice validando que no sea nulo y que no exista previamente.
     * Orden: O(1), el acceso al HashMap es directo por hash sin recorrer la lista.
     *
     * @param vertex vértice a agregar
     * @return true si lo agregó, false si era nulo o ya existía
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
     * Recorre todos los vértices y retorna el primero que cumple el criterio.
     * Orden: O(n), en el peor caso recorre todos los vértices sin encontrar match.
     *
     * @param criterio criterio de búsqueda
     * @return el vértice que cumple el criterio, o null si no existe
     */
    @Override
    public V buscarVertice(Comparable<V> criterio) {
        return vertices().stream()
                .filter(v -> criterio.compareTo(v) == 0)
                .findFirst()
                .orElse(null);
    }

    /**
     * Agrega una arista no dirigida entre source y target si ambos vértices existen.
     * Al ser no dirigida, registra el mismo objeto arista en la adyacencia de ambos
     * extremos. Orden: O(1), inserciones directas por hash en el HashMap y los HashSet.
     *
     * @param source un extremo de la arista
     * @param target el otro extremo de la arista
     * @param dato   dato (por ejemplo, el peso) asociado a la arista
     * @return true si la agregó, false si algún extremo no existe o la arista ya estaba
     */
    @Override
    public boolean agregarArista(V source, V target, D dato) {
        if (!listaDeAdyacencia.containsKey(source) || !listaDeAdyacencia.containsKey(target)) {
            return false;
        }
        UndirectedEdge<V, D> arista = new UndirectedEdge<>(source, target, dato);
        boolean agregado = listaDeAdyacencia.get(source).add(arista);
        if (agregado) {
            // Mismo objeto: el equals simétrico lo reconoce desde ambos extremos
            listaDeAdyacencia.get(target).add(arista);
        }
        return agregado;
    }

    /**
     * Elimina la arista no dirigida entre source y target de ambos extremos.
     * Orden: O(n), busca ambos vértices (O(n)) y luego recorre sus adyacencias
     * (O(grado)); en total O(n + grado) que se acota en O(n).
     *
     * @param source un extremo de la arista
     * @param target el otro extremo de la arista
     * @return true si eliminó la arista, false si algún extremo no existe o no había arista
     */
    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        V origen = buscarVertice(source);
        V destino = buscarVertice(target);
        if (origen == null || destino == null) return false;
        // hay que quitarla de los dos lados para mantener la simetría
        boolean removidaOrigen = listaDeAdyacencia.get(origen).removeIf(e -> contieneAmbos(e, origen, destino));
        listaDeAdyacencia.get(destino).removeIf(e -> contieneAmbos(e, origen, destino));
        return removidaOrigen;
    }

    /**
     * Indica si una arista conecta exactamente a los dos vértices dados, sin importar
     * cuál figure como source o target (recordando que la arista es no dirigida).
     */
    private boolean contieneAmbos(Edge<V, D> e, V x, V y) {
        return (e.source().equals(x) && e.target().equals(y))
                || (e.source().equals(y) && e.target().equals(x));
    }

    /**
     * Remueve un vértice y todas las aristas incidentes sobre él.
     * Orden: O(vértices + aristas), debe revisar la adyacencia de todos los vértices
     * para borrar las aristas que tocan al vértice eliminado.
     *
     * @param criteria criterio de búsqueda del vértice a eliminar
     * @return true si el vértice existía y fue removido, false en caso contrario
     */
    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        V vertice = buscarVertice(criteria);
        if (vertice == null) return false;
        listaDeAdyacencia.values().forEach(aristas ->
                aristas.removeIf(e -> e.source().equals(vertice) || e.target().equals(vertice)));
        return listaDeAdyacencia.remove(vertice) != null;
    }

    /**
     * Retorna el conjunto de vértices como vista inmodificable.
     * Orden: O(1), se devuelve la vista de las claves sin recorrerlas.
     *
     * @return vista inmodificable con los vértices del grafo
     */
    @Override
    public Set<V> vertices() {
        return Collections.unmodifiableSet(listaDeAdyacencia.keySet());
    }

    /**
     * Retorna todas las aristas del grafo. Como cada arista no dirigida se almacena
     * con el mismo objeto en ambos extremos y el equals es simétrico, el HashSet
     * resultado no las duplica.
     * Orden: O(vértices + aristas), recorre toda la lista de adyacencia.
     *
     * @return conjunto con las aristas del grafo (cada (u,v) aparece una sola vez)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<Edge> aristas() {
        Set<Edge> result = new HashSet<>();
        listaDeAdyacencia.values().forEach(result::addAll);
        return result;
    }

    /**
     * Verifica si existe una arista no dirigida entre dos vértices.
     * Orden: O(n), delega en obtenerArista.
     *
     * @param sourceCriteria criterio de un extremo
     * @param targetCriteria criterio del otro extremo
     * @return true si existe la arista, false en caso contrario
     */
    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        return obtenerArista(sourceCriteria, targetCriteria) != null;
    }

    /**
     * Retorna la arista no dirigida que conecta los dos vértices indicados, si existe.
     * Orden: O(n), busca el vértice origen (O(n)) y filtra sus adyacencias (O(grado)).
     *
     * @param sourceCriteria criterio de un extremo
     * @param targetCriteria criterio del otro extremo
     * @return la arista entre ambos vértices, o null si no existe o algún extremo no existe
     */
    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V origen = buscarVertice(sourceCriteria);
        V destino = buscarVertice(targetCriteria);
        if (origen == null || destino == null) return null;
        return listaDeAdyacencia.get(origen).stream()
                .filter(e -> contieneAmbos(e, origen, destino))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retorna las aristas incidentes sobre el vértice indicado. Para grafos no
     * dirigidos cada arista devuelta tiene al vértice consultado como uno de sus
     * extremos (en source o target indistintamente).
     * Orden: O(n), busca el vértice (O(n)) y copia sus adyacencias (O(grado)).
     *
     * @param verticeCriteria criterio del vértice cuyas aristas se quieren listar
     * @return lista de aristas incidentes, o lista vacía si el vértice no existe
     */
    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        V vertice = buscarVertice(verticeCriteria);
        if (vertice == null) return List.of();
        return new ArrayList<>(listaDeAdyacencia.get(vertice));
    }

    /**
     * Indica si el grafo es conexo, es decir, si todos sus vértices son alcanzables
     * desde uno cualquiera mediante un recorrido en profundidad. Un grafo vacío se
     * considera conexo.
     * Orden: O(vértices + aristas), un único recorrido en profundidad.
     *
     * @return true si el grafo es conexo o está vacío, false en caso contrario
     */
    @Override
    public boolean esConexo() {
        if (listaDeAdyacencia.isEmpty()) return true;
        Set<V> visitados = new HashSet<>();
        V inicio = listaDeAdyacencia.keySet().iterator().next();
        profundidad(inicio, visitados);
        return visitados.size() == listaDeAdyacencia.size();
    }

    /**
     * Recorrido en profundidad auxiliar que marca todos los vértices alcanzables
     * desde v, visitando el vecino que no es el vértice actual en cada arista.
     */
    private void profundidad(V v, Set<V> visitados) {
        visitados.add(v);
        for (Edge<V, D> e : listaDeAdyacencia.get(v)) {
            V vecino = e.source().equals(v) ? e.target() : e.source();
            if (!visitados.contains(vecino)) profundidad(vecino, visitados);
        }
    }

    /**
     * Vacía el grafo eliminando todos sus vértices y aristas.
     * Orden: O(1) amortizado sobre la estructura interna del HashMap.
     */
    @Override
    public void vaciar() {
        listaDeAdyacencia.clear();
    }

    /**
     * Indica si el grafo no dirigido contiene algún ciclo. Recorre en profundidad
     * pasando el vértice "padre" para no confundir la arista por la que se llegó con
     * un ciclo: si se alcanza un vértice ya visitado que no es el padre, hay ciclo.
     * Orden: O(vértices + aristas), recorre cada arista una vez por componente.
     *
     * @return true si existe al menos un ciclo, false si el grafo es acíclico (un bosque)
     */
    @Override
    public boolean tieneCiclos() {
        Set<V> visitados = new HashSet<>();
        for (V v : listaDeAdyacencia.keySet()) {
            if (!visitados.contains(v) && ciclosProfundidad(v, null, visitados)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recorrido en profundidad que detecta ciclos en un grafo no dirigido.
     * Visita los vecinos de v ignorando al padre (la arista de regreso). Si encuentra
     * un vecino ya visitado distinto del padre, ese vecino cierra un ciclo.
     */
    private boolean ciclosProfundidad(V v, V padre, Set<V> visitados) {
        visitados.add(v);
        for (Edge<V, D> e : listaDeAdyacencia.get(v)) {
            V vecino = e.source().equals(v) ? e.target() : e.source();
            if (!visitados.contains(vecino)) {
                if (ciclosProfundidad(vecino, v, visitados)) return true;
            } else if (!vecino.equals(padre)) {
                return true;
            }
        }
        return false;
    }
}