package geometry;

public final class LocatedPoint extends Point {

	LocatedPoint(double x, double y, double z) {
		super(x, y, z, -1);
	}

	protected Polygon polygon;

	
	public Polygon getPolygon() {
		return this.polygon;
	}

}
