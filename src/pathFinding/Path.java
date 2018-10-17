package pathFinding;

import java.util.ArrayList;

import geometry.LocatedPoint;
import geometry.Point;
import geometry.Polygon;

class BlockedPathException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}

class Path {
	
	private ArrayList<Step> steps;
	private double length;
	private Point arrival;
	
	
	@Deprecated
	Path(Point origin) {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.arrival = origin;
	}
	
	Path(LocatedPoint origin, Point aim) throws BlockedPathException {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.arrival = (Point) origin;
		
		StepEnd newStep = pathFinding.Step.firstStep(origin, aim, origin.getPolygon());
		this.add(newStep.innerStep);
		while (this.arrival != aim) {
			if (newStep.intersectedEdge.getCross() == Double.POSITIVE_INFINITY) throw new BlockedPathException();
			newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
			this.add(newStep.innerStep);
		}
	}
	
	
	double length() {
		return this.length;
	}

	@Deprecated
	void add(Point p, Polygon area) throws BlockedPathException {
		// on assume que le segment [this.arrival p] est dans le polygon area
		double speed = area.coeffSpeed(p.minus(this.arrival));
		Step newStep = new Step(speed, this.arrival, p);
		this.steps.add(newStep);
		this.arrival = p;
		this.length += newStep.length();
	}

	@Deprecated
	void add(Point p, Polygon area, double startingEdgeCrossTime) throws BlockedPathException {
		// on assume que le segment [this.arrival p] est dans le polygon area
		double speed = area.coeffSpeed(p.minus(this.arrival));
		Step newStep = new Step(speed, startingEdgeCrossTime, this.arrival, p);
		this.steps.add(newStep);
		this.arrival = p;
		this.length += newStep.length();
	}
	
	boolean add(Step newStep) {
		if (this.arrival.equals(newStep.getOrigin())) {
			if (this.steps.add(newStep)) {
				this.length += newStep.length();
				this.arrival = newStep.getEnd();
				return true;
			}
		}
		return false;
	}
	
	boolean merge(Path next) {
		if (this.arrival.equals(next.getPath()[0])) {
			if (this.steps.addAll(next.steps)) {
				this.arrival = next.arrival;
				this.length += next.length;
				return true;
			}
		}
		return false;
	}
	
	Point[] getPath() {
		int n = this.steps.size();
		Point[] path = new Point[n + 1];
		for (int i = 0; i < n; i++) {
			path[i] = this.steps.get(i).getOrigin();
		}
		path[n] = this.arrival;
		return path;
	}
}
