package pathFinding;

class Step {
	
	private final double speedCoeff;
	private final Point origin;
	private final Point end;
	
	Step(double s, Point a, Point b) {
		this.speedCoeff = s;
		this.origin = a;
		this.end = b;
	}
	
	Vector vector() {
		return this.end.minus(this.origin);
	}
	
	double speed() {
		return this.speedCoeff;
	}
	
	double length() {
		return this.vector().length() / this.speedCoeff;
	}
}
