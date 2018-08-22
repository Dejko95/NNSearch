package geometry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class QueryAnswer {
    CellDistanceComparator cellDistanceComparator;
    PriorityQueue<Cell> pq;

    // used for chronological drawing
    public ArrayList<Cell> ordered_cells = new ArrayList<>();
    public ArrayList<Double> shortest_distances = new ArrayList<>();
    public ArrayList<DataPoint> closest_points = new ArrayList<>();
    public Cell lastCell = null;

    void findClosestK(DataPoint q, BBDTree tree, int k, double epsilon) {
        cellDistanceComparator = new CellDistanceComparator(q);
        pq = new PriorityQueue<>(cellDistanceComparator);

        pq.add(tree.rootCell);

        double closest_distance =  Double.MAX_VALUE;
        while (true) {
            Cell next_cell = nextClosestLeafCell();
            if (next_cell != null) {
                lastCell = next_cell;
            }
            if (next_cell == null || cellDistanceComparator.distanceFromQ(next_cell) > closest_distance / (1 + epsilon)) {
                break;
            }
            DataPoint associated_point = next_cell.borrowed != null ? next_cell.borrowed : next_cell.points.get(0);
            double dist_from_cell = q.distanceFrom(associated_point);
            if (dist_from_cell < closest_distance) {
                closest_distance = dist_from_cell;
                closest_points.add(associated_point);
            } else {
                //repeat last point
                closest_points.add(closest_points.get(closest_points.size() - 1));
            }
            //closest_distance = Math.min(closest_distance,
            //        q.distanceFrom(next_cell.borrowed != null ? next_cell.borrowed : next_cell.points.get(0)));
            ordered_cells.add(next_cell);
            shortest_distances.add(closest_distance);
        }
    }

    Cell nextClosestLeafCell() {
        Cell cell = pq.poll();
        while (cell != null && !cell.leaf) {
            if (cellDistanceComparator.compare(cell.lowChild, cell.highChild) < 0) {
                pq.add(cell.highChild);
                cell = cell.lowChild;
            } else {
                pq.add(cell.lowChild);
                cell = cell.highChild;
            }
        }
        return cell;
    }
}
