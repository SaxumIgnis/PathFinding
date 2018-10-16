package pathFinding;

public class HalfEdge {

	private Point origin;
	private HalfEdge opposite;
	private HalfEdge next;
	private Polygon polygon;
	double crossTime;
	
	HalfEdge(Point p, double f) {
		this.origin = p;
		this.set_opposite(null);
		this.set_next(null);
		this.polygon = null;
		this.crossTime = f;
	}

	HalfEdge get_next() {
		return next;
	}

	HalfEdge get_opposite() {
		return opposite;
	}
	void set_origin(Vertex v) {
		this.origin = v;
	}
	
	void set_polygon(Polygon p) {
		this.polygon = p;
	}
	
	void set_opposite(HalfEdge o) {
		this.opposite = o;
		o.opposite = this;
	}
	
	void set_next(HalfEdge n) {
		this.next = n;
		this.polygon = n.polygon;
	}
	
	Point intersection(Point a, Point b) {
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
