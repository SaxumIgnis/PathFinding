package geometry;

public class Test {

	public static void main(String[] args) {
		Vertex a = new Vertex(0, 0, 0, 0);
		Vertex b = new Vertex(1, 0, 0, 1);
		Vertex c = new Vertex(0.5, 1, 0, 2);
		Vertex d = new Vertex(0.5, 2, 0, 3);
		BinaryEdge ab = new BinaryEdge(a, b, true);
		BinaryEdge cd = new BinaryEdge(c, d, true);
		System.out.println(ab.intersection(cd));
	}

}
