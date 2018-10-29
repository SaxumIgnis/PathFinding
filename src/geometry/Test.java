package geometry;

public class Test {

	public static void main(String[] args) {
		Vertex a = new Vertex(68.15341985692213, 80.05298677030585, 0, 0);
		Vertex b = new Vertex(67.81546983934477, 51.21177657308292, 0, 1);
		Vertex c = new Vertex(11.46545919540316, 8.482602617843671, 0, 2);
		Vertex d = new Vertex(3.3219149052721053, 42.22050449605067, 0, 3);
		BinaryEdge ab = new BinaryEdge(a, c, true);
		BinaryEdge cd = new BinaryEdge(b, d, true);
		System.out.println(ab.intersection(cd));
	}

}
