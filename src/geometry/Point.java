package geometry;

import java.util.Comparator;

public class Point extends Object implements Comparable<Point> {
	
	protected final double x;
	protected final double y;
	protected final double z;
	public final int tag;
	// TODO : remove tag
	
	public Point(double x, double y, double z, int tag) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.tag = tag;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}
	
	public double distance(Point p) {
		return(this.minus(p).length());
	}
	
	public double distancePlan(Point p) {
		return(Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2)));
	}
	
	public Vector minus(Point p) {
		return new Vector(
				this.x - p.x,
				this.y - p.y,
				this.z - p.z
				);
	}
	
	Point plus(Vector v) {
		return new Point(
				this.x + v.x,
				this.y + v.y,
				this.z + v.z,
				-1
				);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point p = (Point) o;
			return (this.x == p.x) && (this.y == p.y) && (this.z == p.z);
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Point p) {
		if (this.x < p.x) return -1;
		if (this.x > p.x) return 1;
		return 0;
	}
	
	public LocatedPoint locate(Polygon polygon) {
		LocatedPoint p = (LocatedPoint) this;
		p.polygon = polygon;
		return p;
	}
	
	public static final class ComparePoints implements Comparator<Point> {

		private final Vector direction;
		
		public ComparePoints() {
			this.direction = new Vector(1, 0, 0);
		}
		
		public ComparePoints(Vector dir) {
			this.direction = dir;
		}
		
		@Override
		public int compare(Point a, Point b) {
			double prod = this.direction.scalarProd(a.minus(b));
			if (prod > 0) return 1;
			if (prod < 0) return -1;
			return 0;
		}
		
	}
	
	public Vertex toVertex() {
		return new Vertex(x, y, z, tag);
	}
	
	@Override
	public String toString() {
		return "Point " + tag + " (" + this.hashCode() + ") - x = " + x + " - y = " + y + " - z = " + z;
	}
}
