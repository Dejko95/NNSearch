package geometry;

public abstract class PartitionAlgorithm {
    public abstract void split(Cell cell, boolean flag);
    public abstract void shrink(Cell cell);
}
