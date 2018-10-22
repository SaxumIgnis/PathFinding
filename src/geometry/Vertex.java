package geometry;

import java.util.ArrayList;

public class Vertex extends Point {

	private ArrayList<HalfEdge> edges;
	private AccessAngle angle;
	
	Vertex(double x, double y, double z) {
		super(x, y, z);
		this.edges = new ArrayList<HalfEdge>(2);
		angle = new AccessAngle(true);
	}
	
	public void addEdge(HalfEdge e) {
		this.edges.add(e);
		if (e.getCross() == Double.POSITIVE_INFINITY) this.angle = this.angle.addVector(e.vector());
	}
	
	public void updateAngle() {
		this.angle = new AccessAngle(true);
		for (HalfEdge e : this.edges) if (e.getCross() == Double.POSITIVE_INFINITY) this.angle = this.angle.addVector(e.vector());
	}
	
	public boolean allows(Vector out) {
		return this.angle.allows(out);
	}
	
	public HalfEdge getEdge() {
		return this.edges.get(0);
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
	
	private double distanceToAccessibleNeighbour(HalfEdge e) {
		return Math.max(e.getPolygon().coeffSpeed(e.vector()), e.getOpposite().getPolygon().coeffSpeed(e.vector()));
	}
	
	public double distanceToNeighbour(Vertex v) {
		for (HalfEdge e : this.edges) {
			if (v.equals(e.getOpposite().getOrigin())) {
				if (e.getPolygon().isCrossable() || e.getNext().getPolygon().isCrossable()) {
					if (this.angle.allowsLarge(v) && v.angle.allowsLarge(this)) {
						if (e.getCross() < Double.POSITIVE_INFINITY || this.angle.isCompatible(v.angle))
							return this.distanceToAccessibleNeighbour(e);

					}
				}
				return Double.POSITIVE_INFINITY;
			}
		}
		return Double.POSITIVE_INFINITY;
	}
	
}
