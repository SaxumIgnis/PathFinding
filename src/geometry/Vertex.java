package geometry;

import java.util.ArrayList;

public class Vertex extends Point {

	private ArrayList<HalfEdge> edges;
	
	Vertex(double x, double y, double z) {
		super(x, y, z);
		this.edges = new ArrayList<HalfEdge>(2);
	}
	
	public void add_edge(HalfEdge e) {
		this.edges.add(e);
	}
	
	public double cross(Point in, Point out) {
		// TODO
		return 0;
	}
	
	public boolean is_neighbour(Vertex v) {
		for (HalfEdge e : this.edges) {
			if (v.equals(e.get_opposite().get_origin())) return true;
		}
		return false;
	}
	
	public HalfEdge edge_to_neighbour(Vertex v) {
		for (HalfEdge e : this.edges) {
			if (v.equals(e.get_opposite().get_origin())) return e;
		}
		return null;		
	}
	
}
