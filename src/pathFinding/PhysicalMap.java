package pathFinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Arrays;

import geometry.BinaryEdge;
import geometry.Point;
import geometry.Vertex;

public class PhysicalMap {
	
	protected ArrayList<Vertex> vertices;
	
	PhysicalMap() {
		this.vertices = new ArrayList<Vertex>();
	}
	
	class EdgeComparator implements Comparator<BinaryEdge> {

		@Override
		public int compare(BinaryEdge arg0, BinaryEdge arg1) {
			return (int) Math.signum(arg0.length() - arg0.length());
		}
		
	}
	
	private static boolean intersects(BinaryEdge edge, HashSet<BinaryEdge> chosenEdges) {
		// retourne true si edge croise une des arêtes de chosenEdges
		for (BinaryEdge chosenEdge : chosenEdges) {
			if (chosenEdge.intersection(edge) != null) {
				return true;
			}
		}
		return false;
	}
	
	PhysicalMap(Point[] points, Point[][] edges) {
		
		HashSet<BinaryEdge> unAddedEdges = new HashSet<BinaryEdge>();
		HashSet<BinaryEdge> chosenEdges = new HashSet<BinaryEdge>();
		HashSet<Vertex> tempVertices = new HashSet<Vertex>();

		for (Point point : points) {
			tempVertices.add((Vertex) point);
		}
		
		// triangularisation de Delaunay avec des arêtes forcées
		
		for (Point[] edge : edges) {
			BinaryEdge binaryEdge = new BinaryEdge((Vertex) edge[0], (Vertex) edge[1], false);
			chosenEdges.add(binaryEdge);
			tempVertices.add(binaryEdge.getEnd());
			tempVertices.add(binaryEdge.getOrigin());
		}

		this.vertices = new ArrayList<Vertex>(tempVertices);
		
		for (Vertex v : this.vertices) {
			for (Vertex w : this.vertices) {
				BinaryEdge edge = new BinaryEdge(v, w, true);
				if (!chosenEdges.contains(edge)) {
					unAddedEdges.add(edge);
				}
			}
		}
		
		BinaryEdge[] edgesArray = (BinaryEdge[]) unAddedEdges.toArray();
		Arrays.parallelSort(edgesArray, new EdgeComparator());
		
		for (BinaryEdge edge : edgesArray) {
			if (!intersects(edge, chosenEdges)) {
				chosenEdges.add(edge);
				edge.getOrigin().addEdge(edge.getEnd(), edge.getCross());
			}
		}
		
		// vérification
		for (Vertex vertex : this.vertices) vertex.update();
	}
	
	
	
}
