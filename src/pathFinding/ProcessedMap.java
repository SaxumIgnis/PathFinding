package pathFinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import geometry.HalfEdge;
import geometry.LocatedPoint;
import geometry.Point;
import geometry.Vertex;

public final class ProcessedMap extends PhysicalMap implements PathFinder {

	enum ProcessAlgo {FLOYD_WARSHALL, DIJKTRA};

	public final int RESEARCH_AREA = 2;
	public final ProcessAlgo algo = ProcessAlgo.DIJKTRA;

	private Path[][] paths;


	private class Chrono {

		private final double startTime = java.lang.System.currentTimeMillis();

		double time() {
			return java.lang.System.currentTimeMillis() - this.startTime;
		}

	}

	public ProcessedMap(Point[] points, int[][] edges) {

		super(points, edges);

		Chrono chrono = new Chrono();
		this.process();

		System.out.println("Temps de calcul : " + chrono.time() + " ms");
		for (Path[] pathList : this.paths)
			for (Path path : pathList)
				if (path.length() < Double.POSITIVE_INFINITY && path.length() > 0)
					System.out.println(path);
		
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
					} catch (PathException e) {
						e.printStackTrace();
					}
				}

	}

	private void processDijkstra() {

		int n = this.vertices.size();


		// copie des chemins directs
		ArrayList<HashMap<Integer, Path>> directPaths = new ArrayList<HashMap<Integer, Path>>(n);
		for (int i = 0; i < n; i++) {
			directPaths.add(i, new HashMap<Integer, Path>());
			for (int j = 0; j < n; j++)
				if (this.paths[i][j].length() < Double.POSITIVE_INFINITY) {
					directPaths.get(i).put(j, this.paths[i][j]);
					//	System.out.println(this.vertices.get(i).tag + " a un chemin vers " + this.vertices.get(j).tag);
				}
		}


		for (int i = 0; i < n; i++) {
			// initialisation
			TreeSet<Integer> heap = new TreeSet<Integer>(new IndexComparator(this.paths[i]));
			for (int j = 0; j < n; j++) 
				if (this.paths[i][j].length() < Double.POSITIVE_INFINITY) heap.add(j);

			while (!heap.isEmpty())
			{
				// recherche du sommet de heap le plus proche du sommet i
				int k = heap.pollFirst();
				
				heap.remove(k);
				for (Map.Entry<Integer,Path> keyVal : directPaths.get(k).entrySet()) {
					int j = keyVal.getKey();
					Path kj = keyVal.getValue();
					try {
						if (this.paths[i][j].length() > this.paths[i][k].addLength(kj)) {
							heap.remove(j);
							this.paths[i][j] = this.paths[i][k].add(kj);
							heap.add(j);
						}
					} catch (NotConsecutivePathException e) {
						System.out.println("Not Consecutive Paths " + this.paths[i][k] + " and " + kj);
					}
				}
			}

		}
	}

	private void process() {

		int n = this.vertices.size();
		this.paths = new Path[n][n];

		this.vertices.sort(new geometry.Point.ComparePoints());

		for (int i = 0; i < n; i++) System.out.println(this.vertices.get(i));

		for (int i = 0; i < n; i ++)
			for (int j = 0; j < n; j++)
				this.paths[i][j] = new Path(this.vertices.get(i), this.vertices.get(j));


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

		// marche depuis origin vers p
		return p.locate(origin);
	}

	private static void researchArea(HashSet<HalfEdge> edgesSet, HalfEdge edge, int remainingResearch) {
		if (remainingResearch > 0 && !edgesSet.contains(edge)) {
			edgesSet.add(edge);
			researchArea(edgesSet, edge.getOpposite(), remainingResearch - 1);
			researchArea(edgesSet, edge.getNext(), remainingResearch);
		}
	}

	private Path shortestWay(LocatedPoint start, LocatedPoint end, int researchArea) {
		Path directPath = new Path(start, end);
		if (directPath.length() < Double.POSITIVE_INFINITY) {
			return directPath;
		} else {
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
					} catch (NotConsecutivePathException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return bestPath;
		}
	}

	@Override
	public Point[] shortestWay(Point a, Point b) {
		return this.shortestWay(this.locatePoint(a), this.locatePoint(b), this.RESEARCH_AREA).getPath();
	}

	private class IndexComparator implements Comparator<Integer> {

		private final Path[] paths;
		
		private IndexComparator(Path[] paths)
		{
			this.paths = paths;
		}
		
		@Override
		public int compare(Integer arg0, Integer arg1) {
			// TODO Auto-generated method stub
			return (int) Math.signum(this.paths[arg0].length() - this.paths[arg1].length());
		}
		
	}
	
}
