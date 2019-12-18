package csc482;

import java.lang.Math;

public class GenerateGraphs {
    public static double[][] GenerateRandomCostMatrix(int size) {
        double[][] Matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                if (i == j)
                    Matrix[i][j] = 0;
                else {
                    Matrix[i][j] = (int) (Math.random() * 100) + 1;
                    Matrix[j][i] = Matrix[i][j];
                }
            }
        }
        return Matrix;
    }
    public static double [][] GenerateRandomEuclideanCostMatrix(int size) {
        int[][] Locations = new int[size][2];
        double[][] Matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            Locations[i][0] = (int) Math.random() * 20;
            Locations[i][1] = (int) Math.random() * 20;
        }
        for (int j = 0; j < size; j++) {
            for (int k = j; k < size; k++) {
                double distance = GetDistance(Locations[j][0], Locations[j][1], Locations[k][0], Locations[k][1]);
                Matrix[j][k] = distance;
                Matrix[k][j] = distance;
            }
        }
        return Matrix;
    }

    public static double [][] GenerateRandomCircularGraphCostMatrix(int size) {
        int radius = 2;
        double[][] Locations = new double[size][2];
        int[] order = new int[size];
        double[][] Matrix = new double[size][size];

        // Create Locations in a circle
        for (int i = 0; i < size; i++) {
            order[i] = i;
            double num = (double) i;
            Locations[i][0] = Math.sin((double)num/size*2*Math.PI)*10;
            Locations[i][1] = Math.cos((double)num/size*2*Math.PI)*10;
        }

        // Randomize order of points
        for (int i = 0; i < size; i++) {
            int SwapValue = (int) (Math.random()*size);
            double temp1 = Locations[i][0];
            double temp2 = Locations[i][1];

            Locations[i][0] = Locations[SwapValue][0];
            Locations[i][1] = Locations[SwapValue][1];

            Locations[SwapValue][0] = temp1;
            Locations[SwapValue][1] = temp2;
        }

        for (int j = 0; j < size; j++) {
            for (int k = j; k < size; k++) {
                double distance = GetDistance(Locations[j][0], Locations[j][1], Locations[k][0], Locations[k][1]);
                Matrix[j][k] = distance;
                Matrix[k][j] = distance;
            }
        }
        return Matrix;
    }

    public static double GetDistance(double x1, double y1, double x2, double y2) {
        double distance;
        distance = Math.sqrt((x2 - x1)*(x2-x1) + (y2-y1)*(y2-y1));
        return distance;
    }
}
