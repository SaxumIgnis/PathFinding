package pathFinding;

public class Polygon {

	final HalfEdge e;
	private final double scalarCoeff;
	private final Vector vectorCoeff;
	
	Polygon(HalfEdge e, double s) {
		this.e = e;
		this.scalarCoeff = s;
		Vector normal = e.get_next().vector().vector_product(e.get_opposite().vector()).norm();
		// vecteur normal à la surface orienté vers le haut
		this.vectorCoeff = normal.to_plan();
	}
	
	double coeff_speed(Vector dir) {
		return(this.scalarCoeff * (Math.min(0.5, Math.max(-1, dir.norm().scalar_prod(this.vectorCoeff) * 2)) + 1));
	}
	
}
