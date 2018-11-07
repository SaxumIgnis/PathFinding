package geometry;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class VertexIterator implements Iterator<HalfEdge> {
	
	private HalfEdge startEdge;
	private HalfEdge edge;
	private boolean notLast;
	
	VertexIterator(Vertex vertex) {
		this.startEdge = vertex.getEdge();
		this.edge = this.startEdge;
		this.notLast = true;
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		if (this.startEdge == null) return false;
		return this.notLast;
	}

	@Override
	public HalfEdge next() {
		if (this.hasNext()) {
			HalfEdge e = this.edge;
			this.edge = this.edge.getOpposite().getNext();
			if (this.edge.equals(this.startEdge)) this.notLast = false;
			return e;
		} else {
			throw new NoSuchElementException();
		}
	}

}

public class Vertex extends Point implements Iterable<HalfEdge>{

	private HalfEdge firstEdge;
	private AccessAngle angle;
	
	public Vertex(double x, double y, double z, int tag) {
		super(x, y, z, tag);
		this.firstEdge = null;
		angle = new AccessAngle(true);
	}
	
	public void updateAngle() {
		this.angle = new AccessAngle(true);
		for (HalfEdge e : this) if (!e.getCross()) this.angle = this.angle.addVector(e.getVector());
	}
	
	public boolean allows(Vector out) {
		return this.angle.allows(out);
	}
	
	public HalfEdge getEdge() {
		return this.firstEdge;
	}
	
	public boolean isNeighbour(Vertex v) {
		for (HalfEdge e : this) {
			if (v.equals(e.getOpposite().getOrigin())) return true;
		}
		return false;
	}
	
	public HalfEdge edgeToNeighbour(Vertex v) {
		for (HalfEdge e : this) {
			if (v.equals(e.getOpposite().getOrigin())) return e;
		}
		return null;
	}
	
	private double distanceToAccessibleNeighbour(HalfEdge e) {
		return Math.max(e.getPolygon().coeffSpeed(e.getVector()), e.getOpposite().getPolygon().coeffSpeed(e.getVector()));
	}
	
	public double distanceToNeighbour(Vertex v) {
		for (HalfEdge e : this) {
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
		for (HalfEdge e : this) {
			if (v.rotSense(e.getVector()) == 1 && v.rotSense(e.getOpposite().getNext().getVector()) == -1 &&
					e.getOpposite().getNext().getVector().rotSense(e.getVector()) == 1) {
				return e.getOpposite();
			}
		}
		
		// recherche d'un angle obtus
		for (HalfEdge e : this) {
			if (e.getOpposite().getNext().getVector().rotSense(e.getVector()) == -1) {
				return e.getOpposite();
			}
		}
		
		return this.firstEdge.getOpposite();
			
	}
	
	private void addHalfEdge(HalfEdge e) {
		if (this.firstEdge == null) {
			System.out.println("sommet "+this.tag+" n'avait pas de voisins");
			e.getOpposite().setNext(e);
			new Polygon(e, 1);
			this.firstEdge = e;

			System.out.println("Polygon " + e.getPolygon());
		} else {
			HalfEdge previous = this.directionPolygon(e.getEnd());
			HalfEdge next = previous.getNext();
			System.out.println(previous + " is previous for " + e);
			System.out.println(next + " is next for " + e);
			previous.setNext(e);
			e.getOpposite().setNext(next);

			// mise à jour des polygones
			new Polygon(e, 1);

			if (previous.getPolygon() == null)
				System.out.println("arete " + previous.getOrigin().tag + " -> " + previous.getEnd().tag + " has no plygon");
			System.out.println("Polygon 1 " + previous.getPolygon());

			if (e.getOpposite().getPolygon() == null) {
				new Polygon(e.getOpposite(), 1);
			}
			System.out.println("Polygon 2 " + e.getOpposite().getPolygon());

			this.firstEdge = e;
			if (!e.getCross()) this.angle = this.angle.addVector(e.getVector());
		}
	}
	
	public void addEdge(Vertex end, boolean crossable) {
		if (!this.isNeighbour(end) && !this.equals(end)) {
			HalfEdge e = new HalfEdge(this, end, crossable);
			e.setOpposite(new HalfEdge(e.getEnd(), e.getOrigin(), e.getCross()));

			if (this.firstEdge == null) {
				System.out.println("sommet "+this.tag+" n'avait pas de voisins");
				e.getOpposite().setNext(e);
			} else {
				HalfEdge previous = this.directionPolygon(e.getEnd());
				System.out.println(previous + " is previous for " + e);
				HalfEdge next = previous.getNext();
				System.out.println(next + " is next for " + e);
				previous.setNext(e);
				e.getOpposite().setNext(next);
			}
			this.firstEdge = e;
			e.getEnd().addHalfEdge(e.getOpposite());
			if (!e.getCross()) this.angle = this.angle.addVector(e.getVector());
		}
	}
	
	public void update() {
		for (HalfEdge edge : this) {
			if (!edge.equals(edge.getNext().getNext().getNext())) {
				// le polygone n'est pas un triangle => on l'interdit
				edge.setPolygon(null);
				edge.setCross(false);
				System.out.println(" --> "+edge.getNext()+" --> "+edge.getNext().getNext()+" --> "+edge.getNext().getNext().getNext());
			} else {
				System.out.println(edge.getNext().getPolygon());
				System.out.println();

				if (!edge.getPolygon().equals(edge.getNext().getPolygon())) {
					for (HalfEdge iEdge : edge.getPolygon()) {
						iEdge.setPolygon(edge.getPolygon());
					}
				}
			}
		}
	}

	@Override
	public Iterator<HalfEdge> iterator() {
		return new VertexIterator(this);
	}
}
