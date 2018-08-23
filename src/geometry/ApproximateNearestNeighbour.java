package geometry;

import gui.InputPanel;
import gui.Visualiser;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ApproximateNearestNeighbour {

    File configFile;
    //String inputPath = "inputExamples/input3.in";
    public boolean graphicsON = true;
    HyperCube hyperCube;
    BBDTree bbdTree;
    public double epsilon = 0.2;
    boolean randomGeneratePoints;
    public int dimension;
    int bounds[];
    int pointCount;
    public boolean testing = false;

    public ArrayList<DataPoint> testPoints;
    public String testOutPath;

    public ApproximateNearestNeighbour() {
        try {
            readConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (randomGeneratePoints) {
            generatePoints();
        } else {
            try {
                loadPoints();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ? scalePoints();

        bbdTree = new BBDTree(hyperCube);
        if (graphicsON) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new Visualiser(bbdTree.rootCell, hyperCube, ApproximateNearestNeighbour.this);
                }
            });
        } else {
            if (testing) {
                try {
                    runTests();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        //new Visualiser(bbdTree.rootCell, hyperCube, ApproximateNearestNeighbour.this);
                        JFrame basicFrame = new JFrame();
                        basicFrame.setVisible(true);
                        basicFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                        basicFrame.add(new InputPanel(ApproximateNearestNeighbour.this, null));
                        basicFrame.pack();
                        basicFrame.setLocationRelativeTo(null);
                    }
                });
            }
        }
    }

    private void runTests() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(testOutPath);
        int cnt = 0;
        int maxVisited = 0;
        //int minVisited = 1000000;
        int sumVisited = 0;
        double maxErr = 0;
        double sumErr = 0;
        int zeros = 0;

        for (DataPoint q: testPoints) {
            System.out.println(cnt);
            QueryAnswer queryAnswer = answerQuery(q);
            DataPoint closestPoint = queryAnswer.closest_points.get(queryAnswer.closest_points.size() - 1);
            if (q.distanceFrom(queryAnswer.trueClosestPoint) < 0.00001) continue;
            cnt++;

            double err = q.distanceFrom(closestPoint) / q.distanceFrom(queryAnswer.trueClosestPoint) - 1;
            int visited = queryAnswer.closest_points.size();
            maxVisited = Math.max(maxVisited, visited);
            sumVisited += visited;
            maxErr = Math.max(maxErr, err);
            sumErr += err;
            //pw.println(visited + " " + err);
            if (err < 0.00001) {
                zeros++;
            }
        }
        pw.println();
        pw.println("Max visited: " + maxVisited);
        pw.println("Avg visited: " + sumVisited / (double)cnt);
        pw.println("Max error: " + maxErr);
        pw.println("Avg err: " + sumErr / cnt);
        pw.println("Precision: " + (int)(zeros / (double)cnt * 100) + " %");
        pw.close();
    }

    private void generatePoints() {
        hyperCube = new HyperCube(dimension);
        hyperCube.bounds = bounds;
        for (int i=0; i<pointCount; i++) {
            DataPoint p = new DataPoint(hyperCube.dimensions);
            for (int j=0; j<hyperCube.dimensions; j++) {
                p.coordinates[j] = (int)(Math.random() * (bounds[j] + 1));
            }
            hyperCube.points.add(p);
        }
    }

    void readConfig() throws FileNotFoundException {
        //choosing config file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("D:\\programming\\IdeaProjects\\BalancedBoxDecompositionTree\\configs"));
        do {
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                configFile = fileChooser.getSelectedFile();
                break;
            }
        } while (true);

        //read config params from config file
        HashMap<String, String> configParams = new HashMap<>();
        Scanner sc = new Scanner(configFile);
        while (sc.hasNextLine()) {
            String key = sc.next();
            String value = sc.next();
            configParams.put(key, value);
        }

        System.out.println(configParams.get("randomGeneratePoints"));
        randomGeneratePoints = Boolean.valueOf(configParams.get("randomGeneratePoints"));
        //inputPath = configParams.get("inputPath");
        graphicsON = Boolean.valueOf(configParams.get("graphicsON"));
        epsilon = Double.valueOf(configParams.get("epsilon"));
        if (randomGeneratePoints) {
            dimension = Integer.valueOf(configParams.get("dimension"));
            bounds = Arrays.stream(configParams.get("bounds").split(",")).mapToInt(Integer::parseInt).toArray();
            pointCount = Integer.valueOf(configParams.get("pointCount"));
        }
        if (configParams.containsKey("testing")) {
            testing = Boolean.valueOf(configParams.get("testing"));
        }
        //System.out.println("bounds:");
        //Arrays.stream(bounds).forEach(System.out::println);
    }

    void loadPoints() throws Exception {
        File inputFile;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("D:\\programming\\IdeaProjects\\BalancedBoxDecompositionTree\\tests"));
        do {
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                inputFile = fileChooser.getSelectedFile();
                break;
            }
        } while (true);
        Scanner sc = new Scanner(inputFile);
        testOutPath = inputFile.getAbsolutePath().replaceFirst("\\.in", "_eps" + (int)(epsilon * 100) + ".out");
        System.out.println(testOutPath);

        // read number of dimensions
        dimension = sc.nextInt();
        hyperCube = new HyperCube(dimension);
        if (hyperCube.dimensions > 2 && graphicsON) {
            throw new Exception("Only 2D points can be visualised.");
        }

        bounds = new int[dimension];
        // read bounds per each dimension
        for (int i=0; i<hyperCube.dimensions; i++) {
            bounds[i] = sc.nextInt();
        }
        hyperCube.bounds = bounds;

        //read points
        pointCount = sc.nextInt();
        for (int i=0; i<pointCount; i++) {
            DataPoint p = new DataPoint(hyperCube.dimensions);
            for (int j=0; j<hyperCube.dimensions; j++) {
                p.coordinates[j] = sc.nextInt();
            }
            hyperCube.points.add(p);
        }

        //read test points
        if (testing) {
            testPoints = new ArrayList<>();
            int testCount = sc.nextInt();
            for (int i=0; i<testCount; i++) {
                DataPoint p = new DataPoint(dimension);
                for (int j=0; j<dimension; j++) {
                    p.coordinates[j] = sc.nextInt();
                }
                testPoints.add(p);
            }
        }
    }

    public QueryAnswer answerQuery2D(int x, int y) {
        System.out.println("Query point: " + x + ", " + y);
        DataPoint queryPoint = new DataPoint(2);
        queryPoint.coordinates[0] = queryPoint.x = x;
        queryPoint.coordinates[1] = queryPoint.y = y;
        return answerQuery(queryPoint);
    }

    public QueryAnswer answerQuery(DataPoint queryPoint) {
        QueryAnswer queryAnswer = new QueryAnswer();
        queryAnswer.findClosestK(queryPoint, bbdTree, 1, epsilon);
        DataPoint closestPoint = queryAnswer.closest_points.get(queryAnswer.closest_points.size() - 1);
        queryAnswer.trueClosestPoint(queryPoint, hyperCube.points);
        //System.out.println("closest: " + closestPoint.coordinates[0] + ", " + closestPoint.coordinates[1]);
        return queryAnswer;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new geometry.ApproximateNearestNeighbour();
    }

}
