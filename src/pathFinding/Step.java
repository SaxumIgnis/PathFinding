package pathFinding;

import geometry.HalfEdge;
import geometry.Point;
import geometry.Polygon;
import geometry.Vector;

class StepEnd {
	final Step innerStep;
	final HalfEdge intersectedEdge;
	
	StepEnd(Step innerStep, HalfEdge intersectedEdge) {
		this.innerStep = innerStep;
		this.intersectedEdge = intersectedEdge;
	}
}

class Step {
	
	private final double speed;
	private final double startTime;
	private final Point origin;
	private final Point end;
	
	Step(double speed, Point a, Point b) throws BlockedPathException {
		if (speed <= 0) throw new BlockedPathException();
		this.speed = speed;
		this.startTime = 0;
		this.origin = a;
		this.end = b;
	}
	
	Step(double speed, double startTime, Point a, Point b) throws BlockedPathException {
		if (speed <= 0) throw new BlockedPathException();
		this.speed = speed;
		this.startTime = startTime;
		this.origin = a;
		this.end = b;
	}
	
	Vector vector() {
		return this.end.minus(this.origin);
	}
	
	double speed() {
		return this.speed;
	}
	
	Point getOrigin() {
		return this.origin;
	}

	Point getEnd() {
		return this.end;
	}
	
	double length() {
		return this.vector().length() / this.speed + this.startTime;
	}
	
	StepEnd nextStep(Point aim, HalfEdge intersectedEdge) throws BlockedPathException {
		HalfEdge toTestEdge = intersectedEdge.getOpposite().getNext();
		Point intersection = null;
		while (!toTestEdge.equals(intersectedEdge.getOpposite())) {
			intersection = toTestEdge.intersection(this.end, aim);
			
			if (intersection != null) return new StepEnd(
					// l'objectif se trouve en dehors du polygone
					new Step(
							toTestEdge.getPolygon().coeffSpeed(aim.minus(this.end)), 
							intersectedEdge.getCross(), 
							this.end, 
							intersection
							),
					toTestEdge
					);
			
			toTestEdge = toTestEdge.getNext();
		}
		
		// l'objectif est à l'intérieur du polygone
		return new StepEnd(
				new Step(
						toTestEdge.getPolygon().coeffSpeed(aim.minus(this.end)), 
						intersectedEdge.getCross(), 
						this.end, 
						aim
						),
				null
				);
	}
	
	static StepEnd firstStep(Point origin, Point aim, Polygon area) throws BlockedPathException {
		HalfEdge toTestEdge = area.getEdge();
		Point intersection = null;
		do {
			intersection = toTestEdge.intersection(origin, aim);
			
			if (intersection != null) return new StepEnd(
					// l'objectif se trouve en dehors du polygone
					new Step(
							toTestEdge.getPolygon().coeffSpeed(aim.minus(origin)),
							origin,
							intersection
							),
					toTestEdge
					);
			
			toTestEdge = toTestEdge.getNext();
		} while (!toTestEdge.equals(area.getEdge()));
		
		// l'objectif est à l'intérieur du polygone
		return new StepEnd(
				new Step(
						toTestEdge.getPolygon().coeffSpeed(aim.minus(origin)),
						origin,
						aim
						),
				null
				);
	}

}
