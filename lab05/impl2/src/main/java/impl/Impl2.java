package impl;

import api.ClusterAnalysisService;
import api.ClusteringException;
import api.DataSet;

import java.util.*;

public class Impl2 implements ClusterAnalysisService {

    public int clusterAmount;
    private ArrayList<Cluster> result = new ArrayList<>();

    private class Point{

        Point(double x, double y){
            this.x = x;
            this.y = y;
        }

        double x;
        double y;
    }

    private class Cluster extends Point{

        boolean considered;
        ArrayList<Cluster> assignedClusters = new ArrayList<>();
        double distance;

        Cluster(double x, double y) {
            super(x, y);
            this.considered = false;
        }

        double getDistance(Cluster c){

            return Math.sqrt(Math.pow((this.x - c.x), 2) + Math.pow((this.y - c.y), 2));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cluster cluster = (Cluster) o;
            return (this.x == cluster.x) && (this.y == cluster.y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(considered);
        }

        private void setDistances(){
            double max = 0;
            for(Cluster cluster : assignedClusters){
                if(cluster.getDistance(this) > max){
                    max = cluster.getDistance(this);
                }
            }
            distance = max;
        }
    }


    @Override
    public void setOptions(String[] options) throws ClusteringException {
        clusterAmount = Integer.parseInt(options[0]);
    }

    @Override
    public String getName() {
        return "HAC\n";
    }


    private double[] getXLimits(DataSet ds){
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        for(String[] s : ds.getData()) {

            Point p = new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
            if (p.x < minX)
                minX = p.x;
            if (p.x > maxX)
                maxX = p.x;
        }
        return new double[]{minX, maxX};
    }

    private double[] getYLimits(DataSet ds){
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        for(String[] s : ds.getData()) {

            Point p = new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
            if (p.y < minY)
                minY = p.y;
            if (p.y > maxY)
                maxY = p.y;
        }
        return new double[]{minY, maxY};
    }

    @Override
    public void submit(DataSet ds) throws ClusteringException {

        result = new ArrayList<>();
        List<Point> pointsList = getPoints(ds);
        List<Cluster> clusterList = new ArrayList<>();
        for(Point point : pointsList){
            clusterList.add(new Cluster(point.x, point.y));
        }

        boolean running = true;
        while(running){
            boolean doAdd = false;
            ArrayList<Cluster> tempClusters = new ArrayList<>();
            for(Cluster cluster : clusterList){
                double minDistance = 999999;
                int temp = 0;
                for(Cluster nextCluster : clusterList){
                    if(cluster.getDistance(nextCluster) < minDistance && !cluster.equals(nextCluster) &&
                            !cluster.considered && !nextCluster.considered){
                        minDistance = cluster.getDistance(nextCluster);
                        temp = clusterList.indexOf(nextCluster);
                        doAdd = true;
                    }
                }
                if(doAdd){
                    Cluster tempCluster = new Cluster((cluster.x + clusterList.get(temp).x) / 2,
                            (cluster.y + clusterList.get(temp).y) / 2);
                    tempCluster.assignedClusters.add(cluster);
                    tempCluster.assignedClusters.addAll(cluster.assignedClusters);
                    tempCluster.assignedClusters.add(clusterList.get(temp));
                    tempCluster.assignedClusters.addAll(clusterList.get(temp).assignedClusters);
                    tempClusters.add(tempCluster);
                    cluster.considered = true;
                    clusterList.get(temp).considered = true;
                    doAdd = false;
                }
            }
            clusterList.addAll(tempClusters);
            int considered = 0;
            for(Cluster cluster : clusterList){
                if(!cluster.considered)
                    considered++;
            }
            if(considered <= clusterAmount){

                for(Cluster cluster : clusterList){
                    if(!cluster.considered)
                        result.add(cluster);
                }
                running = false;
            }
        }
    }

    @Override
    public DataSet retrieve(boolean clear) throws ClusteringException {
        System.out.println("retrieve works");
        DataSet dataSet = new DataSet();
        String[][] ret = new String[clusterAmount][2];
        System.out.println("ClusterAMount: " + clusterAmount + " result.size()" + result.size());
        for(int i = 0; i < result.size(); i++){
            if(i < ret.length){
                Cluster cluster = result.get(i);
                ret[i][0] = String.valueOf(cluster.x);
                ret[i][1] = String.valueOf(cluster.y);
            }
        }
        dataSet.setData(ret);
        return dataSet;
    }

    private List<Point> getPoints(DataSet ds){

        List<Point> pointsList = new ArrayList<>();

        for(String[] s : ds.getData()) {
            Point p = new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
            pointsList.add(p);
        }

        return pointsList;
    }

    @Override
    public ArrayList<Double> getDistances(){
        ArrayList<Double> ret = new ArrayList<>();
        for(Cluster cluster : result){
            cluster.setDistances();
            ret.add(cluster.distance);
        }
        return ret;
    }

    public static void main(String[] args) {

        System.out.println("class works");
    }
}
