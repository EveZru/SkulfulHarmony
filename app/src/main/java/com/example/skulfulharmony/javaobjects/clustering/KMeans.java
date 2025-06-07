package com.example.skulfulharmony.javaobjects.clustering;

import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.List;
import java.util.*;

public class KMeans {
    private int k;
    private int maxIterations;
    private List<DataPoint> dataPoints;
    private List<Cluster> clusters;

    public KMeans(int k, int maxIterations) {
        this.k = k;
        this.maxIterations = maxIterations;
        this.dataPoints = new ArrayList<>();
        this.clusters = new ArrayList<>();
    }

    public void fit(List<DataPoint> points) {
        this.dataPoints = points;
        initializeClusters();
        for (int i = 0; i < maxIterations; i++) {
            assignClusters();
            updateCentroids();
        }
    }

    public int predict(DataPoint point) {
        Cluster nearestCluster = null;
        double minDistance = Double.MAX_VALUE;
        for (Cluster cluster : clusters) {
            double distance = euclideanDistance(point.getFeatures(), cluster.getCentroid());
            if (distance < minDistance) {
                minDistance = distance;
                nearestCluster = cluster;
            }
        }
        return clusters.indexOf(nearestCluster);
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    private void initializeClusters() {
        Random random = new Random();
        Set<Integer> chosenIndexes = new HashSet<>();
        while (clusters.size() < k) {
            int index = random.nextInt(dataPoints.size());
            if (!chosenIndexes.contains(index)) {
                chosenIndexes.add(index);
                clusters.add(new Cluster(dataPoints.get(index).getFeatures()));
            }
        }
    }

    private void assignClusters() {
        for (Cluster cluster : clusters) {
            cluster.clearPoints();
        }
        for (DataPoint point : dataPoints) {
            Cluster nearestCluster = null;
            double minDistance = Double.MAX_VALUE;
            for (Cluster cluster : clusters) {
                double distance = euclideanDistance(point.getFeatures(), cluster.getCentroid());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestCluster = cluster;
                }
            }
            nearestCluster.addPoint(point);
        }
    }

    private void updateCentroids() {
        for (Cluster cluster : clusters) {
            cluster.updateCentroid();
        }
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }
}
