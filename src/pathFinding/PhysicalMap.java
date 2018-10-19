package pathFinding;

import java.util.ArrayList;
import geometry.HalfEdge;
import geometry.Point;
import geometry.Polygon;
import geometry.Vertex;

public class PhysicalMap {
	
	protected ArrayList<Vertex> vertices;
	protected Polygon[] polygons;
	
	PhysicalMap() {
		this.vertices = new ArrayList<Vertex>();
	}
	
	PhysicalMap(Point[][] polygons) {
		this.vertices = new ArrayList<Vertex>(polygons.length);
		this.polygons = new Polygon[polygons.length];
		for (int i = 0; i < polygons.length; i++) {
			this.polygons[i] = this.addPolygon(polygons[i], 1, i);
		}
		this.update();
	}
	
	public PhysicalMap(Point[][] polygons, double[] scalarCoeffs) {
		this.vertices = new ArrayList<Vertex>(polygons.length);
		this.polygons = new Polygon[polygons.length];
		for (int i = 0; i < polygons.length; i++) {
			this.polygons[i] = this.addPolygon(polygons[i], scalarCoeffs[i], i);
		}
		this.update();
	}
	
	private void update() {
		for (Polygon polygon : this.polygons) polygon.updateEdges();
		for (Vertex vertex : this.vertices) vertex.updateAngle();
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
	
	public Polygon addPolygon(Point[] newVertices, double scalarSpeedCoeff, int polygonTag) {
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
		Polygon poly = new Polygon(edges[n-1], scalarSpeedCoeff, polygonTag);
		for (HalfEdge edge : edges) edge.setPolygon(poly);
		
		return poly;
	}
	
}
