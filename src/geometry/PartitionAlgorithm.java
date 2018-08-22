package geometry;

public abstract class PartitionAlgorithm {
    abstract void split(Cell cell, boolean flag);
    abstract void shrink(Cell cell);
}
