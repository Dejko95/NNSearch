package geometry;

public class KDTree {
    KDCell rootCell;

    public KDTree(HyperCube hyperCube) {
        rootCell = new KDCell();
        rootCell.points = hyperCube.points;
        rootCell.box = new Box(hyperCube.dimensions);
        for (int i=0; i<hyperCube.dimensions; i++) {
            rootCell.box.begin[i] = 0;
            rootCell.box.end[i] = hyperCube.bounds[i];
        }
        rootCell.divide(0);
    }

}
