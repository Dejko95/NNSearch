package geometry;

import java.util.Comparator;

public class CellDistanceComparator implements Comparator<Cell> {
    DataPoint q;

    public CellDistanceComparator(DataPoint q) {
        this.q = q;
    }

    @Override
    public int compare(Cell c1, Cell c2) {
        return distanceFromQ(c1).compareTo(distanceFromQ(c2));
    }

    Double distanceFromQ(Cell c) {
        if (c.innerBox != null && c.innerBox.contains(q)) {
            return distanceFromInnerBox(c);
        } else if (c.outerBox.contains(q)) {
            return 0.0;
        } else {
            return distanceFromOuterBox(c);
        }
    }

    Double distanceFromInnerBox(Cell c) {
        double min = Double.MAX_VALUE;
        for (int dimension = 0; dimension < q.coordinates.length; dimension++) {
            min = Math.min(min, c.innerBox.end[dimension] - q.coordinates[dimension]);
            min = Math.min(min, q.coordinates[dimension] - c.innerBox.begin[dimension]);
        }
        return min;
    }

    Double distanceFromOuterBox(Cell c) {
        double sum = 0;
        for (int dimension = 0; dimension < q.coordinates.length; dimension++) {
            double dist_dimension = max(c.outerBox.begin[dimension] - q.coordinates[dimension], 0, q.coordinates[dimension] - c.outerBox.end[dimension]);
            sum += dist_dimension * dist_dimension;
        }
        return Math.sqrt(sum);
    }

    double max(double a, double b, double c) {
        return Math.max(a, Math.max(b, c));
    }
}
