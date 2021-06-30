package impl;

import api.ClusterAnalysisService;
import api.ClusteringException;
import api.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Impl implements ClusterAnalysisService {

    private int clustersAmount;
    private List<Cluster> result = null;

    private class Point{

        private double x;
        private double y;


        Point(double x, double y){
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }


        double getDistance(Cluster c){

            return Math.sqrt(Math.pow((this.x - c.getX()), 2) + Math.pow((this.y - c.getY()), 2));
        }
    }

    private class Cluster extends Point{

        private ArrayList<Point> assignedPoints;
        private Boolean toBeUpdated;
        public double distance;

        Cluster(double x, double y) {
            super(x, y);
            assignedPoints = new ArrayList<>();
            toBeUpdated = false;
        }

        public ArrayList<Point> getAssignedPoints() {
            return assignedPoints;
        }

        public void setAssignedPoints(ArrayList<Point> assignedPoints) {
            this.assignedPoints = assignedPoints;
        }

        public Boolean getToBeUpdated() {
            return toBeUpdated;
        }

        public void setToBeUpdated(Boolean toBeUpdated) {
            this.toBeUpdated = toBeUpdated;
        }

        private void setDistances(){
            double max = 0;
            for(Point point : assignedPoints){
                if(point.getDistance(this) > max){
                    max = point.getDistance(this);
                }
            }
            distance = max;
        }
    }

    public ArrayList<Double> getDistances(){
        ArrayList<Double> ret = new ArrayList<>();
        for(Cluster cluster : result){
            cluster.setDistances();
            ret.add(cluster.distance);
        }
        return ret;
    }

    @Override
    public void setOptions(String[] options) throws ClusteringException {
        clustersAmount = Integer.parseInt(options[0]);
    }

    @Override
    public String getName() {
        return "KMeans\n";
    }

    private List<Point> getPoints(DataSet ds){

        List<Point> pointsList = new ArrayList<>();

        for(String[] s : ds.getData()) {
            Point p = new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
            pointsList.add(p);
        }

        return pointsList;
    }

    private double[] getXLimits(DataSet ds){
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        for(String[] s : ds.getData()) {

            Point p = new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
            if (p.getX() < minX)
                minX = p.getX();
            if (p.getX() > maxX)
                maxX = p.getX();
        }
        return new double[]{minX, maxX};
    }

    private double[] getYLimits(DataSet ds){
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        for(String[] s : ds.getData()) {

            Point p = new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
            if (p.getY() < minY)
                minY = p.getY();
            if (p.getY() > maxY)
                maxY = p.getY();
        }
        return new double[]{minY, maxY};
    }

    private List<Cluster> generateRandomClusters(int index, double minX, double maxX, double minY, double maxY){
        List<Cluster> clustersList = new ArrayList<>();
        for(int i = 0; i < index; i++){
            Random r = new Random();
            Cluster c = new Cluster(minX + (maxX - minX) * r.nextDouble(),
                    minY + (maxY - minY) * r.nextDouble());
            c.setAssignedPoints(new ArrayList<>());
            clustersList.add(c);
        }
        return clustersList;
    }

    private void assignPoints(List<Point> pointsList, List<Cluster> clustersList){
        int indexOfCluster = 0;
        for(Cluster cluster : clustersList)
            cluster.setToBeUpdated(false);
        for(Point point : pointsList){
            double minDistance = 999999;
            for(Cluster cluster : clustersList){
                if(point.getDistance(cluster) < minDistance){
                    minDistance = point.getDistance(cluster);
                    indexOfCluster = clustersList.indexOf(cluster);
                }
            }
            clustersList.get(indexOfCluster).setToBeUpdated(true);
            clustersList.get(indexOfCluster).getAssignedPoints().add(point);
        }
    }

    private void relocateClusters(List<Cluster> clustersList, List<Cluster> clustersLastPositionsList){
        for(Cluster cluster : clustersList){
            if(cluster.getToBeUpdated()){
                clustersLastPositionsList.get(clustersList.indexOf(cluster)).setX(cluster.getX());
                clustersLastPositionsList.get(clustersList.indexOf(cluster)).setY(cluster.getY());
                double averageX = 0;
                double averageY = 0;
                for(Point point : cluster.getAssignedPoints()){
                    averageX += point.getX();
                    averageY += point.getY();
                }
                averageX /= Double.parseDouble(String.valueOf(cluster.getAssignedPoints().size()));
                averageY /= Double.parseDouble(String.valueOf(cluster.getAssignedPoints().size()));
                cluster.setX(averageX);
                cluster.setY(averageY);
            }
        }
    }

    private boolean checkChanges(List<Cluster> clustersList, List<Cluster> clustersLastPositionsList){
        for(Cluster cluster : clustersList){
            if(clustersLastPositionsList.get(clustersList.indexOf(cluster)).getX() != cluster.getX() ||
                    clustersLastPositionsList.get(clustersList.indexOf(cluster)).getY() != cluster.getY()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void submit(DataSet ds) throws ClusteringException {

        List<Point> pointsList = getPoints(ds);
        List<Cluster> clustersList = generateRandomClusters(clustersAmount, getXLimits(ds)[0], getXLimits(ds)[1],
                getYLimits(ds)[0], getXLimits(ds)[1]);
        List<Cluster> clustersLastPositionsList = List.copyOf(clustersList);

        boolean running = true;
        while(running){
            assignPoints(pointsList, clustersList);
            relocateClusters(clustersList, clustersLastPositionsList);
            running = checkChanges(clustersList, clustersLastPositionsList);
        }

        result = List.copyOf(clustersList);
    }

    @Override
    public DataSet retrieve(boolean clear) throws ClusteringException {

        DataSet dataSet = new DataSet();
        String[][] ret = new String[clustersAmount][2];
        for(int i = 0; i < clustersAmount; i++){
            Cluster cluster = result.get(i);
            ret[i][0] = String.valueOf(cluster.getX());
            ret[i][1] = String.valueOf(cluster.getY());
        }
        dataSet.setData(ret);
        return dataSet;
    }

    public static void main(String[] args) {

        System.out.println("class works");
    }
}
