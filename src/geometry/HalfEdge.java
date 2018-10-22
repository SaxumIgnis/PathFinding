package geometry;

public class HalfEdge {

	final private Point origin;
	private HalfEdge opposite;
	private HalfEdge next;
	private Polygon polygon;
	private double crossTime;
	
	public HalfEdge(Point p) {
		this.origin = p;
		this.opposite = null;
		this.next = null;
		this.polygon = null;
		this.crossTime = 0;
	}

	public HalfEdge getNext() {
		return next;
	}

	public HalfEdge getOpposite() {
		return opposite;
	}
	
	public void setPolygon(Polygon p) {
		this.polygon = p;
	}
	
	public void setOpposite(HalfEdge o) {
		this.opposite = o;
		o.opposite = this;
	}
	
	public void setNext(HalfEdge n) {
		this.next = n;
		this.polygon = n.polygon;
	}
	
	public void setCross(double time) {
		this.crossTime = time;
		this.opposite.crossTime = time;
		// c'est plus simple quand c'est symétrique
	}
	
	public double getCross() {
		return this.crossTime;
	}
	
	Point getOrigin() {
		return this.origin;
	}
	
	public Polygon getPolygon() {
		return this.polygon;
	}
	
	public Point intersection(Point a, Point b) {
		Point c = this.origin;
		Vector v = this.vector();
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
		Point i = c.add(this.vector().mult(t));
		double t0 = b.minus(a).scalarProd(i.minus(a).toPlan()) / Math.pow(a.distance(b), 2);
		
		if (t0 < 0 || t0 > 1 || t < 0 || t > 1) {
			return null;
		} else {
			return i;
		}
		
	}
	
	HalfEdge previous() {
		HalfEdge p = this.opposite;
		while (p.next != this) {
			p = p.next.opposite;
		}
		return p;
	}
	
	Vector vector() {
		return this.opposite.origin.minus(this.origin);
	}

	double speedAlong() {
		return Math.max(this.getPolygon().coeffSpeed(this.vector()), this.getOpposite().getPolygon().coeffSpeed(this.vector()));
	}
	
	public boolean isEdgeOf(Vertex v) {
		return v.equals(this.origin) || v.equals(this.next.origin);
	}
	
}
