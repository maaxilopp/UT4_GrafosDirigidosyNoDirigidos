package ucu.edu.aed.tda.grafo.model.impl;

import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;

import java.util.List;

public class FloydWarshallResult<V> implements IFloydWarshallResult<V> {
    private final double[][] distancias;

    public FloydWarshallResult(double[][] distancias) {
        this.distancias = distancias;
    }

    @Override
    public List<V> getPath(V source, V target) {
        return List.of();
    }

    @Override
    public double getCost(V source, V target) {
        return 0;
    }

    @Override
    public boolean connected(V source, V target) {
        return false;
    }
}
