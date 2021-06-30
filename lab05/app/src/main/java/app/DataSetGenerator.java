package app;

import api.DataSet;

import java.util.Random;

public class DataSetGenerator {

    public static DataSet generateDataSet(int size, int minX, int maxX, int minY, int maxY){

        Random random = new Random();
        DataSet dataSet = new DataSet();
        String[][] data = new String[size][2];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < 2; j++){
                data[i][0] = String.valueOf(minX + (maxX - minX) * random.nextDouble());
                data[i][1] = String.valueOf(minY + (maxY - minY) * random.nextDouble());
            }
        }
        dataSet.setData(data);
        return dataSet;
    }
}
