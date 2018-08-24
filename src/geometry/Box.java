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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (int i=0; i<begin.length; i++) {
            sb.append(begin[i] + "-" + end[i] + ", ");
        }
        return sb.toString();
    }
}
