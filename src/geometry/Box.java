package geometry;

public class Box {
    public double begin[];
    public double end[];

    public Box(int dimensions) {
        this.begin = new double[dimensions];
        this.end = new double[dimensions];
    }

    public boolean contains(DataPoint point) {
        for (int i=0; i<begin.length; i++) {
            if (point.coordinates[i] < begin[i] || point.coordinates[i] > end[i]) {
                return false;
            }
        }
        return true;
    }
}
