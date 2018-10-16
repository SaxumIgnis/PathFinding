package geometry;

public class HalfEdge {

	private Point origin;
	private HalfEdge opposite;
	private HalfEdge next;
	private Polygon polygon;
	private double crossTime;
	
	public HalfEdge(Point p) {
		this.origin = p;
		this.set_opposite(null);
		this.set_next(null);
		this.polygon = null;
		this.crossTime = 0;
	}

	public HalfEdge get_next() {
		return next;
	}

	public HalfEdge get_opposite() {
		return opposite;
	}
	void set_origin(Vertex v) {
		this.origin = v;
	}
	
	public void set_polygon(Polygon p) {
		this.polygon = p;
	}
	
	public void set_opposite(HalfEdge o) {
		this.opposite = o;
		o.opposite = this;
	}
	
	public void set_next(HalfEdge n) {
		this.next = n;
		this.polygon = n.polygon;
	}
	
	void set_cross(double time) {
		this.crossTime = time;
	}
	
	double get_cross() {
		return this.crossTime;
	}
	
	Point get_origin() {
		return this.origin;
	}
	
	public Point intersection(Point a, Point b) {
		// TODO
		return null;
	}
	
	HalfEdge previous() {
		HalfEdge p = this.get_opposite();
		while (p.get_next() != this) {
			p = p.get_next().get_opposite();
		}
		return p;
	}
	
	Vector vector() {
		return this.get_opposite().origin.minus(this.origin);
	}

}
