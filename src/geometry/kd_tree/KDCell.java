package geometry.kd_tree;

import geometry.DataPoint;
import geometry.bbd_tree.Box;

import java.util.ArrayList;

public class KDCell {
    public Box box;
    public ArrayList<DataPoint> points;
    public DataPoint cellPoint;
    public KDCell leftChild = null;
    public KDCell rightChild = null;


    public void divide(int dimension) {
        //System.out.println(box);
        int dimensionCount = points.get(0).coordinates.length;
        if (dimension >= dimensionCount) {
            dimension -= dimensionCount;
        }
        final int sortDimension = dimension;
        points.sort((o1, o2) -> {
            return o1.coordinates[sortDimension] - o2.coordinates[sortDimension];
        });

        int midIndex = points.size() / 2;
        cellPoint = points.get(midIndex);
        if (midIndex > 0) {
            KDCell left = new KDCell();
            left.points = new ArrayList<>();
            for (int i=0; i<midIndex; i++) {
                left.points.add(points.get(i));
            }

            left.box = new Box(dimensionCount);
            for (int i=0; i<dimensionCount; i++) {
                left.box.begin[i] = box.begin[i];
                left.box.end[i] = box.end[i];
            }
            left.box.end[dimension] = cellPoint.coordinates[dimension];

            leftChild = left;
            leftChild.divide(dimension + 1);
        }

        if (midIndex < points.size() - 1) {
            KDCell right = new KDCell();
            right.points = new ArrayList<>();
            for (int i=midIndex + 1; i<points.size(); i++) {
                right.points.add(points.get(i));
            }

            right.box = new Box(dimensionCount);
            for (int i=0; i<dimensionCount; i++) {
                right.box.begin[i] = box.begin[i];
                right.box.end[i] = box.end[i];
            }
            right.box.begin[dimension] = cellPoint.coordinates[dimension];

            rightChild = right;
            rightChild.divide(dimension + 1);
        }
        points = null;
    }

    public double distance(DataPoint q) {
        if (box.contains(q)) {
            return 0;
        }
        double sum = 0;
        for (int dimension = 0; dimension < q.coordinates.length; dimension++) {
            double dist_dimension = max(box.begin[dimension] - q.coordinates[dimension], 0, q.coordinates[dimension] - box.end[dimension]);
            sum += dist_dimension * dist_dimension;
        }
        return Math.sqrt(sum);
    }


    double max(double a, double b, double c) {
        return Math.max(a, Math.max(b, c));
    }
}
