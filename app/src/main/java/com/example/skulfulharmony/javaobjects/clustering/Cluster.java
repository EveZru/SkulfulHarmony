package com.example.skulfulharmony.javaobjects.clustering;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private double[] centroid;
    private List<DataPoint> points;

    public Cluster(double[] centroid) {
        this.centroid = centroid;
        this.points = new ArrayList<>();
    }

    public void addPoint(DataPoint point) {
        points.add(point);
    }

    public void clearPoints() {
        points.clear();
    }

    public void updateCentroid() {
        if (points.isEmpty()) return;

        int dimensions = centroid.length;
        double[] newCentroid = new double[dimensions];

        for (DataPoint point : points) {
            double[] features = point.getFeatures();
            for (int i = 0; i < dimensions; i++) {
                newCentroid[i] += features[i];
            }
        }

        for (int i = 0; i < dimensions; i++) {
            newCentroid[i] /= points.size();
        }

        this.centroid = newCentroid;
    }

    public double[] getCentroid() {
        return centroid;
    }
}
