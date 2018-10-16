package pathFinding;

import java.util.ArrayList;
import java.util.Arrays;

import geometry.HalfEdge;
import geometry.Point;
import geometry.Polygon;
import geometry.Vertex;

public class StructuredMap {
	
	ArrayList<Point> points;
	ArrayList<Vertex> vertices;
	
	public StructuredMap() {
		this.points = new ArrayList<Point>();
		this.vertices = new ArrayList<Vertex>();
	}
	
	public StructuredMap(Point[] points) {
		this.points = new ArrayList<Point>(Arrays.asList(points));
		this.vertices = new ArrayList<Vertex>();
	}
	
	public void addSinglePoint(Point p) {
		this.points.add(p);
	}
	
	private HalfEdge addEdge(Vertex a, Vertex b) {
		// retourne l'arrête interne
		if (!this.vertices.contains(a)) this.vertices.add(a);
		if (!this.vertices.contains(b)) this.vertices.add(b);
		if (a.is_neighbour(b)) {
			return a.edge_to_neighbour(b);
		} else {
			HalfEdge ab = new HalfEdge(a);
			HalfEdge ba = new HalfEdge(b);
			ab.set_opposite(ba);
			a.add_edge(ab);
			b.add_edge(ba);
			return ab;
		}
	}
	
	public void addPolygon(Point[] newVertices, double scalarSpeedCoeff) {
		// on assume que les sommets sont dans l'ordre + sens direct (anti-horaire) de préférence
		int n = newVertices.length;
		HalfEdge[] edges = new HalfEdge[n];
		// tableau qui contiendra les arrêtes internes du polygone
		
		// initialisation des arrêtes et sommets
		edges[n-1] = this.addEdge((Vertex) newVertices[n-1], (Vertex) newVertices[0]);
		for (int i = n-2; i >= 0; i--) {
			edges[i] = this.addEdge((Vertex) newVertices[i], (Vertex) newVertices[i+1]);
			edges[i].set_next(edges[i+1]);
		}
		edges[n-1].set_next(edges[0]);
		
		// création du polygone
		Polygon poly = new Polygon(edges[n-1], scalarSpeedCoeff);
		for (HalfEdge edge : edges) edge.set_polygon(poly);
	}
}
