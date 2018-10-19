package geometry;

public class Test {

	public static void main(String[] args) {
		Vector v = new Vector(-0.5, 2, 0);
		Vector v1 = new Vector(1, 0, 0);
		Vector v2 = new Vector(0, 1, 0);
		AccessAngle a = new AccessAngle(v1, v2);
		System.out.println(a.allows(v));
	}

}
