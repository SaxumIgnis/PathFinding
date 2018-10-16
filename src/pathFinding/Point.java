package pathFinding;

class Point {
	
	protected final double x;
	protected final double y;
	protected final double z;
	
	Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	double distance(Point p) {
		return(this.minus(p).length());
	}
	
	Vector minus(Point p) {
		return new Vector(
				this.x - p.x,
				this.y - p.y,
				this.z - p.z
				);
	}
	
	Point add(Vector v) {
		return new Point(
				this.x + v.x,
				this.y + v.y,
				this.z + v.z
				);
	}
	
	
}
