package pathFinding;

import geometry.HalfEdge;
import geometry.LocatedPoint;
import geometry.Point;
import geometry.Polygon;
import geometry.Vector;
import geometry.Vertex;

final class StepEnd {
	final Step innerStep;
	final HalfEdge intersectedEdge;

	StepEnd(Step innerStep, HalfEdge intersectedEdge) {
		this.innerStep = innerStep;
		this.intersectedEdge = intersectedEdge;
	}
}

final class Step {

	private final double speed;
	private final Point origin;
	private final Point end;

	Step(final double speed, final Point a, final Point b) throws BlockedPathException {
		//if (speed <= 0) throw new BlockedPathException("Null speed");
		this.speed = speed > 0 ? speed : 0;
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
		return (this.speed > 0) ? this.vector().length() / this.speed : Double.POSITIVE_INFINITY;
	}

	StepEnd nextStep(final Point aim, final HalfEdge intersectedEdge) throws BlockedPathException {
		HalfEdge toTestEdge = intersectedEdge.getOpposite().getNext();
		Point intersection = null;
		while (!toTestEdge.equals(intersectedEdge.getOpposite())) {
			intersection = toTestEdge.intersection(this.end, aim);

			if (intersection != null) return new StepEnd(
					// l'objectif se trouve en dehors du polygone
					new Step(
							toTestEdge.getPolygon().coeffSpeed(aim.minus(this.end)), 
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
						this.end, 
						aim
						),
				null
				);

	}

	StepEnd nextStep(final LocatedPoint aim, final HalfEdge intersectedEdge) throws PathException {
		StepEnd res = nextStep((Point) aim, intersectedEdge);

		if (res.intersectedEdge == null && !aim.getPolygon().equals(intersectedEdge.getOpposite().getPolygon())) {
			throw new PathException("Arrived in wrong polygon");
		} else {
			return res;
		}

	}

	StepEnd nextStep(final Vertex aim, final HalfEdge intersectedEdge) throws BlockedPathException {
		HalfEdge toTestEdge = intersectedEdge.getOpposite().getNext();
		Point intersection = null;
		try {
			while (!toTestEdge.equals(intersectedEdge.getOpposite())) {

				if (toTestEdge.isEdgeOf(aim)) return new StepEnd(
						new Step(
								toTestEdge.getPolygon().coeffSpeed(aim.minus(this.end)),
								this.end,
								aim
								),
						toTestEdge
						);

				intersection = toTestEdge.intersection(this.end, aim);

				if (intersection != null) return new StepEnd(
						// l'objectif se trouve en dehors du polygone
						new Step(
								toTestEdge.getPolygon().coeffSpeed(aim.minus(this.end)),
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

	static StepEnd firstStep(final Point origin, final Point aim, final Polygon area) throws BlockedPathException {
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

	static StepEnd firstStep(final LocatedPoint origin, final Point aim) throws PathException {
		return firstStep((Point) origin, aim, origin.getPolygon());
	}

	static StepEnd firstStep(final Point origin, final LocatedPoint aim, final Polygon area) throws PathException {
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
			throw new PathException("Arrived in wrong polygon");
		}
	}	

	static StepEnd firstStep(final LocatedPoint origin, final LocatedPoint aim) throws PathException {
		return firstStep((Point) origin, aim, origin.getPolygon());
	}

	static StepEnd firstStep(final Vertex origin, final Vertex aim) throws PathException {

		Point intersection = null;
		for (HalfEdge edge : origin) {
			if (edge.getPolygon().isCrossable())
				for (HalfEdge toTestEdge : edge.getPolygon() ){
					if (!toTestEdge.isEdgeOf(origin)) {
						if (toTestEdge.isEdgeOf(aim)) return new StepEnd(
								new Step(
										edge.getPolygon().coeffSpeed(aim.minus(origin)),
										origin,
										aim
										),
								toTestEdge
								);

						intersection = toTestEdge.intersection(origin, aim);

						if (intersection != null) return new StepEnd(
								// l'objectif se trouve en dehors du polygone mais la ligne droite passe par ce polygone
								new Step(
										edge.getPolygon().coeffSpeed(aim.minus(origin)),
										origin,
										intersection
										),
								toTestEdge
								);

						toTestEdge = toTestEdge.getNext();
					}
				} 
		}

		// l'objectif se trouve à la verticale d'un polygone voisin mais n'en est pas un sommet
		throw new PathException("Vertex inside polygon");
	}

	static StepEnd firstStep(final Vertex origin, final Point aim) throws BlockedPathException {
		Point intersection = null;
		for (HalfEdge edge : origin) {
			for (HalfEdge toTestEdge : edge.getPolygon()) {
				if (!toTestEdge.isEdgeOf(origin)) {

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
			}
		}

		// le polygone de l'objectif a origin comme sommet
		return new StepEnd(
				new Step(
						aim.locate(origin.getEdge().getPolygon().center()).getPolygon().coeffSpeed(aim.minus(origin)),
						origin,
						aim
						),
				null
				);
	}

	static StepEnd firstStep(final LocatedPoint origin, final Vertex aim) throws BlockedPathException {
		HalfEdge toTestEdge = origin.getPolygon().getEdge();
		Point intersection = null;
		do {
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
					// l'objectif se trouve en dehors du polygone
					new Step(
							toTestEdge.getPolygon().coeffSpeed(aim.minus(origin)),
							origin,
							intersection
							),
					toTestEdge
					);

			toTestEdge = toTestEdge.getNext();
		} while (!toTestEdge.equals(origin.getPolygon().getEdge()));

		// l'objectif est à l'intérieur du polygone
		throw new BlockedPathException("Vertex inside polygon");

	}	
}
