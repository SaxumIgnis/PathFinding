package pathFinding;

import java.util.ArrayList;

import geometry.LocatedPoint;
import geometry.Point;
import geometry.Polygon;
import geometry.Vertex;

class BlockedPathException extends Exception{

	public BlockedPathException(String string) {
		super(string);
	}

	public BlockedPathException() {
		super();
	}

	private static final long serialVersionUID = 1L;
	
}

class Path {
	
	/** 
	 * classe pour un itinéraire entre deux points
	 * 
	 * l'initialisation requiert
	 * - un LocadetPoint comme point de départ (besoin de connaitre le polygone de départ)
	 * - un LocatedPoint comme point d'arrivée (par sécurité : on s'assure que le polygone d'arrivée est celui voulu)
	 * 
	 * renvoie un chemin rectiligne (vu de dessus)
	 * retourne une BlockedPathException si la ligne direct n'est pas possible
	 * 
	 * AB.merge(BC) -> AC
	 */
	
	private ArrayList<Step> steps;
	private double length;
	private Point arrival;
	
	@Deprecated
	private Path(Point origin) {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.arrival = origin;
	}

	double length() {
		return this.length;
	}
	
	Path(LocatedPoint origin, LocatedPoint aim) throws BlockedPathException {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.arrival = (Point) origin;
		
		StepEnd newStep = pathFinding.Step.firstStep(origin, aim);
		this.add(newStep.innerStep);
		while (this.arrival != aim) {
			if (newStep.intersectedEdge.getCross() == Double.POSITIVE_INFINITY) throw new BlockedPathException("Incrossable edge");
			newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
			this.add(newStep.innerStep);
		}
		
		//if (aim.getPolygon() != newStep.intersectedEdge.getPolygon()) throw new BlockedPathException("Arrived in wrong polygon");
	}
	
	Path(Vertex origin, Vertex aim) throws BlockedPathException {
		if (origin.equals(aim)) {
			this.arrival = aim;
			this.length = 0;
			this.steps = new ArrayList<Step>();
		} else {
			if (origin.isNeighbour(aim)) {
				this.arrival = aim;
				double speed = origin.distanceToNeighbour(aim);
				this.steps = new ArrayList<Step>(1);
				this.add(new Step(speed, origin, aim));
			} else {
				StepEnd newStep = pathFinding.Step.firstStep(origin, aim);
				this.add(newStep.innerStep);
				while (this.arrival != aim) {
					if (newStep.intersectedEdge.getCross() == Double.POSITIVE_INFINITY) throw new BlockedPathException("Incrossable edge");
					newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
					this.add(newStep.innerStep);
				}
			}
		}
	}

	@Deprecated
	private void add(Point p, Polygon area) throws BlockedPathException {
		// on assume que le segment [this.arrival p] est dans le polygon area
		double speed = area.coeffSpeed(p.minus(this.arrival));
		Step newStep = new Step(speed, this.arrival, p);
		this.steps.add(newStep);
		this.arrival = p;
		this.length += newStep.length();
	}

	@Deprecated
	private void add(Point p, Polygon area, double startingEdgeCrossTime) throws BlockedPathException {
		// on assume que le segment [this.arrival p] est dans le polygon area
		double speed = area.coeffSpeed(p.minus(this.arrival));
		Step newStep = new Step(speed, startingEdgeCrossTime, this.arrival, p);
		this.steps.add(newStep);
		this.arrival = p;
		this.length += newStep.length();
	}
	
	private boolean add(Step newStep) {
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
