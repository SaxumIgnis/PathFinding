package pathFinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Stack;
import java.util.Arrays;
import geometry.BinaryEdge;
import geometry.HalfEdge;
import geometry.Point;
import geometry.Vertex;

public class PhysicalMap {
	
	protected ArrayList<Vertex> vertices;
	
	@Deprecated
	PhysicalMap() {
		this.vertices = new ArrayList<Vertex>();
	}
	
	class EdgeComparator implements Comparator<BinaryEdge> {

		@Override
		public int compare(BinaryEdge arg0, BinaryEdge arg1) {
			return (int) Math.signum(arg0.lengthPlan() - arg1.lengthPlan());
		}
		
	}
	
	private void crossedEdges(BinaryEdge e1, HashSet<BinaryEdge> currentEdges, Stack<BinaryEdge> uncheckedEdges) {
		for (BinaryEdge e2 : currentEdges) {
			Point inter = e1.intersection(e2);
			if (inter != null) {
				Vertex v = inter.toVertex(this.vertices.size());
				currentEdges.remove(e1);
				currentEdges.remove(e2);
				uncheckedEdges.remove(e2);

				this.vertices.add(v);

				currentEdges.add(new BinaryEdge(e1.getOrigin(), v, false));
				currentEdges.add(new BinaryEdge(e2.getOrigin(), v, false));
				currentEdges.add(new BinaryEdge(e1.getEnd(), v, false));
				currentEdges.add(new BinaryEdge(e2.getEnd(), v, false));

				uncheckedEdges.push(new BinaryEdge(e1.getOrigin(), v, false));
				uncheckedEdges.push(new BinaryEdge(e2.getOrigin(), v, false));
				uncheckedEdges.push(new BinaryEdge(e1.getEnd(), v, false));
				uncheckedEdges.push(new BinaryEdge(e2.getEnd(), v, false));
				
				return;

			}

		}
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

		
		// vérification que les aretes forcées ne se croisent pas 
		// sinon ajout de l'intersection
		
		Stack<BinaryEdge> uncheckedEdges = new Stack<BinaryEdge>();
		
		for (int[] edge : edges) {
			BinaryEdge binaryEdge = new BinaryEdge(this.vertices.get(edge[0]), this.vertices.get(edge[1]), false);
			uncheckedEdges.push(binaryEdge);
			chosenEdges.add(binaryEdge);
		}
		

		
		while (!uncheckedEdges.isEmpty()) {
			crossedEdges(uncheckedEdges.pop(), chosenEdges, uncheckedEdges);
		}
		
		for (BinaryEdge edge : chosenEdges) {
			//System.out.println("ajout " + edge + " comme obstacle");
			edge.getOrigin().addEdge(edge.getEnd(), false);
			//System.out.println();
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
			if (!edge.intersectsOne(chosenEdges)) {
				//System.out.println("ajout " + edge);
				chosenEdges.add(edge);
				edge.getOrigin().addEdge(edge.getEnd(), edge.crossable());
				//System.out.println();
			}
		}
		
		//System.out.println();
		
		//for (Vertex v : this.vertices) {
		//	System.out.println("Sommet " + v.tag);
		//	for (HalfEdge e : v) {
		//		System.out.println(e);
		//	}
		//	System.out.println();
		//}
		
		//for (BinaryEdge e : this.getEdges()) System.out.println(e);
		
		// vérification
		for (Vertex vertex : this.vertices) vertex.update();
	}
	
	public Point[] getPoints() {
		return this.vertices.toArray(new Point[this.vertices.size()]);
	}
	
	public BinaryEdge[] getEdges() {
		HashSet<BinaryEdge> edgeSet = new HashSet<BinaryEdge>();
		for (Vertex vertex : this.vertices) 
			for (HalfEdge e : vertex)
				edgeSet.add((BinaryEdge) e);
		
		return edgeSet.toArray(new BinaryEdge[edgeSet.size()]);
	}
	
}
