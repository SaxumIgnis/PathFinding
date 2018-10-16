package pathFinding;

class Vector extends Point {

	public Vector(double x, double y, double z) {
		super(x, y, z);
	}
	
	double length() {
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

	double scalar_prod(Vector v) {
		return(this.x * v.x + this.y * v.y + this.z * v.z);
	}
	
	Vector vector_product(Vector v) {
		return new Vector(
				this.y * v.z - this.z * v.y,
				this.z * v.x - this.x * v.z,
				this.x * v.y - this.y * v.x
				);
	}
	
	double angle2D() {
		return Math.atan2(this.x,  this.y);
	}
	

	Vector to_plan() {
		return new Vector(this.x, this.y, (double) 0);
	}
}
