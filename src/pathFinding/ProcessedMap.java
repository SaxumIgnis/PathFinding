package pathFinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import geometry.HalfEdge;
import geometry.LocatedPoint;
import geometry.Point;
import geometry.Vertex;

public final class ProcessedMap extends PhysicalMap implements PathFinder {

	enum ProcessAlgo {FLOYD_WARSHALL, DIJKTRA};
	
	public final int RESEARCH_AREA = 2;
	public ProcessAlgo algo = ProcessAlgo.DIJKTRA;
	
	private Path[][] paths;
	
	public ProcessedMap(Point[] points, int[][] edges) {
		
		super(points, edges);
		
		this.process();
	}
	
	private void processFloydWarshall() {
		
		int n = this.vertices.size();		
		
		for (int k = 0; k < n; k++)
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++) {
					try {
						if (this.paths[i][j].length() < this.paths[i][k].addLength(this.paths[k][j])) {
							this.paths[i][j] = this.paths[i][k].add(this.paths[k][j]);
						}
					} catch (BlockedPathException e) {
						e.printStackTrace();
					}
		}
		
	}
	
	private void processDijkstra() {
		
		int n = this.vertices.size();
		
		// copie des chemins directs
		ArrayList<HashMap<Integer, Path>> directPaths = new ArrayList<HashMap<Integer, Path>>(n);
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if (this.paths[i][j].length() < Double.POSITIVE_INFINITY)
					directPaths.get(i).put(j, this.paths[i][j]);
		
		
		for (int i = 0; i < n; i++) {
			// initialisation
			HashSet<Integer> heap = new HashSet<Integer>(n);
			for (int j = 0; j < n; j++) heap.add(j);
			
			while (!heap.isEmpty()) {
				// recherche du sommet de heap le plus proche d'index
				int k = -1;
				double minDist = Double.POSITIVE_INFINITY;
				for (int j : heap) {
					if (paths[i][j].length() < minDist) {
						k = i;
						minDist = paths[i][j].length();
					}
				}
				
				if (k == -1) {
					heap.clear();
				} else {
				
					heap.remove(k);
					
					for (Map.Entry<Integer,Path> keyVal : directPaths.get(k).entrySet()) {
						int j = keyVal.getKey();
						Path kj = keyVal.getValue();
						try {
							if (this.paths[i][j].length() > this.paths[i][k].addLength(kj)) {
								this.paths[i][j] = this.paths[i][k].add(kj);
								heap.add(j);
							}
						} catch (BlockedPathException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
			
		}
	}

	private void process() {
		
		int n = this.vertices.size();
		this.paths = new Path[n][n];

		this.vertices.sort(new geometry.Point.ComparePoints());

		for (int i = 0; i < n; i ++)
			for (int j = 0; j < n; j++)
				try {
					this.paths[i][j] = new Path(this.vertices.get(i), this.vertices.get(j));
				} catch (BlockedPathException e) {
					this.paths[i][j] = new Path(); // longueur infinie
				}
		
		if (this.algo == ProcessAlgo.FLOYD_WARSHALL) this.processFloydWarshall();
		if (this.algo == ProcessAlgo.DIJKTRA) this.processDijkstra();
	}
	
	private LocatedPoint randomLocatedPoint(Random generator) {
		return this.vertices.get(generator.nextInt(this.vertices.size())).getEdge().getPolygon().center();
	}
	
	private LocatedPoint locatePoint(Point p) {
		// algo de marche aléatoire
		
		// on cherche un point connu "pas trop loin"
		double dist = Double.POSITIVE_INFINITY;
		LocatedPoint origin = null;
		Random generator = new Random();
		for (int i = 0; i < Math.sqrt(this.vertices.size()); i++) {
			LocatedPoint newRandom = this.randomLocatedPoint(generator);
			if (newRandom.distance(p) < dist) {
				dist = newRandom.distance(p);
				origin = newRandom;
			}
		}
		
		// marche vers le point recherché
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
	public Point[] shortestWay(Point a, Point b) {
		return this.shortestWay(this.locatePoint(a), this.locatePoint(b), this.RESEARCH_AREA).getPath();
	}
	
}
