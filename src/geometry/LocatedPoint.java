package geometry;

public class LocatedPoint extends Point {

	LocatedPoint(double x, double y, double z) {
		super(x, y, z);
	}

	protected Polygon polygon;
	
	public Polygon getPolygon() {
		return this.polygon;
	}

}
