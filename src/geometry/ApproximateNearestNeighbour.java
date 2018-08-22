package geometry;

import gui.Visualiser;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.File;
import java.util.Scanner;

public class ApproximateNearestNeighbour {

    String inputPath = "inputExamples/input3.in";
    boolean graphicsON = true;
    HyperCube hyperCube;
    BBDTree bbdTree;
    public double epsilon = 0.2;

    public ApproximateNearestNeighbour() {
        try {
            loadPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ? scalePoints();

        bbdTree = new BBDTree(hyperCube);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Visualiser(bbdTree.rootCell, hyperCube, ApproximateNearestNeighbour.this);
            }});
    }

    void loadPoints() throws Exception {
        System.out.println("printing:");
        Scanner sc = new Scanner(new File(inputPath));

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
        new geometry.ApproximateNearestNeighbour();
    }

}
