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
	
	public void updateAngle() {
		this.angle = new AccessAngle(true);
		for (HalfEdge e : this.edges) if (!e.getCross()) this.angle = this.angle.addVector(e.getVector());
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
		return Math.max(e.getPolygon().coeffSpeed(e.getVector()), e.getOpposite().getPolygon().coeffSpeed(e.getVector()));
	}
	
	public double distanceToNeighbour(Vertex v) {
		for (HalfEdge e : this.edges) {
			if (v.equals(e.getOpposite().getOrigin())) {
				if (e.getPolygon().isCrossable() || e.getNext().getPolygon().isCrossable()) {
					if (this.angle.allowsLarge(v) && v.angle.allowsLarge(this)) {
						if (e.getCross() || this.angle.isCompatible(v.angle))
							return this.distanceToAccessibleNeighbour(e);

					}
				}
				return Double.POSITIVE_INFINITY;
			}
		}
		return Double.POSITIVE_INFINITY;
	}
	
	private HalfEdge directionPolygon(Point dir) {
		
		Vector v = dir.minus(this);
		
		// recherche parmi les angles aigus
		for (HalfEdge e : this.edges) {
			if (v.rotSense(e.getVector()) == 1 && v.rotSense(e.getOpposite().getNext().getVector()) == -1 &&
					e.getOpposite().getNext().getVector().rotSense(e.getVector()) == 1) {
				return e.getOpposite();
			}
		}
		
		// recherche d'un angle obtus
		for (HalfEdge e : this.edges) {
			if (e.getOpposite().getNext().getVector().rotSense(e.getVector()) == -1) {
				return e.getOpposite();
			}
		}
		
		return this.edges.get(0);
			
	}
	
	private void addHalfEdge(HalfEdge e) {
		if (this.edges.isEmpty()) {
			e.getOpposite().setNext(e);
			e.setPolygon(new Polygon(e, 1));
			e.getOpposite().setPolygon(e.getPolygon());
		} else {
			if (!this.edges.contains(e)) {
				HalfEdge previous = this.directionPolygon(e.getEnd());
				HalfEdge next = previous.getNext();
				previous.setNext(e);
				e.getOpposite().setNext(next);
				
				// mise à jour des polygones
				
				e.updatePolygon(previous.getPolygon());
				
				if (e.getOpposite().getPolygon() == null) {
					e.getOpposite().updatePolygon(new Polygon(e.getOpposite(), previous.getPolygon().coeffSpeed(new Vector(0, 0, 0))));
				}
				
				this.edges.add(e);
				if (!e.getCross()) this.angle = this.angle.addVector(e.getVector());
			}
		}
	}
	
	public void addEdge(Vertex end, boolean crossable) {
		if (!this.isNeighbour(end)) {
			HalfEdge e = new HalfEdge(this, end, crossable);
			if (e.getOpposite() == null) {
				e.setOpposite(new HalfEdge(e.getEnd(), e.getOrigin(), e.getCross()));
			}
			if (this.edges.isEmpty()) {
				e.getOpposite().setNext(e);
			} else {
				HalfEdge previous = this.directionPolygon(e.getEnd());
				HalfEdge next = previous.getNext();
				previous.setNext(e);
				e.getOpposite().setNext(next);
			}
			this.edges.add(e);
			e.getEnd().addHalfEdge(e.getOpposite());
			if (!e.getCross()) this.angle = this.angle.addVector(e.getVector());
		}
	}
	
	public void update() {
		for (HalfEdge edge : this.edges) {
			if (!edge.equals(edge.getNext().getNext().getNext())) {
				// le polygone n'est pas un triangle => on l'interdit
				edge.setPolygon(null);
				edge.setCross(false);
			}
			if (!edge.getPolygon().equals(edge.getNext().getPolygon())) {
				edge.updatePolygon(edge.getPolygon());
			}
		}
	}
}
