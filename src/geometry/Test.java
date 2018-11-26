package geometry;

public class Test {

	public static void main(String[] args) {
		Vertex a = new Vertex(1, 1, 0, 0);
		Vertex b = new Vertex(1, 0, 0, 1);
		Vertex c = new Vertex(0, 1, 0, 2);
		Vertex d = new Vertex(2, 2, 0, 3);
		a.addEdge(b, false);
		a.addEdge(c, false);
		a.addEdge(d, false);
		b.addEdge(c, false);
		c.addEdge(d, true);
		d.addEdge(b, true);
		a.update();
		b.update();
		c.update();
		d.update();
	}

}
