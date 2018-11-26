package geometry;

final class AccessAngle {

	enum Access {NULL, PARTIAL, TOTAL};

	private final Point center;
	private final Point p1;
	private final Point p2;
	private final Access access;


	AccessAngle(Vector v1, Vector v2) {
		this.center = new Point(0, 0, 0, -1);
		this.p1 = v1;
		this.p2 = v2;
		if (v1.equals(v2)) {
			this.access = Access.TOTAL;
		} else {
			this.access = Access.PARTIAL;
		}
	}

	AccessAngle(Point center, Point p1, Point p2) {
		this.center = center;
		this.p1 = p1;
		this.p2 = p2;
		if (p1.equals(p2)) {
			this.access = Access.TOTAL;
		} else {
			this.access = Access.PARTIAL;
		}
	}

	AccessAngle(Point center, boolean access) {
		this.center = center;
		if (access) {
			this.access = Access.TOTAL;
		} else {
			this.access = Access.NULL;
		}
		this.p1 = null;
		this.p2 = null;
	}

	private boolean useful(Vector v) {
		return v.rotSense(this.p1.minus(center)) == v.rotSense(this.p2.minus(center));
	}

	private boolean useful(Point p) {
		return this.useful(p.minus(center));
	}
	
	boolean allows(Point p) {
		if (this.access == Access.TOTAL) return true;
		if (this.access == Access.NULL) return false;
		
		return this.useful(p) || !this.contains(p);
	}

	boolean allowsLarge(Point p) {
		if (this.access == Access.NULL) return false;
		else if (this.access == Access.TOTAL) return true;
		
		else if (p.equals(this.p1) || p.equals(this.p2)) return true;
		else return this.allows(p);
	}

	private boolean contains(Vector v) {
		return v.scalarProd(this.p1.minus(this.center).norm().plus(this.p2.minus(this.center).norm())) > 0;
	}

	private boolean contains(Point p) {
		return this.contains(p.minus(this.center));
	}

	@Deprecated
	AccessAngle addVector(Vector v) {
		if (this.access == Access.NULL) {
			return this;
		} else if (this.p1 == null) {
			return new AccessAngle(v, v);
		} else if (p1.equals(p2)) {
			return new AccessAngle(this.center, this.p1, this.center.plus(v));
		} else if (this.allows(v)) {
			AccessAngle vv1 = new AccessAngle(v, this.center.minus(this.p1));
			if (vv1.allows(this.center.minus(this.p2))) {
				return new AccessAngle(v, this.center.minus(this.p2));
			} else {
				return vv1;
			}
		} else {
			if (this.contains(v))  {
				return this;
			} else {
				return new AccessAngle(this.center, false);
			}
		}

	}

	AccessAngle addPoint(Point p) {
		if (this.access == Access.NULL) {
			System.out.println(" tjs interdit");
			return this;
		} else if (this.p1 == null) {
			System.out.println(" initialisé");
			return new AccessAngle(this.center, p, p);
		} else if (this.allows(p)) {
			AccessAngle pcp1 = new AccessAngle(this.center, p, this.p1);
			System.out.println(" restreint");
			if (pcp1.allows(this.p2)) {
				return new AccessAngle(this.center, p, this.p2);
			} else {
				return pcp1;
			}
		} else {
			if (this.contains(p))  {
				System.out.println(" inchangé");
				return this;
			} else {
				System.out.println(" interdit");
				return new AccessAngle(this.center, false);
			}
		}

	}

	boolean isCompatible(AccessAngle angle) {
		
		if (this.access == Access.TOTAL || angle.access == Access.TOTAL)
			return true;
		
		Point tp;
		Point ap;
		if (this.center == angle.p1) {
			ap = angle.p2;
		} else {
			ap = angle.p1;
		}
		if (this.p1 == angle.center) {
			tp = this.p2;
		} else {
			tp = this.p1;
		}

		try {
			double a = this.center.minus(angle.center).rotSense(ap.minus(angle.center));
			double t = angle.center.minus(this.center).rotSense(tp.minus(this.center));
			return a == -t;
		} catch (NullPointerException e) {
			return true;
		}

	}

	@Override
	public String toString() {
		if (this.access == Access.NULL) return "nul";
		if (this.access == Access.TOTAL) return "total";
		return " partiel : " + this.p1.minus(this.center) + " à " + this.p2.minus(this.center);
	}
	
}
