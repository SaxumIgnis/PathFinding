package geometry;

public class Polygon implements Comparable<Polygon>{

	final HalfEdge edge;
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
		this.crossable = (normal.z > 0.5);
		
		// vecteur normal à la surface orienté vers le haut
		this.vectorCoeff = normal.toPlan();
		// projection du vecteur normal dans le plan horizontal => opposé du gradient d'altitude
	}
	
	public double coeffSpeed(Vector dir) {
		if (this.crossable) {
			return(Math.max(0, Math.min(this.scalarCoeff * (dir.norm().scalarProd(this.vectorCoeff) * 1.5 + 1), MAXSPEED)));			
		} else return 0;
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

			HalfEdge e = this.edge;
			do {
				if (e.equals(polygon.edge)) return true;
				e = e.getNext();
			} while (!e.equals(this.edge));
		} 
		return false;
	}
	
	double area() {
		// aire totale du ploygone (problème si le polygone n'est pas convexe) par somme des aires des triangles ayant p pour sommet
		Point p = (Point) this.edge.getOrigin();
		HalfEdge currentEdge = this.edge.getNext();
		double res = 0;
		do {
			res += currentEdge.getOrigin().minus(p).vectorProduct(currentEdge.getVector()).length();
			currentEdge = currentEdge.getNext();
		} while (!currentEdge.getNext().equals(this.edge));
		return res/2;
	}
	
	double areaFlat() {
		// aire algébrique du polygone projeté dans le plan horizontal (positive si sens direct, négative si sens indirect)
		Point p = (Point) this.edge.getOrigin();
		HalfEdge currentEdge = this.edge.getNext();
		double res = 0;
		do {
			Point q = currentEdge.getOrigin();
			Vector v = currentEdge.getVector();
			res += (q.x - p.x) * v.y - (q.y - p.y) * v.x;
			currentEdge = currentEdge.getNext();
		} while (!currentEdge.getNext().equals(this.edge));
		return res/2;
	}
	
	public LocatedPoint center() {
		Point a = (Point) this.edge.getOrigin();
		Point b = (Point) this.edge.getNext().getOrigin();
		Point c = (Point) this.edge.getNext().getNext().getOrigin();
		return a.plus(b.minus(a).mult(1/3)).plus(c.minus(a).mult(1/3)).locate(this);
	}
}
