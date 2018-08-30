package geometry.bbd_tree;

import geometry.bbd_tree.Cell;

public abstract class PartitionAlgorithm {
    public abstract void split(Cell cell, boolean flag);
    public abstract void shrink(Cell cell);
}
