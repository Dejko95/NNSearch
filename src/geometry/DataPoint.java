package geometry;

public class DataPoint {
    public int x, y;
    public int coordinates[];  // for multidimensional case

    public DataPoint(int dimensions) {
        this.coordinates = new int[dimensions];
    }

    public void prepare2D() {
        x = coordinates[0];
        y = coordinates[1];
    }

    public double distanceFrom(DataPoint q) {
        double sum = 0;
        for (int dimension = 0; dimension < coordinates.length; dimension++) {
            sum += (coordinates[dimension] - q.coordinates[dimension]) * (coordinates[dimension] - q.coordinates[dimension]);
        }
        return Math.sqrt(sum);
    }
}
