package pathFinding;

import java.util.ArrayList;

import geometry.LocatedPoint;
import geometry.Point;
import geometry.Vertex;


class PathException extends Exception {

	public PathException(String string) {
		super(string);
	}

	public PathException() {
		super();
	}

	private static final long serialVersionUID = 1L;
	
}

class BlockedPathException extends PathException {

	public BlockedPathException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;
	
}

class NotConsecutivePathException extends PathException {
	
	private static final long serialVersionUID = 1L;
	
}

final class Path implements Comparable<Path>{
	
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
	private final Point origin;
	private Point arrival;
	
	Path() {
		this.steps = null;
		this.length = Double.POSITIVE_INFINITY;
		this.origin = null;
		this.arrival = null;
	}
	
	Path(Point origin, boolean blocked) {
		this.steps = new ArrayList<Step>();
		this.length = blocked ? Double.POSITIVE_INFINITY : 0;
		this.origin = origin;
		this.arrival = origin;
	}

	double length() {
		return this.length;
	}
	
	Path(LocatedPoint origin, LocatedPoint aim) throws PathException {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.origin = origin;
		this.arrival = (Point) origin;
		
		StepEnd newStep = pathFinding.Step.firstStep(origin, aim);
		this.add(newStep.innerStep);
		while (this.arrival != aim) {
			if (!newStep.intersectedEdge.crossable()) throw new BlockedPathException("Incrossable edge");
			
			newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
			this.add(newStep.innerStep);
		}
		
		//if (aim.getPolygon() != newStep.intersectedEdge.getPolygon()) throw new BlockedPathException("Arrived in wrong polygon");
	}
	
	Path(Vertex origin, Vertex aim) throws PathException {
		this.origin = origin;
		this.arrival = origin;
		if (origin.equals(aim)) {
			this.length = 0;
			this.steps = new ArrayList<Step>(0);
		}
		else
		{
			// TODO : utiliser les AccessAngle
			System.out.print ("création chemin " + origin.tag + " vers " + aim.tag + " : ");
			if (origin.isNeighbour(aim)) {
				double speed = origin.speedToNeighbour(aim);
				this.steps = new ArrayList<Step>(1);
				this.add(new Step(speed, origin, aim));
				System.out.println("sommets voisins, longueur " + this.length);
			}
			else
			{
				this.steps = new ArrayList<Step>();
				StepEnd newStep = pathFinding.Step.firstStep(origin, aim);
				this.add(newStep.innerStep);
				//System.out.println("Ajout chemin de " + newStep.innerStep.getOrigin() + " vers " + newStep.innerStep.getEnd() + "de longueur " + newStep.innerStep.length() + ", nouvelle longueur " + this.length);
				
				
				while (!this.arrival.equals(aim))
				{

					//System.out.println("point intermédiaire : " + this.arrival);
					if (!newStep.intersectedEdge.crossable())
					{
						System.out.println("chemin bloqué par " + newStep.intersectedEdge);
						throw new BlockedPathException("Incrossable edge");
					}
					newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
					if (!this.add(newStep.innerStep))
					{
						System.out.println("Echec d'ajout du nouveau chemin : " + newStep.innerStep.getOrigin() + " != "+ this.arrival);
						throw new BlockedPathException("Not Consectutive Paths");
					}
				}
				System.out.println((this.steps.size() - 1) + " sommets intermédiaires, longueur " + this.length);
			}
		}
	}
	
	Path(LocatedPoint origin, Vertex aim) throws BlockedPathException {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.origin = origin;
		this.arrival = (Point) origin;
		
		StepEnd newStep = pathFinding.Step.firstStep(origin, aim);
		this.add(newStep.innerStep);
		while (this.arrival != aim) {
			if (!newStep.intersectedEdge.crossable()) throw new BlockedPathException("Incrossable edge");
			newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
			this.add(newStep.innerStep);
		}
	}
	
	Path(Vertex origin, LocatedPoint aim) throws BlockedPathException {
		this.steps = new ArrayList<Step>();
		this.length = 0;
		this.origin = origin;
		this.arrival = (Point) origin;
		
		StepEnd newStep = pathFinding.Step.firstStep(origin, aim);
		this.add(newStep.innerStep);
		while (this.arrival != aim) {
			if (!newStep.intersectedEdge.crossable()) throw new BlockedPathException("Incrossable edge");
			newStep = newStep.innerStep.nextStep(aim, newStep.intersectedEdge);
			this.add(newStep.innerStep);
		}
	}
	
	private boolean add(Step newStep) {
		if (this.arrival.equals(newStep.getOrigin())) {
			this.steps.add(newStep);
			this.length += newStep.length();
			this.arrival = newStep.getEnd();
			return true;
		}
		return false;
	}
	
	void merge(Path pathToAdd) throws BlockedPathException {
		if (!this.arrival.equals(pathToAdd.origin)) throw new BlockedPathException("Not consecutive paths");

		if (this.steps.addAll(pathToAdd.steps)) {
			this.arrival = pathToAdd.arrival;
			this.length += pathToAdd.length;
		}
	}
	
	Path add(Path pathToAdd) throws BlockedPathException {
		if (!this.arrival.equals(pathToAdd.steps.get(0).getOrigin())) throw new BlockedPathException("Not consecutive paths");
		
		Path newPath = new Path(this.origin, false);
		newPath.steps.addAll(this.steps);
		newPath.steps.addAll(pathToAdd.steps);
		
		newPath.length = this.addLength(pathToAdd);
		newPath.arrival = pathToAdd.arrival;
		return newPath;
	}
	
	double addLength(Path pathToAdd) throws BlockedPathException {
		if (this.arrival.equals(pathToAdd.origin)) {
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
	
	@Override
	public String toString() {
		if (this.length < Double.POSITIVE_INFINITY) {
			return "chemin de " + this.origin.tag + " vers " + this.arrival.tag + " : longueur " + this.length;
		} else {
			return "chemin impossible";
		}
	}
}
