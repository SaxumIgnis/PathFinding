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

	public double lengthPlan() {
		return this.getEnd().distancePlan(this.getOrigin());
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
	
	@Override
	public int hashCode() {
		return origin.tag * end.tag;
	}
	
	public Vector getVector() {
		return this.getEnd().minus(this.origin);
	}
	
	public Point intersection(Point a, Point b) {
		Point c = this.getOrigin();
		Vector v1 = this.getVector();
		Vector v2 = b.minus(a);
		double det = (v1.x * v2.y) - (v1.y * v2.x);
		
		if (det == 0) return null; // les deux droites sont paralleles : pas d'intersection
		
		double x = (v2.x * (v1.x * c.y - v1.y * c.x) - v1.x * (v2.x * a.y - v2.y * a.x)) / det;
		double y;
		if (a.x == b.x) {
			y = (x - c.x) * v1.y / v1.x + c.y;
		} else {
			y = (x - a.x) * v2.y / v2.x + a.y;
		}
		
		double t = v1.scalarProd(new Vector(x - c.x, y - c.y, 0)) / Math.pow(v1.length(), 2);
		Point i = c.plus(this.getVector().mult(t));
		double t0 = v2.scalarProd(i.minus(a).toPlan()) / Math.pow(v2.length(), 2);
		
		if (t0 < 0 || t0 > 1 || t < 0 || t > 1) {
			return null;
		} else {
			return i;
		}
		
	}
	
	public Point intersection(BinaryEdge edge) {
		if (edge.getOrigin().equals(this.getOrigin()) ||
				edge.getOrigin().equals(this.getEnd()) ||
				edge.getEnd().equals(this.getOrigin()) ||
				edge.getEnd().equals(this.getEnd()))
			return null;
		Point intersect = edge.intersection(this.getOrigin(), this.getEnd());
		if (intersect != null) 
			System.out.println(edge + " coupe " + this);
		return intersect;
	}
	
	public boolean intersectsOne(Iterable<BinaryEdge> list) {
		for (BinaryEdge edge : list) {
			if (this.intersection(edge) != null) return true;
		}
		return false;
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
	
	@Override
	public String toString() {
		return "arete " + this.getOrigin().tag + " -> " + this.getEnd().tag;
	}
}
