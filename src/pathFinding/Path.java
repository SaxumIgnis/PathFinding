package pathFinding;

import java.util.ArrayList;

import geometry.LocatedPoint;
import geometry.Point;
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

class Path implements Comparable<Path>{
	
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
	
	private Path(Point origin) {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.arrival = origin;
	}
	
	Path() {
		this.steps = null;
		this.length = Double.POSITIVE_INFINITY;
		this.arrival = null;
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
			if (!newStep.intersectedEdge.getCross()) throw new BlockedPathException("Incrossable edge");
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
					if (!newStep.intersectedEdge.getCross()) throw new BlockedPathException("Incrossable edge");
					newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
					this.add(newStep.innerStep);
				}
			}
		}
	}
	
	Path(LocatedPoint origin, Vertex aim) throws BlockedPathException {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.arrival = (Point) origin;
		
		StepEnd newStep = pathFinding.Step.firstStep(origin, aim);
		this.add(newStep.innerStep);
		while (this.arrival != aim) {
			if (!newStep.intersectedEdge.getCross()) throw new BlockedPathException("Incrossable edge");
			newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
			this.add(newStep.innerStep);
		}
	}
	
	Path(Vertex origin, LocatedPoint aim) throws BlockedPathException {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.arrival = (Point) origin;
		
		StepEnd newStep = pathFinding.Step.firstStep(origin, aim);
		this.add(newStep.innerStep);
		while (this.arrival != aim) {
			if (!newStep.intersectedEdge.getCross()) throw new BlockedPathException("Incrossable edge");
			newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
			this.add(newStep.innerStep);
		}
	}
	
	private Point start() {
		return this.steps.get(0).getOrigin();
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
	
	void merge(Path pathToAdd) throws BlockedPathException {
		if (!this.arrival.equals(pathToAdd.start())) throw new BlockedPathException("Not consecutive paths");

		if (this.steps.addAll(pathToAdd.steps)) {
			this.arrival = pathToAdd.arrival;
			this.length += pathToAdd.length;
		}
	}
	
	Path add(Path pathToAdd) throws BlockedPathException {
		if (!this.arrival.equals(pathToAdd.steps.get(0).getOrigin())) throw new BlockedPathException("Not consecutive paths");
		
		Path newPath = new Path(pathToAdd.arrival);
		newPath.steps.addAll(this.steps);
		newPath.steps.addAll(pathToAdd.steps);
		
		newPath.length = this.addLength(pathToAdd);
		return newPath;
	}
	
	double addLength(Path pathToAdd) throws BlockedPathException {
		if (this.arrival.equals(pathToAdd.steps.get(0).getOrigin())) {
			return this.length + pathToAdd.length;
		}
		throw new BlockedPathException("Not consecutive paths");
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

	@Override
	public int compareTo(Path path) {
		if (path == null) return -1;
		return (int) Math.signum(this.length - path.length);
	}
}
