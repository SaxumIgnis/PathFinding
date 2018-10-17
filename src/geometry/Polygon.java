package geometry;

public class Polygon {

	final HalfEdge edge;
	private final double scalarCoeff;
	private final Vector vectorCoeff;
	private final double MAXSPEED = 1.5;
	
	public Polygon(HalfEdge e, double s) {
		this.edge = e;
		this.scalarCoeff = s;
		Vector normal = e.getNext().vector().vectorProduct(e.getOpposite().vector()).norm();
		if (normal.z < 0) {
			normal = normal.mult(-1);
		}
		// vecteur normal à la surface orienté vers le haut
		this.vectorCoeff = normal.toPlan();
		// projection du vecteur normal dans le plan horizontal => opposé du gradient d'altitude
	}
	
	public double coeffSpeed(Vector dir) {
		return(Math.max(0, Math.min(this.scalarCoeff * (dir.norm().scalarProd(this.vectorCoeff) * 1.5 + 1), MAXSPEED)));
		// valeur entre 0 et 1.5
	}
	
	public HalfEdge getEdge() {
		return this.edge;
	}
}
