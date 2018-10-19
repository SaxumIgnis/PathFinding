package geometry;

class AccessAngle {

	enum Access {NULL, PARTIAL, TOTAL};
	
	private final Point center;
	private final Point p1;
	private final Point p2;
	private final Access access;
	
	
	AccessAngle(Vector v1, Vector v2) {
		this.center = new Point(0, 0, 0);
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
	
	AccessAngle(boolean access) {
		this.center = new Point(0, 0, 0);
		if (access) {
			this.access = Access.TOTAL;
		} else {
			this.access = Access.NULL;
		}
		this.p1 = null;
		this.p2 = null;
	}
	
	boolean allows(Vector v) {
		if (this.access == Access.TOTAL) return true;
		if (this.access == Access.NULL) return false;
		
		return (((short) Math.signum(v.angle2D(this.center.minus(this.p1)))) == ((short) Math.signum(v.angle2D(this.center.minus(this.p2)))));
		// equivalent de v.angle2D(this.v1) * v.angle2D(this.v2) > 0 mais sans multiplication de double
	}
	
	boolean allows(Point p) {
		return this.allows(p.minus(center));
	}
	
	boolean allowsLarge(Vector v) {
		if (this.access == Access.TOTAL) return true;
		if (this.access == Access.NULL) return false;

		return Math.abs(((short) Math.signum(v.angle2D(this.center.minus(this.p1)))) - ((short) Math.signum(v.angle2D(this.center.minus(this.p2))))) < 2;
		// equivalent de v.angle2D(this.v1) * v.angle2D(this.v2) >= 0 mais sans multiplication de double		
	}
	
	boolean allowsLarge(Point p) {
		if (p.equals(this.p1) || p.equals(this.p2)) {
			return true;
		} else {
			return this.allowsLarge(p.minus(this.center));
		}
	}
	
	private boolean contains(Vector v) {
		return Math.abs(v.angle2D(this.center.minus(this.p1)) - v.angle2D(this.center.minus(this.p2))) < Math.PI;
	}
	
	private boolean contains(Point p) {
		return this.contains(p.minus(this.center));
	}
	
	AccessAngle addVector(Vector v) {
		if (this.access == Access.NULL) {
			return this;
		} else if (this.center.minus(this.p1) == null) {
			return new AccessAngle(v, v);
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
				return new AccessAngle(false);
			}
		}
		
	}
	
	AccessAngle adddPoint(Point p) {
		if (this.access == Access.NULL) {
			return this;
		} else if (this.p1 == null) {
			return new AccessAngle(this.center, p, p);
		} else if (this.allows(p)) {
			AccessAngle pcp1 = new AccessAngle(this.center, p, this.p1);
			if (pcp1.allows(this.p2)) {
				return new AccessAngle(this.center, p, this.p2);
			} else {
				return pcp1;
			}
		} else {
			if (this.contains(p))  {
				return this;
			} else {
				return new AccessAngle(false);
			}
		}
		
	}
	
	boolean isCompatible(AccessAngle angle) {
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
		
		double a = this.center.minus(angle.center).angle2D(ap.minus(angle.center));
		double t = angle.center.minus(this.center).angle2D(tp.minus(this.center));
		
		return Math.signum(a) == Math.signum(t);
	}
	
}
