package geometry;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class PolygonIterator implements Iterator<HalfEdge> {

	private HalfEdge startEdge;
	private HalfEdge edge;
	private boolean notLast;
	
	PolygonIterator(Polygon polygon) {
		this.startEdge = polygon.getEdge();
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
			this.edge = this.edge.getNext();
			if (this.edge.equals(this.startEdge)) this.notLast = false;
			return e;
		} else {
			throw new NoSuchElementException();
		} 
	}

}

public final class Polygon implements Comparable<Polygon>, Iterable<HalfEdge> {

	private final HalfEdge edge;
	private final double scalarCoeff;
	private final Vector vectorCoeff;
	private final double MAXSPEED = 1.5;
	private final boolean crossable;
	
	public Polygon(HalfEdge e, double s) {
		this.edge = e;
		this.scalarCoeff = s;
		Vector normal = e.getNext().getVector().vectorProduct(e.getOpposite().getVector()).norm();
		if (normal.z < 0) {
			normal = normal.mult(-1);
		}
		this.crossable = (normal.z > 0.5) && s > 0;
		
		// vecteur normal à la surface orienté vers le haut
		this.vectorCoeff = normal.toPlan();
		// projection du vecteur normal dans le plan horizontal => opposé du gradient d'altitude
		
		for (HalfEdge iEdge : this) {
			iEdge.setPolygon(this);
			//System.out.println(iEdge + " is in polygon " + this.hashCode());
		}
	}
	
	public double coeffSpeed(final Vector dir) {
		if (this.crossable) {
			return(Math.max(0, Math.min(this.scalarCoeff * (dir.norm().scalarProd(this.vectorCoeff) * 1.5 + 1), MAXSPEED)));
		} else {
			return 0;
		}
		// valeur entre 0 et 1.5
	}
	
	public HalfEdge getEdge() {
		return this.edge;
	}
	
	public void updateEdges() {
		if (!this.crossable) {
			HalfEdge currentEdge = this.edge;
			do {
				currentEdge.setCross(false);
				currentEdge = currentEdge.getNext();
			} while (currentEdge != this.edge);
		}
	}

	public boolean isCrossable() {
		return this.crossable;
	}
	
	@Override
	public int compareTo(Polygon polygon) {
		return (int) Math.signum(this.areaFlat() - polygon.areaFlat());
	}
	
	@Override
	public boolean equals(Object arg) {
		if (arg instanceof Polygon) {
			Polygon polygon = (Polygon) arg;

			for (HalfEdge e : polygon) {
				if (e.equals(this.edge)) return true;
			}
		} 
		return false;
	}
	
	double area() {
		// aire totale du ploygone (problème si le polygone n'est pas convexe) par somme des aires des triangles ayant p pour sommet
		Point p = (Point) this.edge.getOrigin();
		double res = 0;
		for (HalfEdge e : this) {
			res += e.getOrigin().minus(p).vectorProduct(e.getVector()).length();
		}
		return res/2;
	}
	
	double areaFlat() {
		// aire algébrique du polygone projeté dans le plan horizontal (positive si sens direct, négative si sens indirect)
		Point p = (Point) this.edge.getOrigin();
		double res = 0;
		for (HalfEdge e : this) {
			Point q = e.getOrigin();
			Vector v = e.getVector();
			res += (q.x - p.x) * v.y - (q.y - p.y) * v.x;
		}
		return res/2;
	}
	
	public LocatedPoint center() {
		Point a = (Point) this.edge.getOrigin();
		Point b = (Point) this.edge.getNext().getOrigin();
		Point c = (Point) this.edge.getNext().getNext().getOrigin();
		LocatedPoint center = (LocatedPoint) a.plus(b.minus(a).mult(1/3)).plus(c.minus(a).mult(1/3));
		center.polygon = this;
		return center;
	}

	@Override
	public Iterator<HalfEdge> iterator() {
		return new PolygonIterator(this);
	}
	
	@Override
	public String toString() {
		String res = "Polygon";
		for (HalfEdge e : this) {
			res += "  " + e;
		}
		return res;
	}
}
