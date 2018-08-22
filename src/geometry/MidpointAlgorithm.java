package geometry;

import java.util.ArrayList;

public class MidpointAlgorithm extends PartitionAlgorithm {

    SplittingRule splittingRule = SplittingRule.orthogonal;
    HyperCube hyperCube;

    public MidpointAlgorithm(HyperCube hyperCube) {
        this.hyperCube = hyperCube;
    }

    @Override
    void split(Cell cell, boolean calledFromShrink) {
        if (cell.points.size() <= 1) {
            cell.leaf = true;
            return;
        }

        cell.operation = Operation.split;
        //find dimension for splitting
        int dimension = cell.splittingDimension = findSplitingDimension(cell);

        double splittingCoordinate = (cell.outerBox.begin[dimension] + cell.outerBox.end[dimension]) / 2;

        Cell lowCell = new Cell();
        Cell highCell = new Cell();
        // split points that lies to left and right of splitting plane
        for (DataPoint p: cell.points) {
            if (p.coordinates[dimension] < splittingCoordinate) {
                lowCell.points.add(p);
            } else if (p.coordinates[dimension] > splittingCoordinate) {
                highCell.points.add(p);
            }
        }
        // divide points on dividing plane as equal as possible
        for (DataPoint p: cell.points) {
            if (p.coordinates[dimension] == splittingCoordinate) {
                if (lowCell.points.size() < highCell.points.size()) {
                    lowCell.points.add(p);
                } else {
                    highCell.points.add(p);
                }
            }
        }

        lowCell.outerBox = new Box(hyperCube.dimensions);
        highCell.outerBox = new Box(hyperCube.dimensions);
        for (int i=0; i<hyperCube.dimensions; i++) {
            lowCell.outerBox.begin[i] = highCell.outerBox.begin[i] = cell.outerBox.begin[i];
            lowCell.outerBox.end[i] = highCell.outerBox.end[i] = cell.outerBox.end[i];
        }
        lowCell.outerBox.end[dimension] = splittingCoordinate;
        highCell.outerBox.begin[dimension] = splittingCoordinate;

        if (cell.innerBox != null) {
            if (cell.innerBox.begin[dimension] < splittingCoordinate) {
                //lowCell.innerBox = new geometry.Box(hyperCube.dimensions);
                lowCell.innerBox = cell.innerBox;
            } else {
                //highCell.innerBox = new geometry.Box(hyperCube.dimensions);
                highCell.innerBox = cell.innerBox;
            }
        }

        cell.lowChild = lowCell;
        cell.highChild = highCell;
        lowCell.parent = cell;
        highCell.parent = cell;

        //this could be moved to another function that decide which operation to perform
        if (!calledFromShrink) {
            shrink(lowCell);
        }
        if (!calledFromShrink) {
            shrink(highCell);
        }
    }

    @Override
    void shrink(Cell cell) {
        if (cell.points.size() <= 1) {
            cell.leaf = true;
            return;
        }

        Cell current = cell;
        while (true) {
            split(current, true);
            Cell majority = current.lowChild.points.size() >= current.highChild.points.size() ? current.lowChild : current.highChild;
            Cell remaining = current.lowChild == majority ? current.highChild : current.lowChild;

            // check if majority is separated from inner box
            if (remaining.innerBox != null) {
                //special case is when the situaiton occurs after first split, (but also after second)
                if (cell == current) {
                    split(cell.lowChild, false);
                    split(cell.highChild, false);
                    break;
                //} else if (cell.lowChild == current || cell.highChild == current) { //not sure for this
                //    shrink(majority);
                //    split(remaining, false);

                } else {
                    System.out.println("DESILO SE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.println("M: " + majority);
                    System.out.println("R: " + remaining);
                    //current is low child, cell - current is high
                    //second split
                    if (cell.lowChild != current && cell.highChild != current) {
                        processShrinkingBox(cell, current);
                        System.out.println("mora shrink");
                    }
                    //split current to majority and remaining

                    //call shrink on majority
                    shrink(majority);

                    //call split on remaining node
                    split(remaining, false);

                    //also call split on (cell - current)
                    if (cell.lowChild == current) {
                        split(cell.highChild, false);
                    } else {
                        split(cell.lowChild, false);
                    }
                    break;
                }

            } else {
                //to much points, keep spliting, otherwise shrinking box is found
                if (majority.points.size() > cell.points.size() * 2 / 3) {
                    //current.lowChild = current.highChild = null;
                    current = majority;
                } else {
                    //if shrink is actually split
                    if (current != cell) {
                        processShrinkingBox(cell, majority);
                    } else {
                        //everything is already ok
                    }

                    // call split on new nodes
                    split(cell.lowChild, false);
                    split(cell.highChild, false);
                    break;
                }
            }
        }
    }

    //majority is shrinking box
    void processShrinkingBox(Cell cell, Cell majority) {
        cell.operation = Operation.shrink;
        Cell difference = new Cell();
        // rest of the points are outer (other option is to do partition again, with respect to boundaries)
        for (DataPoint point: cell.points) {
            if (!majority.points.contains(point)) {
                difference.points.add(point);
            }
        }
        difference.outerBox = cell.outerBox;
        difference.innerBox = majority.outerBox;

        cell.lowChild = majority;
        cell.highChild = difference;
        majority.parent = difference.parent = cell;
        if (majority == cell || difference == cell) {
            System.out.println("LOL");
        }
    }

    int findSplitingDimension(Cell cell) {
        if (splittingRule == SplittingRule.orthogonal) {
            Cell anc = cell.parent;
            if (anc == cell) {
                System.out.println("wtf");
            }
            while (anc != null && /*anc.splittingDimension == -1*/ anc.operation != Operation.split) {
                anc = anc.parent;
                if (anc == anc.parent) {
                    System.out.println("wtf");
                }
            }

            if (anc != null) {
                //if not rootCell return next dimension
                return (anc.splittingDimension + 1) % hyperCube.dimensions;
            } else {
                //if rootCell return dimension 0
                return 0;
            }
        } else {
            //to be implemented
            return -1;
        }
    }
}
