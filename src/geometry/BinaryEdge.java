package geometry;

public class BinaryEdge implements Comparable<BinaryEdge> {

	protected final Vertex origin;
	private final Vertex end;
	protected boolean crossable;
	
	public BinaryEdge(Vertex origin, Vertex end, boolean crossable) {
		this.origin = origin;
		this.end = end;
		this.crossable = crossable;
	}
	
	public double length() {
		return this.getEnd().distance(this.getOrigin());
	}


	public Vertex getOrigin() {
		return this.origin;
	}
	
	public Vertex getEnd() {
		return end;
	}
	@Override
	public int compareTo(BinaryEdge edge) {
		return (int) Math.signum(edge.length() - edge.length());
	}
	
	@Override
	public boolean equals(Object arg) {
		if (arg instanceof BinaryEdge) {
			BinaryEdge edge = (BinaryEdge) arg;
			return (this.getEnd() == edge.getEnd() && this.getOrigin() == edge.getOrigin()) || (this.getOrigin() == edge.getEnd() && this.getEnd() == edge.origin);
		} else {
			return false;
		}			
	}
	
	public Vector getVector() {
		return this.getEnd().minus(this.origin);
	}
	
	public Point intersection(Point a, Point b) {
		Point c = this.getOrigin();
		Vector v = this.getVector();
		double det = (b.x - a.x) * v.y - (v.x * (b.y - a.y));
		
		if (det == 0) return null; // les deux droites sont paralleles : pas d'intersection
		
		double x = ((b.x - a.x) * v.x * (a.y - c.y) + (b.x - a.x) * v.y * c.x - (b.y - a.y) * v.y * a.x) / det;
		double y;
		if (a.x == b.x) {
			y = ((x - c.x) * v.y / v.x + c.y);
		} else {
			y = ((x - a.x) * (b.y - a.y) / (b.x - a.x) + a.y);
		}
		
		double t = v.scalarProd(new Vector(x - c.x, y - c.y, 0)) / Math.pow(v.length(), 2);
		Point i = c.plus(this.getVector().mult(t));
		double t0 = b.minus(a).scalarProd(i.minus(a).toPlan()) / Math.pow(a.distance(b), 2);
		
		if (t0 < 0 || t0 > 1 || t < 0 || t > 1) {
			return null;
		} else {
			return i;
		}
		
	}
	
	public Point intersection(BinaryEdge edge) {
		return edge.intersection(this.getOrigin(), this.getEnd());
	}
	
	public boolean getCross() {
		return this.crossable;
	}
	
	public boolean isEdgeOf(Vertex v) {
		return v.equals(this.origin) || v.equals(this.getEnd());
	}

	public void setCross(boolean crossable) {
		this.crossable = crossable;
	}
}
