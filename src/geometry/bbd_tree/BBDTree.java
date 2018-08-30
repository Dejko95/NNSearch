package geometry;

import javax.xml.crypto.Data;

public class BBDTree {
    public Cell rootCell;
    public PartitionAlgorithm partitionAlgorithm;

    public BBDTree(HyperCube hyperCube) {
        rootCell = new Cell();
        rootCell.points = hyperCube.points;
        rootCell.outerBox = new Box(hyperCube.dimensions);
        for (int i=0; i<hyperCube.dimensions; i++) {
            rootCell.outerBox.begin[i] = 0;
            rootCell.outerBox.end[i] = hyperCube.bounds[i];
        }

        partitionAlgorithm = new MidpointAlgorithm(hyperCube);
        partitionAlgorithm.split(rootCell, false);
        mergeTrivialPartitions(rootCell);
        // assignPointToEmptyLeaves
        getPoints(rootCell);
    }

    void mergeTrivialPartitions(Cell cell) {
        //if null or leaf return
        if (cell == null || cell.leaf) return;

        mergeTrivialPartitions(cell.lowChild);
        mergeTrivialPartitions(cell.highChild);

        // cell is trivial if one of child is empty
        if (cell.lowChild.points.size() == 0 || cell.highChild.points.size() == 0) {
            cell.trivial = true;
            Cell empty;
            Cell nonEmpty;
            if (cell.lowChild.points.size() == 0) {
                empty = cell.lowChild;
                nonEmpty = cell.highChild;
            } else {
                empty = cell.highChild;
                nonEmpty = cell.lowChild;
            }
            if (nonEmpty.trivial) { // merge empty cell of nonEmpty (its highChild) with empty of cell
                cell.lowChild = nonEmpty.lowChild;
                Cell mergedEmpty = new Cell();
                mergedEmpty.parent = cell;
                mergedEmpty.outerBox = cell.outerBox;
                //mergedEmpty.innerBox = nonEmpty.outerBox; THIS WAS WRONG
                mergedEmpty.innerBox = nonEmpty.lowChild.outerBox;
                mergedEmpty.leaf = true;
                cell.highChild = mergedEmpty;
            } else {    // set that empty child is highChild
                cell.lowChild = nonEmpty;
                cell.highChild = empty;
            }
        }
    }

    DataPoint[] getPoints(Cell cell) {
        //if it is leaf, it must contain a point
        if (cell.leaf) {
            return new DataPoint[]{cell.points.get(0), null};
        }
        //take one point of each child
        if (!cell.trivial) {
            DataPoint ptsLow[] = getPoints(cell.lowChild);
            DataPoint ptsHigh[] = getPoints(cell.highChild);
            return new DataPoint[]{ptsLow[0], ptsHigh[0]};
        }
        //pass one point to empty child, and return the other to the parent
        Cell empty;
        Cell nonEmpty;
        if (cell.lowChild.points.size() == 0) {
            empty = cell.lowChild;
            nonEmpty = cell.highChild;
        } else {
            empty = cell.highChild;
            nonEmpty = cell.lowChild;
        }
        DataPoint pts[] = getPoints(nonEmpty);
        empty.borrowed = pts[1];
        pts[1] = null;
        return pts;
    }

}
