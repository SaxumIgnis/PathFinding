package pathFinding;

import java.util.ArrayList;
import java.util.HashSet;
import geometry.HalfEdge;
import geometry.LocatedPoint;
import geometry.Point;
import geometry.Vertex;

final class ProcessedMap extends PhysicalMap implements PathFinder {

	@Deprecated
	private ArrayList<LocatedPoint> points;
	public final int RESEARCH_AREA = 2;
	
	private Path[][] paths;
	
	public ProcessedMap(Point[][] polygons, double[] scalarCoeffs) {
		
		super(polygons, scalarCoeffs);
		
		this.process();
	}
	
	@Deprecated
	public ProcessedMap(Point[][] polygons, double[] scalarCoeffs, Point[] singlePoints, int[] singlePointsLocation) {
		super(polygons, scalarCoeffs);
		this.process(singlePoints, singlePointsLocation); 
	}
	
	private void process() {
		
		int n = this.vertices.size();
		this.paths = new Path[n][n];
		
		this.vertices.sort(new geometry.Point.ComparePoints());

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				this.paths[i][j] = new Path();
				// on initialise les distance à Double.POSITIVE_INFINITY
		
		ArrayList<HashSet<Integer>> aNeighbours = new ArrayList<HashSet<Integer>>(n);
		ArrayList<HashSet<Integer>> pNeighbours = new ArrayList<HashSet<Integer>>(n);
		
		
		// Djikstra sans l'inégalité triangulaire
		for (int i = 1; i < n; i++) {
			Vertex vi = this.vertices.get(i);
			for (int j = 0; j < i; j++) {
				Vertex vj = this.vertices.get(j);
				
				// j -> i
				try {
					Path newPath = new Path(vj, vi); // -> BlockedPathException si pas de chemin direct
					
					pNeighbours.get(j).add(i);
					aNeighbours.get(i).add(j);
					
					if (newPath.compareTo(this.paths[j][i]) == -1) {
						
						// k -> j -> i  &&  k -> i
						for (int k : aNeighbours.get(j)) {
							if (aNeighbours.get(i).contains(k)) {
								if (this.paths[k][i].length() < this.paths[k][j].addLength(newPath)) {
									this.paths[k][i] = this.paths[k][j].add(this.paths[j][i]);
								}
							} else {
								pNeighbours.get(k).add(i);
								aNeighbours.get(i).add(k);
								this.paths[k][i] = this.paths[k][j].add(this.paths[j][i]);
							}
								
						}
						
						// j -> i -> k  && j -> k
						for (int k : pNeighbours.get(i)) {
							if (pNeighbours.get(j).contains(k)) {
								if (this.paths[j][k].length() < newPath.addLength(this.paths[i][k])) {
									this.paths[j][k] = this.paths[j][i].add(this.paths[i][k]);
								}
							} else {
								pNeighbours.get(j).add(k);
								aNeighbours.get(k).add(j);
								this.paths[j][k] = this.paths[j][i].add(this.paths[i][k]);
							}
								
						}
					}
				} catch (BlockedPathException e) { }
				
				// i -> j
				try {
					Path newPath = new Path(vi, vj);
					
					pNeighbours.get(i).add(j);
					aNeighbours.get(j).add(i);
					
					if (newPath.compareTo(this.paths[i][j]) == -1) {
						
						// i -> j -> k  &&  i -> k
						for (int k : pNeighbours.get(j)) {
							if (pNeighbours.get(i).contains(k)) {
								if (this.paths[i][k].length() < this.paths[j][k].addLength(newPath)) {
									this.paths[i][k] = this.paths[i][j].add(this.paths[j][k]);
								}
							} else {
								pNeighbours.get(i).add(k);
								aNeighbours.get(k).add(i);
								this.paths[i][k] = this.paths[i][j].add(this.paths[j][k]);
							}
								
						}
						
						// k -> i -> j  && k -> j
						for (int k : aNeighbours.get(i)) {
							if (aNeighbours.get(j).contains(k)) {
								if (this.paths[k][j].length() < this.paths[k][i].addLength(newPath)) {
									this.paths[k][j] = this.paths[k][i].add(this.paths[i][j]);
								}
							} else {
								pNeighbours.get(k).add(j);
								aNeighbours.get(j).add(k);
								this.paths[k][j] = this.paths[k][i].add(this.paths[i][j]);
							}
								
						}
					}
				} catch (BlockedPathException e) { }
			}
		}
	}
	
	@Deprecated
	public void process(Point[] singlePoints, int[] singlePointsLocation) {

		this.points = new ArrayList<LocatedPoint>(singlePoints.length);
		
		for (int i = 0; i < singlePoints.length; i++) {
			this.points.add(singlePoints[i].locate(this.polygons[i]));
		}
		
		this.points.sort(new geometry.Point.ComparePoints());
		this.process();
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
	
	private static void researchArea(HashSet<HalfEdge> edgesSet, HalfEdge edge, int remainingResearch) {
		if (remainingResearch > 0 && !edgesSet.contains(edge)) {
			edgesSet.add(edge);
			researchArea(edgesSet, edge.getOpposite(), remainingResearch - 1);
			researchArea(edgesSet, edge.getNext(), remainingResearch);
		}
	}
	
	private Path shortestWay(LocatedPoint start, LocatedPoint end, int researchArea) {
		try {
			return new Path(start, end);
		} catch (BlockedPathException e) {
			HashSet<HalfEdge> startEdges = new HashSet<HalfEdge>();
			HashSet<HalfEdge> endEdges = new HashSet<HalfEdge>();
			
			researchArea(startEdges, start.getPolygon().getEdge(), researchArea);
			researchArea(endEdges, end.getPolygon().getEdge(), researchArea);
			
			HashSet<Vertex> startVertices = new HashSet<Vertex>();
			HashSet<Vertex> endVertices = new HashSet<Vertex>();
			
			for (HalfEdge startEdge : startEdges) startVertices.add(startEdge.getOrigin());
			for (HalfEdge endEdge : endEdges) endVertices.add(endEdge.getOrigin());
			
			Path bestPath = new Path();
			
			for (Vertex startVertex : startVertices) {
				for (Vertex endVertex : endVertices) {
					try {
						Path newPath = (new Path(start, startVertex)).add(
								this.paths[this.vertices.indexOf(startVertex)][this.vertices.indexOf(endVertex)]).add(
								(new Path(endVertex, end)));
						if (bestPath.length() < newPath.length()) {
							bestPath = newPath;
						}
							
					} catch (BlockedPathException e1) { }
				}
			}
			
			return bestPath;
		}
	}

	@Override
	public Point[] shortestWay(LocatedPoint a, LocatedPoint b) {
		return this.shortestWay(a, b, this.RESEARCH_AREA).getPath();
	}
	
}
