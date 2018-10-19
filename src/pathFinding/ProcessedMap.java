package pathFinding;

import java.util.ArrayList;
import java.util.Comparator;

import geometry.HalfEdge;
import geometry.LocatedPoint;
import geometry.Point;
import geometry.Polygon;
import geometry.Vector;
import geometry.Vertex;

final class ProcessedMap extends PhysicalMap implements PathFinder {

	@Deprecated
	private ArrayList<LocatedPoint> points;
	
	private ArrayList<ArrayList<Path>> paths;
	
	public ProcessedMap(Point[][] polygons, double[] scalarCoeffs) {
		super(polygons, scalarCoeffs);
		this.points = null;
		this.paths = null;
	}
	
	public ProcessedMap(Point[][] polygons, double[] scalarCoeffs, Point[] singlePoints, int[] singlePointsLocation) {
		super(polygons, scalarCoeffs);
		this.process(singlePoints, singlePointsLocation); 
	}
	
	public void process(Point[] singlePoints, int[] singlePointsLocation) {
		
		this.vertices.sort(new geometry.Point.ComparePoints());
		
		this.points = new ArrayList<LocatedPoint>(singlePoints.length);
		this.paths = new ArrayList<ArrayList<Path>>(singlePoints.length);
		for (int i = 0; i < singlePoints.length; i++) {
			this.points.add(singlePoints[i].locate(this.polygons[i]));
		}
		
		this.points.sort(new geometry.Point.ComparePoints());
		
		for (Vertex v1 : this.vertices) {
			for (Vertex v2 : this.vertices) {
				if (v1 != v2) {
					if (v1.isNeighbour(v2)) {
						this.paths()v1.distanceToAccessibleNeighbour(v2)) {
							
						}
					}
				}
			}
		}
		
		/*
		 * TODO
		 * 
		 * - Matrice des path(vertex -> vertex)
		 * - Djiktra
		 */
	}
	
	@Deprecated
	private LocatedPoint halfClosestPoint(Point p, int index, int j) {
		LocatedPoint closestPoint = this.points.get(index);
		while (index > 0 && index < this.points.size() - 1) {
			index += j;
			if (Math.abs(p.getX() - this.points.get(index).getX()) > p.distance(closestPoint)) {
				return closestPoint;
			}
			if (p.distance(this.vertices.get(index)) < p.distance(closestPoint)) {
				closestPoint = this.points.get(index);
			}
		}
		return closestPoint;
	}

	@Deprecated
	private LocatedPoint halfClosestPoint(LocatedPoint p, int index, int j) {
		/**
		 * ne prend en compte que les points du même polygone
		 */
		LocatedPoint closestPoint = null;
		index -= j;
		while (index > 0 && index < this.points.size() - 1) {
			index += j;
			if (p.getPolygon().equals(this.points.get(index).getPolygon())) {
				if (closestPoint == null) {
					closestPoint = this.points.get(index);
				} else {
					if (Math.abs(p.getX() - this.points.get(index).getX()) > p.distance(closestPoint)) {
						return closestPoint;
					}
					if (p.distance(this.vertices.get(index)) < p.distance(closestPoint)) {
						closestPoint = this.points.get(index);
					}
				}
			}
		}
		return closestPoint;
	}

	@Deprecated
	private LocatedPoint closestPoint(Point p) {
		// 1ere étape : le placer en fonction de l'abscisse (x)
		
		int d = 0;
		int e = this.points.size();
		while (e - d > 1) {
			if (p.compareTo(this.points.get((d + e) / 2)) > 0) {
				d = (d + e) / 2;
			} else {
				e = (d + e) / 2;
			}
		}
		
		// 2e étape : recherche du sommet le plus proche à gauche puis à doite
		LocatedPoint leftClosestPoint = this.halfClosestPoint(p, d, -1);
		LocatedPoint rightClosestPoint = this.halfClosestPoint(p, e, 1);
		
		if (p.distance(leftClosestPoint) < p.distance(rightClosestPoint)) {
			return leftClosestPoint;
		} else {
			return rightClosestPoint;
		}
		
	}

	@Deprecated
	private LocatedPoint closestPoint(LocatedPoint p) {
		// 1ere étape : le placer en fonction de l'abscisse (x)
		
		int d = 0;
		int e = this.points.size();
		while (e - d > 1) {
			if (p.compareTo(this.points.get((d + e) / 2)) > 0) {
				d = (d + e) / 2;
			} else {
				e = (d + e) / 2;
			}
		}
		
		// 2e étape : recherche du sommet le plus proche à gauche puis à doite
		LocatedPoint leftClosestPoint = this.halfClosestPoint(p, d, -1);
		LocatedPoint rightClosestPoint = this.halfClosestPoint(p, e, 1);
		
		if (p.distance(leftClosestPoint) < p.distance(rightClosestPoint)) {
			return leftClosestPoint;
		} else {
			return rightClosestPoint;
		}
		
	}

	@Deprecated
	private LocatedPoint locatePoint(Point p) {
		LocatedPoint origin = this.closestPoint(p);
		HalfEdge startingEdge = origin.getPolygon().getEdge();
		HalfEdge nextEdge = startingEdge;
		do {
			if (nextEdge.intersection(origin, p) != null) {
				startingEdge = nextEdge.getOpposite();
				nextEdge = startingEdge.getNext();
			} else {
				nextEdge = nextEdge.getNext();
			}
		} while (nextEdge != startingEdge);
		return p.locate(startingEdge.getPolygon());
	}
	
	@Override
	@Deprecated
	public Path shorterWay(Point start, Point end) {
		try {
			return new Path(this.locatePoint(start), this.locatePoint(end));
		} catch (BlockedPathException e) {
			return this.paths.get(this.points.indexOf(this.closestPoint(start))).get(this.points.indexOf(this.closestPoint(end)));
		}
	}
	
	public Path shorterWay(LocatedPoint start, LocatedPoint end) {
		try {
			return new Path(start, end);
		} catch (BlockedPathException e) {
			/*
			 * TODO
			 * 
			 * get min (startVertex : start.polygon) (endVertex : end.polygon)
			 * 		|start -> startVertex| + |startVertex -> endVertex| + |endVertex -> end|
			 * 
			 * return (start -> startVertex --> endVertex -> end)
			 */
			
			return this.paths.get(this.points.indexOf(this.closestPoint(start))).get(this.points.indexOf(this.closestPoint(end)));
		}
	}
	
}
