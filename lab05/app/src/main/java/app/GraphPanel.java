package app;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphPanel extends JPanel {

    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<Point> clusters = new ArrayList<>();
    private ArrayList<Double> distances = new ArrayList<>();
    private static final int pointSize = 10;
    private static final int clusterSize = 20;

    public GraphPanel(){
        this.setSize(600, 450);
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public void setClusters(ArrayList<Point> clusters) {
        this.clusters = clusters;
    }

    public void setDistances(ArrayList<Double> distances) {
        this.distances = distances;
    }

    public void draw(Graphics2D g){
        drawBackground(g);
        drawGraph(g);
        drawPoints(g);
        drawClusters(g);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        draw(g2d);
    }

    private void drawGraph(Graphics2D g){
        g.setColor(Color.black);
        g.fillRect(298, 0, 4, 450);
        g.fillRect(0, 223, 600, 4);
    }

    private void drawPoints(Graphics2D g){
        g.setColor(Color.red);
        for(Point point : points){
            g.fillOval(point.x, point.y, pointSize, pointSize);
        }
    }

    private void drawClusters(Graphics2D g){
        g.setColor(Color.green);
        for(Point cluster : clusters){
            String sizeS = String.valueOf(distances.get(clusters.indexOf(cluster)));
            int size = Integer.parseInt(sizeS.substring(0, sizeS.indexOf('.')));
            g.fillOval(cluster.x, cluster.y, clusterSize, clusterSize);
            g.drawOval(cluster.x - size / 2 + 10, cluster.y - size / 2+ 10, size, size);
        }
    }

    private void drawBackground(Graphics2D g){
        g.setColor(Color.white);
        g.fillRect(0, 0, 600, 450);
    }

    private ArrayList<Point> getPoints() {
        return points;
    }

    private ArrayList<Point> getClusters() {
        return clusters;
    }
}
