package com.example.skulfulharmony.javaobjects.clustering;

public class DataPoint {
    private double[] features;

    public DataPoint(double[] features) {
        this.features = features;
    }

    public double[] getFeatures() {
        return features;
    }
}