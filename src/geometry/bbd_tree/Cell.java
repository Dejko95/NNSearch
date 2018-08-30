package geometry.bbd_tree;

import geometry.DataPoint;

import java.util.ArrayList;

public class Cell {
    public Box innerBox = null;
    public Box outerBox = null;
    public ArrayList<DataPoint> points = new ArrayList<>();
    public Cell lowChild = null;
    public Cell highChild = null;
    public Cell parent = null;
    public int splittingDimension = -1;
    public Operation operation;
    public boolean trivial = false;
    public boolean leaf = false;
    public DataPoint borrowed = null;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(operation != null ? operation.toString() : "none");
        sb.append(" " + points.size() + "(");
        for (int i=0; i<outerBox.begin.length; i++) {
            sb.append(outerBox.begin[i] + "-" + outerBox.end[i] + ", ");
        }
        sb.append(")");
        return sb.toString();
    }

    boolean contains(DataPoint point) {
        return outerBox.contains(point) && (innerBox == null || !innerBox.contains(point));
    }

}
