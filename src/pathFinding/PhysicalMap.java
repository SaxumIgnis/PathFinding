package pathFinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Arrays;

import geometry.BinaryEdge;
import geometry.HalfEdge;
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
			return (int) Math.signum(arg0.lengthPlan() - arg1.lengthPlan());
		}
		
	}
	
	private static boolean intersects(BinaryEdge edge, HashSet<BinaryEdge> chosenEdges) {
		// retourne true si edge croise une des arêtes de chosenEdges
		for (BinaryEdge chosenEdge : chosenEdges) {
			if (!edge.getOrigin().equals(chosenEdge.getOrigin()) &&
					!edge.getOrigin().equals(chosenEdge.getEnd()) &&
					!edge.getEnd().equals(chosenEdge.getOrigin()) &&
					!edge.getEnd().equals(chosenEdge.getEnd()) &&
					chosenEdge.intersection(edge) != null) {
				return true;
			}
		}
		return false;
	}
	
	public PhysicalMap(Point[] points, int[][] edges) {
		
		HashSet<BinaryEdge> unAddedEdges = new HashSet<BinaryEdge>();
		HashSet<BinaryEdge> chosenEdges = new HashSet<BinaryEdge>();
		this.vertices = new ArrayList<Vertex>(points.length);

		for (int i = 0; i < points.length; i++) {
			this.vertices.add(points[i].toVertex());
		}

		//System.out.println("nombre de sommets : " + this.vertices.size());
		// triangularisation de Delaunay avec des arêtes forcées
		
		for (int[] edge : edges) {
			for (int p : edge) System.out.print(" - " + points[p].tag);
			System.out.println();
			BinaryEdge binaryEdge = new BinaryEdge(points[edge[0]].toVertex(), points[edge[1]].toVertex(), false);
			System.out.println("ajout " + binaryEdge + " comme obstacle");
			chosenEdges.add(binaryEdge);
			binaryEdge.getOrigin().addEdge(binaryEdge.getEnd(), false);
		}

		
		for (Vertex v : this.vertices) {
			for (Vertex w : this.vertices) {
				BinaryEdge edge = new BinaryEdge(v, w, true);
				if (!v.equals(w) && !chosenEdges.contains(edge)) {
					unAddedEdges.add(edge);
				}
			}
		}
		
		BinaryEdge[] edgesArray = unAddedEdges.toArray(new BinaryEdge[unAddedEdges.size()]);
		

		//for (BinaryEdge e : edgesArray)
		//	System.out.println("arete "+e.getOrigin().tag+"-"+e.getEnd().tag+" : "+e.lengthPlan());
		//System.out.println();
		
		Arrays.sort(edgesArray, new EdgeComparator());
		
		//for (BinaryEdge e : edgesArray)
		//	System.out.println("arete "+e.getOrigin().tag+"-"+e.getEnd().tag+" : "+e.lengthPlan());
		
		for (BinaryEdge edge : edgesArray) {
			if (!intersects(edge, chosenEdges)) {
				System.out.println("ajout " + edge);
				chosenEdges.add(edge);
				edge.getOrigin().addEdge(edge.getEnd(), edge.getCross());
			}
		}
		
		// vérification
		//for (Vertex vertex : this.vertices) vertex.update();
	}
	
	public Point[] getPoints() {
		return this.vertices.toArray(new Point[this.vertices.size()]);
	}
	
	public BinaryEdge[] getEdges() {
		HashSet<BinaryEdge> edgeSet = new HashSet<BinaryEdge>();
		for (Vertex vertex : this.vertices) {
			HalfEdge e = vertex.getEdge();
			do {
				edgeSet.add((BinaryEdge) e);
				e = e.getOpposite().getNext();
			} while (!e.equals(vertex.getEdge()));
		}
		return edgeSet.toArray(new BinaryEdge[edgeSet.size()]);
	}
	
}
