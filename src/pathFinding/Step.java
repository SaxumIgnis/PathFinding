package pathFinding;

import geometry.HalfEdge;
import geometry.LocatedPoint;
import geometry.Point;
import geometry.Polygon;
import geometry.Vector;
import geometry.Vertex;

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
		if (speed <= 0) throw new BlockedPathException("Null speed");
		this.speed = speed;
		this.startTime = 0;
		this.origin = a;
		this.end = b;
	}
	
	Step(double speed, double startTime, Point a, Point b) throws BlockedPathException {
		if (speed <= 0) throw new BlockedPathException("Null speed");
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
	
	@Deprecated
	StepEnd nextStep(Point aim, HalfEdge intersectedEdge) throws BlockedPathException {
		HalfEdge toTestEdge = intersectedEdge.getOpposite().getNext();
		Point intersection = null;
		try {
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
		} catch (java.lang.NullPointerException e) {
			// normalement c'est inutile car on ne doit pas pouvoir sortir de la map physique mais on ne sait jamais
			throw new BlockedPathException("Going out of the map");
		}
		
	}
	
	StepEnd nextStep(LocatedPoint aim, HalfEdge intersectedEdge) throws BlockedPathException {
		HalfEdge toTestEdge = intersectedEdge.getOpposite().getNext();
		Point intersection = null;
		try {
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
			if (aim.getPolygon().equals(toTestEdge.getPolygon())) {
				return new StepEnd(
						new Step(
								toTestEdge.getPolygon().coeffSpeed(aim.minus(this.end)), 
								intersectedEdge.getCross(), 
								this.end, 
								aim
								),
						null
						);
			} else {
				throw new BlockedPathException("Arrived in wrong polygon");
			}
			
		} catch (java.lang.NullPointerException e) {
			// normalement c'est inutile car on ne doit pas pouvoir sortir de la map physique mais on ne sait jamais
			throw new BlockedPathException("Going out of the map");
		}
		
	}

	StepEnd nextStep(Vertex aim, HalfEdge intersectedEdge) throws BlockedPathException {
		HalfEdge toTestEdge = intersectedEdge.getOpposite().getNext();
		Point intersection = null;
		try {
			while (!toTestEdge.equals(intersectedEdge.getOpposite())) {
				
				if (toTestEdge.isEdgeOf(aim)) return new StepEnd(
						new Step(
								toTestEdge.getPolygon().coeffSpeed(aim.minus(origin)),
								origin,
								aim
								),
						toTestEdge
						);
				
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
			throw new BlockedPathException("Vertex inside polygon");
			
		} catch (java.lang.NullPointerException e) {
			// normalement c'est inutile car on ne doit pas pouvoir sortir de la map physique mais on ne sait jamais
			throw new BlockedPathException("Going out of the map");
		}
		
	}
	
	@Deprecated
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
	
	@Deprecated
	static StepEnd firstStep(LocatedPoint origin, Point aim) throws BlockedPathException {
		return firstStep((Point) origin, aim, origin.getPolygon());
	}
	
	static StepEnd firstStep(Point origin, LocatedPoint aim, Polygon area) throws BlockedPathException {
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
		if (aim.getPolygon().equals(toTestEdge.getPolygon())) {
			return new StepEnd(
					new Step(
							aim.getPolygon().coeffSpeed(aim.minus(origin)),
							origin,
							aim
							),
					null
					);
		} else {
			throw new BlockedPathException("Arrived in wrong polygon");
		}
	}	
	
	static StepEnd firstStep(LocatedPoint origin, LocatedPoint aim) throws BlockedPathException {
		return firstStep((Point) origin, aim, origin.getPolygon());
	}
	
	static StepEnd firstStep(Vertex origin, Vertex aim) throws BlockedPathException {
		HalfEdge firstPolygonEdge = origin.getEdge();
		Point intersection = null;
		do {
			HalfEdge toTestEdge = firstPolygonEdge;
			do {
				if (!toTestEdge.isEdgeOf(origin)) {
					if (toTestEdge.isEdgeOf(aim)) return new StepEnd(
							new Step(
									toTestEdge.getPolygon().coeffSpeed(aim.minus(origin)),
									origin,
									aim
									),
							toTestEdge
							);

					intersection = toTestEdge.intersection(origin, aim);

					if (intersection != null) return new StepEnd(
							// l'objectif se trouve en dehors du polygone mais la ligne droite passe par ce polygone
							new Step(
									toTestEdge.getPolygon().coeffSpeed(aim.minus(origin)),
									origin,
									intersection
									),
							toTestEdge
							);

					toTestEdge = toTestEdge.getNext();
				}
			} while (!toTestEdge.equals(firstPolygonEdge));
			
			firstPolygonEdge = firstPolygonEdge.getOpposite().getNext();
		} while (firstPolygonEdge.equals(origin.getEdge()));
		
		// l'objectif se trouve à la verticale d'un polygone voisin mais n'en est pas un sommet
		throw new BlockedPathException("Vertex inside polygon");
	}
		

}
