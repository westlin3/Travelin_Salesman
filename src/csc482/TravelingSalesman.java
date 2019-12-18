package csc482;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;
import javafx.util.Pair;

public class TravelingSalesman {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */

    static long MAXVALUE =  200000000;

    static long MINVALUE = -200000000;

    static int numberOfTrials = 30;
    static int MAXINPUTSIZE  = 1200;
    static int MININPUTSIZE  =  100;

    static String ResultsFolderPath = "/home/curtis/Bean/LAB8/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    static void runFullExperiment(String resultsFileName) {

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch (Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file " + ResultsFolderPath + resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();

        for (int inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize += 100) {
            long Fib_value = 1;
            // progress message...
            System.out.println("Running test for input size " + inputSize + " ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;

            /* force garbage collection before each batch of trials run so it is not included in the time */
            //System.gc();

            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves

            double[][] Matrix = GenerateGraphs.GenerateRandomEuclideanCostMatrix(inputSize);
            BatchStopwatch.start(); // comment this line if timing trials individually

            // list for dynamic recursive function
            long[] fib = new long[93];
            // run the trials
            for (long trial = 0; trial < numberOfTrials; trial++) {
                GreedyAlgorithm(Matrix, inputSize);
            }

            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f\n", inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");

        }
    }

    public static void main(String[] args) {
        runFullExperiment("Greedy-Exp1.txt");
        runFullExperiment("Greedy-Exp2.txt");
        runFullExperiment("Greedy-Exp3.txt");
//        int matrixsize = 7;
//        double[][] Matrix = GenerateGraphs.GenerateRandomCircularGraphCostMatrix(matrixsize);
////        double[][] Matrix = GenerateGraphs.GenerateRandomCostMatrix(matrixsize);
//        int[] tour = BruteForce(Matrix, matrixsize);
//        int[] tour2 = GreedyAlgorithm(Matrix, matrixsize);
//        System.out.println("Brute Force");
//        PrintArray(tour);
//        System.out.println("Greedy Algorithm");
//        PrintArray(tour2);
//        PrintMatrix(Matrix, matrixsize);
    }

    // Using Heaps algorithm to produce permutations
    public static int[] BruteForce(double matrix[][], int size) {
        int[] tour = new int[size+1];
        int[] indexes = new int[size];
        int[] BestTour = new int[size+1];

        for (int i = 0; i < size; i++) {
            tour[i] = i;
            indexes[i] = 0;
        }

        BestTour = CopyArray(tour);
        double TourLength = CalculateTourLength(matrix, tour);
        int i = 1;
        while (i < size) {
            if (indexes[i] < i) {
                if (i % 2 == 0) {
                    int temp = tour[0];
                    tour[0] = tour[i];
                    tour[i] = temp;
                } else {
                   int temp = tour[i];
                   tour[i] = tour[indexes[i]];
                    tour[indexes[i]] = temp;
                }
                indexes[i]++;
                i = 0;
                if (CalculateTourLength(matrix, tour) <= TourLength && tour[0] <= BestTour[0]) {
                    BestTour = CopyArray(tour);
                    TourLength = CalculateTourLength(matrix, tour);
                }
            } else {
                indexes[i] = 0;
                i++;
            }
        }
        BestTour[size] = BestTour[0];
        return BestTour;
    }

    public static int[] GreedyAlgorithm(double matrix[][], int size){
        int[] tour = new int[size+1];
        List<Integer> check_list = new ArrayList<>();
        tour[0] = 0;
        tour[size] = 0;
        double distance;
        int nextStep;
        check_list.add(0);

        for (int i = 0; i<size; i++){
            if (i != 0) {
                distance = 100;
                nextStep = 0;
            } else {
                distance = 100;
                nextStep = i + 1;
            }
            for (int j = 0; j<size; j++) {
                if (distance > matrix[tour[i]][j] && tour[i] != j && !check_list.contains(j)) {
                    distance = matrix[tour[i]][j];
                    nextStep = j;
                }

            }
            check_list.add(nextStep);
            tour[i+1] = nextStep;
        }
        return tour;
    }
    
//    public static Pair<List<Integer>,Double> DynamicAlgo(int startNode, int endNode, List<Integer> tourNodes,double bestCost, List<Integer> bestPath,
//                                                            double matrix[][], List<List<Integer>> subSolutionTable) {
//        List <Integer> tempNodes = new ArrayList<Integer>();
//        if (tourNodes.size() == 1) {
//            double distance = matrix[startNode][tourNodes.get(0)]+matrix[tourNodes.get(0)][endNode];
//            return new Pair<>(tourNodes, distance);
//        }
//        else {
//            for (int i = 0; i<tourNodes.size(); i++){
//                // Create a list array minus a specific node
//                tempNodes.clear();
//                tempNodes.addAll(tourNodes);
//                int node = tempNodes.get(i);
//                tempNodes.remove(i);
//
//                Pair <List<Integer>, Double> TempPair;
//                TempPair = BruteRecursive(node, endNode, tempNodes,bestCost, bestPath, matrix, subSolutionTable);
//                List <Integer> nodes = new ArrayList<Integer>();
//                nodes.add(node);
//                nodes.addAll(TempPair.getKey());
//                double cost = TempPair.getValue() + matrix[startNode][node];
////                System.out.println(cost);
//                if (cost < bestCost) {
//                    bestCost = cost;
//                    bestPath = nodes;
//                }
//            };
//            return new Pair<List<Integer>,Double>(bestPath, bestCost);
//        }
//    }

//    public static

    public static void PrintMatrix(double matrix[][], int size){
        for(int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                System.out.printf("%f ",matrix[i][j]);
            System.out.println("");
        }
    }

    public static void PrintArray(int array[]) {
        for( int i = 0; i < array.length; i++) {
            System.out.printf("%d ", array[i]);
        }
        System.out.print("\n");
    }

    public static int[] CopyArray(int array[]) {
        int copy[] = new int[array.length];

        for (int i = 0; i < array.length; i++)
            copy[i] = array[i];

        return copy;
    }

    public static double CalculateTourLength(double matrix[][], int tour[]) {

        double distance = matrix[tour[tour.length-1]][tour[0]];
        for (int i = 0; i < tour.length-1; i++) {
            distance += matrix[tour[i]][tour[i + 1]];
        }

        return distance;
    }
}
