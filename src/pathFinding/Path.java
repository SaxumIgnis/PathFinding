package pathFinding;

import java.util.ArrayList;

class Path {
	
	private ArrayList<Step> steps;
	private double length;
	private Point arrival;
	
	Path(Point origin) {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.arrival = origin;
	}
	
	double length() {
		return this.length;
	}
	
	void add(Point p, Polygon area) {
		// on assume que le segment [this.arrival p] est dans le polygon area
		double speed = area.coeff_speed(p.minus(this.arrival));
		Step newStep = new Step(speed, this.arrival, p);
		this.steps.add(newStep);
		this.arrival = p;
		this.length += newStep.length();
	}
	
	Point[] get_path() {
		int n = this.steps.size();
		Point[] path = new Point[n + 1];
		for (int i = 0; i < n; i++) {
			path[i] = this.steps.get(i).get_origin();
		}
		path[n] = this.arrival;
		return path;
	}
}
