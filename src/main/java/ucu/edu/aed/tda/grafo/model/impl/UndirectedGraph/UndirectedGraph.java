package ucu.edu.aed.tda.grafo.model.impl.UndirectedGraph;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.UndirectedEdge;

import java.util.*;

public abstract class UndirectedGraph<V, D>  implements IUndirectedGraph<V, D> {
    private final Map<V, Set<Edge<V, D>>> listaDeAdyacencia = new HashMap<>();

    @Override
    public boolean agregarVertice(Object vertex) {
        if (vertex == null || listaDeAdyacencia.containsKey(vertex)) {
            return false;
        }
        listaDeAdyacencia.put((V) vertex, new HashSet<>());
        return true;
    }

    @Override
    public V buscarVertice(Comparable criterio) {
        return vertices().stream()
                .filter(v -> criterio.compareTo(v) == 0)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean agregarArista(V source, V target, D dato) {
        if (!listaDeAdyacencia.containsKey(source) || !listaDeAdyacencia.containsKey(target)) {
            return false;
        }

        UndirectedEdge<V,D> arista = new UndirectedEdge<>(source, target, dato);

        boolean agregado = listaDeAdyacencia.get(source).add(arista);

        if (agregado) {
            // Mismo objeto, el equals simétrico lo maneja en ambos lados
            listaDeAdyacencia.get(target).add(arista);
        }

        return agregado;
    }

    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        V origen = buscarVertice(source);
        V destino = buscarVertice(target);
        if (origen == null || destino == null) return false;
        return listaDeAdyacencia.get(origen).removeIf(e -> target.compareTo(e.target()) == 0);
    }

    @Override
    public boolean removerVertice(Comparable criteria) {
        return false;
    }

    @Override
    public Set vertices() {
        return Set.of();
    }

    @Override
    public Set<Edge> aristas() {
        return Set.of();
    }

    @Override
    public boolean existeArista(Comparable sourceCriteria, Comparable targetCriteria) {
        return false;
    }

    @Override
    public Edge obtenerArista(Comparable sourceCriteria, Comparable targetCriteria) {
        return null;
    }

    @Override
    public List<Edge> adyacencias(Comparable verticeCriteria) {
        return List.of();
    }

    @Override
    public boolean esConexo() {
        return false;
    }

    @Override
    public void vaciar() {

    }

    @Override
    public boolean tieneCiclos() {
        return false;
    }
}
