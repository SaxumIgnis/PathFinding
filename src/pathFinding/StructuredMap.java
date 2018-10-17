package pathFinding;

import java.util.ArrayList;
import java.util.Arrays;

import geometry.HalfEdge;
import geometry.Point;
import geometry.Polygon;
import geometry.Vertex;

public class StructuredMap {
	
	ArrayList<Vertex> vertices;
	
	StructuredMap() {
		this.vertices = new ArrayList<Vertex>();
	}
	
	StructuredMap(Point[][] polygons) {
		this.vertices = new ArrayList<Vertex>(polygons.length);
		for (Point[] polygon : polygons) {
			this.addPolygon(polygon, 1);
		}
	}
	
	public StructuredMap(Point[][] polygons, double[] scalarCoeffs) {
		this.vertices = new ArrayList<Vertex>(polygons.length);
		for (int i = 0; i < polygons.length; i++) {
			this.addPolygon(polygons[i], scalarCoeffs[i]);
		}
	}
	
	private HalfEdge addEdge(Vertex a, Vertex b) {
		// retourne l'arrête interne
		
		if (!this.vertices.contains(a)) this.vertices.add(a);
		if (!this.vertices.contains(b)) this.vertices.add(b);
		if (a.isNeighbour(b)) {
			HalfEdge ab = a.edgeToNeighbour(b);
			ab.getOpposite().setCross(0);
			return ab;
		} else {
			HalfEdge ab = new HalfEdge(a);
			HalfEdge ba = new HalfEdge(b);
			ab.setOpposite(ba);
			a.addEdge(ab);
			b.addEdge(ba);
			ab.setCross(Double.POSITIVE_INFINITY);
			return ab;
		}
	}
	
	public Polygon addPolygon(Point[] newVertices, double scalarSpeedCoeff) {
		// on assume que les sommets sont dans l'ordre + sens direct (anti-horaire) de préférence
		int n = newVertices.length;
		HalfEdge[] edges = new HalfEdge[n];
		// tableau qui contiendra les arrêtes internes du polygone
		
		// initialisation des arrêtes et sommets
		edges[n-1] = this.addEdge((Vertex) newVertices[n-1], (Vertex) newVertices[0]);
		for (int i = n-2; i >= 0; i--) {
			edges[i] = this.addEdge((Vertex) newVertices[i], (Vertex) newVertices[i+1]);
			edges[i].setNext(edges[i+1]);
		}
		edges[n-1].setNext(edges[0]);
		
		// création du polygone
		Polygon poly = new Polygon(edges[n-1], scalarSpeedCoeff);
		for (HalfEdge edge : edges) edge.setPolygon(poly);
		
		return poly;
	}
}
