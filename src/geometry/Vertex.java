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

public final class Vertex extends Point implements Iterable<HalfEdge>{

	private HalfEdge firstEdge;
	private AccessAngle angle;

	public Vertex(double x, double y, double z, int tag) {
		super(x, y, z, tag);
		this.firstEdge = null;
		angle = new AccessAngle(this, true);
	}

	public void updateAngle() {
		this.angle = new AccessAngle(this, true);
		for (HalfEdge e : this) if (!e.crossable()) this.angle = this.angle.addPoint(e.getEnd());
	}

	public boolean allows(Point out) {
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

	private double speedToAccessibleNeighbour(HalfEdge e) {
		return Math.max(e.getPolygon().coeffSpeed(e.getVector()), e.getOpposite().getPolygon().coeffSpeed(e.getVector()));
	}

	public double speedToNeighbour(Vertex v) {
		for (HalfEdge e : this)
		{
			if (v.equals(e.getEnd()))
			{
				if (e.getPolygon().isCrossable() || e.getOpposite().getPolygon().isCrossable())
				{
					if (this.angle.allowsLarge(v) && v.angle.allowsLarge(this))
					{
						if (e.crossable() || this.angle.isCompatible(v.angle))
							return this.speedToAccessibleNeighbour(e);

					}
				}
				return 0;
			}
		}
		return 0;
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
			//System.out.println("sommet "+this.tag+" n'avait pas de voisins");
			e.getOpposite().setNext(e);
			new Polygon(e, 1);
			this.firstEdge = e;

			//System.out.println("Polygon " + e.getPolygon());
		} else {
			HalfEdge previous = this.directionPolygon(e.getEnd());
			HalfEdge next = previous.getNext();
			//System.out.println(previous + " is previous for " + e);
			//System.out.println(next + " is next for " + e);
			previous.setNext(e);
			e.getOpposite().setNext(next);

			// mise à jour des polygones
			new Polygon(e, 1);

			//if (previous.getPolygon() == null)
			//	System.out.println("arete " + previous.getOrigin().tag + " -> " + previous.getEnd().tag + " has no plygon");
			//System.out.println("Polygon 1 " + previous.getPolygon());

			if (e.getOpposite().getPolygon() == null) {
				new Polygon(e.getOpposite(), 1);
			}
			//System.out.println("Polygon 2 " + e.getOpposite().getPolygon());

			this.firstEdge = e;
		}
	}

	public void addEdge(Vertex end, boolean crossable) {
		if (!this.isNeighbour(end) && !this.equals(end)) {
			HalfEdge e = new HalfEdge(this, end, crossable);
			e.setOpposite(new HalfEdge(e.getEnd(), e.getOrigin(), e.crossable()));

			if (this.firstEdge == null) {
				//	System.out.println("sommet "+this.tag+" n'avait pas de voisins");
				e.getOpposite().setNext(e);
			} else {
				HalfEdge previous = this.directionPolygon(e.getEnd());
				//	System.out.println(previous + " is previous for " + e);
				HalfEdge next = previous.getNext();
				//	System.out.println(next + " is next for " + e);
				previous.setNext(e);
				e.getOpposite().setNext(next);
			}
			this.firstEdge = e;
			e.getEnd().addHalfEdge(e.getOpposite());
		}
	}

	public void update() {
		this.angle = new AccessAngle(this, true);
		for (HalfEdge edge : this) {
			if (!edge.equals(edge.getNext().getNext().getNext())) {
				// le polygone n'est pas un triangle => on l'interdit
				edge.setPolygon(new Polygon(edge, 0));
				edge.setCross(false);
				this.angle = new AccessAngle(this, false);
				// le seul polygone non triangulaire est le contour (convexe) => le sommet n'est pas utilisable pour les chemins intérieurs

				//	System.out.println(" --> "+edge.getNext()+" --> "+edge.getNext().getNext()+" --> "+edge.getNext().getNext().getNext());
			} else {
				//	System.out.println(edge.getNext().getPolygon());
				//	System.out.println();
				if (!edge.crossable())
				{
					System.out.print(this.tag);
					this.angle = this.angle.addPoint(edge.getEnd());
					System.out.println(this.tag + " -> " + edge.getOpposite().getOrigin().tag);
					System.out.println(this);
				}

				
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
	
	@Override
	public String toString() {
		return "Sommet " + this.tag + " acces " + this.angle;
	}

	public void forbid() {
		this.angle = new AccessAngle(this, false);
	}
}
