package geometry;

import gui.Visualiser;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ApproximateNearestNeighbour {

    File configFile;
    String inputPath = "inputExamples/input3.in";
    public boolean graphicsON = true;
    HyperCube hyperCube;
    BBDTree bbdTree;
    public double epsilon = 0.2;
    boolean randomGeneratePoints;
    public int dimension;
    int bounds[];
    int pointCount;

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
            
        }
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
        //System.out.println("bounds:");
        //Arrays.stream(bounds).forEach(System.out::println);
    }

    void loadPoints() throws Exception {
        File inputFile;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("D:\\programming\\IdeaProjects\\BalancedBoxDecompositionTree\\inputExamples"));
        do {
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                inputFile = fileChooser.getSelectedFile();
                break;
            }
        } while (true);
        Scanner sc = new Scanner(inputFile);

        // read number of dimensions
        hyperCube = new HyperCube(sc.nextInt());
        if (hyperCube.dimensions > 2 && graphicsON) {
            throw new Exception("Only 2D points can be visualised.");
        }
        // read bounds per each dimension
        for (int i=0; i<hyperCube.dimensions; i++) {
            hyperCube.bounds[i] = sc.nextInt();
        }
        //read points
        int pointsCount = sc.nextInt();
        for (int i=0; i<pointsCount; i++) {
            DataPoint p = new DataPoint(hyperCube.dimensions);
            for (int j=0; j<hyperCube.dimensions; j++) {
                p.coordinates[j] = sc.nextInt();
            }
            hyperCube.points.add(p);
        }
    }

    public QueryAnswer answerQuery2D(int x, int y) {
        System.out.println("Query point: " + x + ", " + y);
        QueryAnswer queryAnswer = new QueryAnswer();
        DataPoint queryPoint = new DataPoint(2);
        queryPoint.coordinates[0] = queryPoint.x = x;
        queryPoint.coordinates[1] = queryPoint.y = y;
        queryAnswer.findClosestK(queryPoint, bbdTree, 1, epsilon);
        DataPoint closestPoint = queryAnswer.closest_points.get(queryAnswer.closest_points.size() - 1);
        System.out.println("closest: " + closestPoint.coordinates[0] + ", " + closestPoint.coordinates[1]);
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
