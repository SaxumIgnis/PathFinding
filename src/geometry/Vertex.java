package geometry;

import java.util.ArrayList;

public class Vertex extends Point {

	private ArrayList<HalfEdge> edges;
	
	Vertex(double x, double y, double z) {
		super(x, y, z);
		this.edges = new ArrayList<HalfEdge>(2);
	}
	
	public void addEdge(HalfEdge e) {
		this.edges.add(e);
	}
	
	public double cross(Point in, Point out) {
		// TODO
		return 0;
	}
	
	public boolean isNeighbour(Vertex v) {
		for (HalfEdge e : this.edges) {
			if (v.equals(e.getOpposite().getOrigin())) return true;
		}
		return false;
	}
	
	public HalfEdge edgeToNeighbour(Vertex v) {
		for (HalfEdge e : this.edges) {
			if (v.equals(e.getOpposite().getOrigin())) return e;
		}
		return null;		
	}
	
}
