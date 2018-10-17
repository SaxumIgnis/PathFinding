package pathFinding;

import java.util.ArrayList;
import java.util.Comparator;

import geometry.HalfEdge;
import geometry.LocatedPoint;
import geometry.Point;
import geometry.Polygon;
import geometry.Vertex;

final class ProcessedMap extends StructuredMap implements PathFinder {

	private ArrayList<LocatedPoint> points;
	private ArrayList<ArrayList<Path>> paths;
	
	private class SortPointsByX implements Comparator<Point> {

		@Override
		public int compare(Point a, Point b) {
			if (a.getX() > b.getX()) return 1;
			if (a.getX() < b.getX()) return -1;
			if (a.getY() > b.getY()) return 1;
			if (a.getY() < b.getY()) return -1;
			return 0;
		}
		
	}
	
	public ProcessedMap(Point[][] polygons, double[] scalarCoeffs, Point[] singlePoints, int[] singlePointsLocation) {
		this.vertices = new ArrayList<Vertex>(polygons.length);
		Polygon[] polygonsNumber = new Polygon[polygons.length];
		for (int i = 0; i < polygons.length; i++) {
			polygonsNumber[i] = this.addPolygon(polygons[i], scalarCoeffs[i]);
		}
		
		this.vertices.sort(new SortPointsByX());
		
		this.points = new ArrayList<LocatedPoint>(singlePoints.length);
		this.paths = new ArrayList<ArrayList<Path>>(singlePoints.length);
		for (int i = 0; i < singlePoints.length; i++) {
			this.points.add(singlePoints[i].locate(polygonsNumber[i]));
		}
		
		this.points.sort(new SortPointsByX());
	}
	
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
		LocatedPoint left = this.halfClosestPoint(p, d, -1);
		LocatedPoint right = this.halfClosestPoint(p, e, 1);
		
		if (p.distance(left) < p.distance(right)) {
			return left;
		} else {
			return right;
		}
		
	}
	
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
	public Path shorterWay(Point a, Point b) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// TODO
	
}
