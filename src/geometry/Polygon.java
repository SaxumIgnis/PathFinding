package geometry;

public class Polygon {

	final HalfEdge edge;
	private final double scalarCoeff;
	private final Vector vectorCoeff;
	
	public Polygon(HalfEdge e, double s) {
		this.edge = e;
		this.scalarCoeff = s;
		Vector normal = e.get_next().vector().vector_product(e.get_opposite().vector()).norm();
		if (normal.z < 0) {
			normal = normal.mult(-1);
		}
		// vecteur normal à la surface orienté vers le haut
		this.vectorCoeff = normal.to_plan();
		// projection du vecteur normal dans le plan horizontal => opposé du gradient d'altitude
	}
	
	public double coeff_speed(Vector dir) {
		return(this.scalarCoeff * (Math.min(0.5, Math.max(-1, dir.norm().scalar_prod(this.vectorCoeff) * 2)) + 1));
	}
	
}
