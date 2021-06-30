package api;

import api.ClusteringException;
import api.DataSet;

import java.util.ArrayList;

public interface ClusterAnalysisService {
    public void setOptions(String[] options) throws ClusteringException; // ustawia opcje
    // metoda zwracająca nazwę algorytmu
    public String getName();
    // metoda pozwalająca przekazać dane do analizy
    // wyrzuca wyjątek, jeśli aktualnie trwa przetwarzanie danych
    public void submit(DataSet ds) throws ClusteringException;
    // metoda pozwalająca pobrać wynik analizy
    // zwraca null - jeśli trwa jeszcze przetwarzanie lub nie przekazano danych do analizy
    // wyrzuca wyjątek - jeśli podczas przetwarzania doszło do jakichś błędów
    // clear = true - jeśli wyniki po pobraniu mają zniknąć z serwisu
    public DataSet retrieve(boolean clear) throws ClusteringException;
    public ArrayList<Double> getDistances();
}
