package geometry;

public class Test {

	public static void main(String[] args) {
		Vector v1 = new Vector(1, 0, 0);
		Vector v2 = new Vector(-1, -0.00000000000001, 0);
		System.out.println(v1.angle2D(v2));
	}

}
