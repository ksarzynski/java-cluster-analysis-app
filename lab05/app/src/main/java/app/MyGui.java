package app;

import api.ClusterAnalysisService;
import api.ClusteringException;
import api.DataSet;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class MyGui {

    GraphPanel graphPanel = new GraphPanel();
    List<ClusterAnalysisService> myServices = new ArrayList<>(2);

    // data set //

    DataSet dataset = new DataSet();
    private static final int xLimit = 550;
    private static final int yLimit = 400;
    int pointsAmount = 0;
    int minX = 0;
    int maxX = 0;
    int minY = 0;
    int maxY = 0;

    // -------- //

    public MyGui(){
        init();
    }

    private void init(){
        JFrame frame = new JFrame("lab 5");
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        frame.add(mainPanel);
        JPanel sizePanel = new JPanel(new GridLayout(2, 1));
        sizePanel.add(initOptionsPanel());
        mainPanel.add(sizePanel);
        mainPanel.add(graphPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,900);
        frame.setVisible(true);
        ServiceLoader<ClusterAnalysisService> loader = ServiceLoader.load(ClusterAnalysisService.class);
        for(ClusterAnalysisService myService : loader)
        {
            myServices.add(myService);
            System.out.println(myService.getName());
        }
    }

    private JPanel initOptionsPanel(){

        JPanel optionsPanel = new JPanel(new GridLayout(3,3));
        JLabel sizeLabel = new JLabel("enter size: ");
        JLabel minLabel = new JLabel("enter min x/y value: (%)");
        JLabel maxLabel = new JLabel("enter max x/y value: (%)");
        JTextField size = new JTextField("100");
        JTextField minT = new JTextField("0");
        JTextField maxT = new JTextField("100");
        JButton accept = new JButton("accept data");
        accept.addActionListener(e -> {
            this.pointsAmount = Integer.parseInt(size.getText());
            String minXString = String.valueOf(Double.parseDouble(minT.getText()) * xLimit / 100);
            this.minX = Integer.parseInt(minXString.substring(0, minXString.indexOf('.')));
            String maxXString = String.valueOf(Double.parseDouble(maxT.getText()) * xLimit / 100);
            this.maxX = Integer.parseInt(maxXString.substring(0, maxXString.indexOf('.')));
            String minYString = String.valueOf(Double.parseDouble(minT.getText()) * yLimit / 100);
            this.minY = Integer.parseInt(minYString.substring(0, minYString.indexOf('.')));
            String maxYString = String.valueOf(Double.parseDouble(maxT.getText()) * yLimit / 100);
            this.maxY = Integer.parseInt(maxYString.substring(0, maxYString.indexOf('.')));
        });
        JButton kmeans = new JButton("kmeans");
        kmeans.addActionListener(e -> {
            ClusterAnalysisService impl = myServices.get(0);
            try {
                dataset = DataSetGenerator.generateDataSet(pointsAmount, minX, maxX, minY, maxY);
                String[] options = new String[1];
                options[0] = String.valueOf(pointsAmount / 10);
                impl.setOptions(options);
                impl.submit(dataset);
                graphPanel.setPoints(getData(dataset));
                graphPanel.setClusters(getData(impl.retrieve(true)));
                graphPanel.setDistances(impl.getDistances());

            } catch (ClusteringException clusteringException) {
                clusteringException.printStackTrace();
            }
            graphPanel.draw((Graphics2D) graphPanel.getGraphics());
        });
        JButton todo = new JButton("HAC");
        todo.addActionListener(e -> {
            ClusterAnalysisService impl = myServices.get(1);
            try {
                System.out.println("HAC working");
                dataset = DataSetGenerator.generateDataSet(pointsAmount, minX, maxX, minY, maxY);
                String[] options = new String[1];
                options[0] = String.valueOf(pointsAmount / 10);
                impl.setOptions(options);
                impl.submit(dataset);
                graphPanel.setPoints(getData(dataset));
                graphPanel.setClusters(getData(impl.retrieve(true)));
                System.out.println(getData(impl.retrieve(true)));
                graphPanel.setDistances(impl.getDistances());

            } catch (ClusteringException clusteringException) {
                clusteringException.printStackTrace();
            }
            graphPanel.draw((Graphics2D) graphPanel.getGraphics());
        });
        optionsPanel.add(sizeLabel);
        optionsPanel.add(minLabel);
        optionsPanel.add(maxLabel);
        optionsPanel.add(size);
        optionsPanel.add(minT);
        optionsPanel.add(maxT);
        optionsPanel.add(accept);
        optionsPanel.add(kmeans);
        optionsPanel.add(todo);
        return optionsPanel;
    }

    private ArrayList<Point> getData(DataSet dataSet){
        ArrayList<Point> ret = new ArrayList<>();
        for(String[] point : dataSet.getData()){
            String xString = point[0];
            String yString = point[1];
            if(xString != null && yString != null)
                ret.add(new Point(Integer.parseInt(xString.substring(0, xString.indexOf('.'))),
                        Integer.parseInt(yString.substring(0, yString.indexOf('.')))));
        }
        return ret;
    }
}
