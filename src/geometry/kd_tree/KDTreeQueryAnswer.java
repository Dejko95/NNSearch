package geometry.kd_tree;

import geometry.DataPoint;
import geometry.kd_tree.KDCell;
import geometry.kd_tree.KDTree;

import java.util.ArrayList;

public class KDTreeQueryAnswer {
    public double closestDistance;
    public int visited;
    public DataPoint q;

    public void findClosestKD(DataPoint q, KDTree kdTree, ArrayList<DataPoint> points) {
        this.q = q;

        //locate cell
        KDCell containing = kdTree.rootCell;
        visited = 1;
        while (true) {
            if (containing.leftChild != null && containing.leftChild.box.contains(q)) {
                containing = containing.leftChild;
            } else if (containing.rightChild != null && containing.rightChild.box.contains(q)) {
                containing = containing.rightChild;
            } else {
                break;
            }
            visited++;
        }

        closestDistance = q.distanceFrom(containing.cellPoint);
        search(kdTree.rootCell);

        if (Math.abs(closestDistance - trueClosestDistance(q, points)) > 0.00001) {
            try {
                System.out.println(closestDistance + " " + trueClosestDistance(q, points));
                throw new Exception("WA");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void search(KDCell cell) {
        visited++;
        if (q.distanceFrom(cell.cellPoint) < closestDistance) {
            closestDistance = q.distanceFrom(cell.cellPoint);
        }

        if (cell.leftChild != null) {
            if (cell.leftChild.distance(q) < closestDistance) {
                search(cell.leftChild);
            }
        }
        if (cell.rightChild != null) {
            if (cell.rightChild.distance(q) < closestDistance) {
                search(cell.rightChild);
            }
        }
    }

    public double trueClosestDistance(DataPoint q, ArrayList<DataPoint> points) {
        //DataPoint closestPoint = null;
        double trueClosestDistance = Double.MAX_VALUE;
        double distance;
        for (DataPoint point: points) {
            distance = q.distanceFrom(point);
            if (distance < trueClosestDistance) {
                trueClosestDistance = distance;
            }
        }
        return trueClosestDistance;
    }

}
