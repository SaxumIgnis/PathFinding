package pathFinding;

public class Polygon {

	final HalfEdge e;
	final double scalarCoeff;
	final Vector vectorCoeff;
	
	Polygon(HalfEdge e, double s) {
		this.e = e;
		this.scalarCoeff = s;
		Vector normal = e.get_next().vector().vector_product(e.get_opposite().vector()).norm();
		this.vectorCoeff = normal.to_plan();
	}
	
	double coeff_speed(Vector dir) {
		return(this.scalarCoeff * (1 + dir.norm().scalar_prod(this.vectorCoeff)));
	}
	
}
