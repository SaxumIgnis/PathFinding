package pathFinding;

import geometry.Point;
import geometry.Vector;

class Step {
	
	private final double speed;
	private final Point origin;
	private final Point end;
	
	Step(double speed, Point a, Point b) {
		this.speed = speed;
		this.origin = a;
		this.end = b;
	}
	
	Vector vector() {
		return this.end.minus(this.origin);
	}
	
	double speed() {
		return this.speed;
	}
	
	Point get_origin() {
		return this.origin;
	}
	
	double length() {
		return this.vector().length() / this.speed;
	}
}
