package pathFinding;

public class Vertex extends Point {

	HalfEdge e;
	
	Vertex(double x, double y, double z) {
		super(x, y, z);
		this.e = null;
	}
	
	Vertex(double x, double y, double z, HalfEdge e) {
		super(x, y, z);
		this.e = e;
	}
	
	void set_edge(HalfEdge e) {
		this.e = e;
	}
	
	double cross(Point in, Point out) {
		// TODO
		return 0;
	}
	
}
