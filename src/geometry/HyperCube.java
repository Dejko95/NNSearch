package geometry;

import java.util.ArrayList;

public class HyperCube {
    public int dimensions; // for multidimensional case
    public int bounds[];
    public ArrayList<DataPoint> points = new ArrayList<>();

    public HyperCube(int dimensions) {
        this.dimensions = dimensions;
        this.bounds = new int[dimensions];
    }

}
