package ucu.edu.aed.tda.grafo.model.impl;

import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementacion de IFloydWarshallResult, esta clase modela el resultado de
 * ejecutar Floyd (costos minimos) o Warshall (alcanzabilidad).
 *
 * - Si viene de Floyd: tiene "distancias" y "next"; "seLlega" es null.
 * - Si viene de Warshall: tiene "seLlega"; "distancias" y "next" son null.
 *
 * El mapa "indices" traduce vertice -> fila/columna de la matriz.
 * La lista "vertices" hace la traduccion inversa (indice -> vertice), necesaria
 * para reconstruir caminos en getPath.
 *
 * @param <V> tipo generico de los vertices
 */
public class FloydWarshallResult<V> implements IFloydWarshallResult<V> {
    private final double[][] distancias;
    private final boolean[][] seLlega;
    private final int[][] next;
    private final Map<V, Integer> indices;
    private final List<V> vertices;

    public FloydWarshallResult(double[][] distancias, boolean[][] seLlega,
                               int[][] next, Map<V, Integer> indices, List<V> vertices) {
        this.distancias = distancias;
        this.seLlega = seLlega;
        this.next = next;
        this.indices = indices;
        this.vertices = vertices;
    }

    /**
     * Reconstruye el camino minimo usando la matriz "next".
     * Parte de el origen y va saltando al siguiente paso hasta llegar a destino.
     * Si no hay camino (o el resultado vino de Warshall, sin matriz next) retorna lista vacia.
     *
     * @param source vertice de origen
     * @param target vertice de destino
     * @return lista de vertices del camino minimo, o lista vacia si no es alcanzable
     */
    @Override
    public List<V> getPath(V source, V target) {
        Integer i = indices.get(source);
        Integer j = indices.get(target);
        if (i == null || j == null || next == null || next[i][j] == -1) return List.of();

        List<V> camino = new ArrayList<>();
        camino.add(source);
        int actual = i;
        while (actual != j) {
            actual = next[actual][j];
            camino.add(vertices.get(actual));
        }
        return camino;
    }

    /**
     * Retorna el costo minimo.
     * Si algun vertice no existe, o el resultado no tiene distancias, retorna infinito.
     *
     * @param source vertice de origen
     * @param target vertice de destino
     * @return costo del camino minimo, o infinito si no es alcanzable
     */
    @Override
    public double getCost(V source, V target) {
        Integer i = indices.get(source);
        Integer j = indices.get(target);
        if (i == null || j == null || distancias == null) return Double.POSITIVE_INFINITY;
        return distancias[i][j];
    }

    /**
     * Retorna true si existe camino.
     * Si el resultado vino de Warshall lee la matriz booleana;
     * si vino de Floyd, deduce la conexion de la matriz de distancias.
     *
     * @param source vertice de origen
     * @param target vertice de destino
     * @return true si hay camino, false en caso contrario
     */
    @Override
    public boolean connected(V source, V target) {
        Integer i = indices.get(source);
        Integer j = indices.get(target);
        if (i == null || j == null) return false;
        if (seLlega != null) return seLlega[i][j];
        if (distancias != null) return distancias[i][j] != Double.POSITIVE_INFINITY;
        return false;
    }
}