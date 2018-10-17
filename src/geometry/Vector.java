package geometry;

public class Vector extends Point {

	public Vector(double x, double y, double z) {
		super(x, y, z);
	}
	
	public double length() {
		return(Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2)));
	}
	
	Vector norm() {
		return(this.mult(1 / this.length()));
	}
	
	Vector mult(double f) {
		return new Vector(
				this.x * f,
				this.y * f,
				this.z * f
				);
	}

	double scalarProd(Vector v) {
		return(this.x * v.x + this.y * v.y + this.z * v.z);
	}
	
	Vector vectorProduct(Vector v) {
		return new Vector(
				this.y * v.z - this.z * v.y,
				this.z * v.x - this.x * v.z,
				this.x * v.y - this.y * v.x
				);
	}
	
	public double angle2D() {
		// retourne l'angle (en rad) par rapport à la verticale dans le sens direct entre -pi et pi
		return -Math.atan2(this.x, this.y);
	}
	
	public double angle2D(Vector v) {
		// AB.angle2D(AC) retourne l'angle BAC en rad entre -pi et pi
		double res = v.angle2D() - this.angle2D();
		while (res > Math.PI) res -= Math.PI * 2;
		while (res <= -Math.PI) res += Math.PI * 2;
		return res;
	}

	Vector toPlan() {
		return new Vector(this.x, this.y, (double) 0);
	}
}
