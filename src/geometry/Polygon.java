package geometry;

public class Polygon implements Comparable<Polygon>{

	final HalfEdge edge;
	private final double scalarCoeff;
	private final Vector vectorCoeff;
	private final double MAXSPEED = 1.5;
	private final boolean crossable;
	public final int tag;
	
	public Polygon(HalfEdge e, double s, int tag) {
		this.edge = e;
		this.scalarCoeff = s;
		Vector normal = e.getNext().vector().vectorProduct(e.getOpposite().vector()).norm();
		if (normal.z < 0) {
			normal = normal.mult(-1);
		}
		this.crossable = (normal.z > 0.5);
		
		// vecteur normal à la surface orienté vers le haut
		this.vectorCoeff = normal.toPlan();
		// projection du vecteur normal dans le plan horizontal => opposé du gradient d'altitude
		
		this.tag = tag;
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
				currentEdge.setCross(Double.POSITIVE_INFINITY);
				currentEdge = currentEdge.getNext();
			} while (currentEdge != this.edge);
		}
	}

	public boolean isCrossable() {
		return this.crossable;
	}
	
	@Override
	public int compareTo(Polygon polygon) {
		return this.tag - polygon.tag;
	}
	
	double area() {
		// aire totale du ploygone (problème si le polygone n'est pas convexe) par somme des aires des triangles ayant p pour sommet
		Point p = (Point) this.edge.getOrigin();
		HalfEdge currentEdge = this.edge.getNext();
		double res = 0;
		do {
			res += currentEdge.getOrigin().minus(p).vectorProduct(currentEdge.vector()).length();
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
			Vector v = currentEdge.vector();
			res += (q.x - p.x) * v.y - (q.y - p.y) * v.x;
			currentEdge = currentEdge.getNext();
		} while (!currentEdge.getNext().equals(this.edge));
		return res/2;
	}
}
