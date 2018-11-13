package geometry;

public final class HalfEdge extends BinaryEdge {

	private HalfEdge opposite;
	private HalfEdge next;
	private Polygon polygon;
	
	public HalfEdge(Vertex origin, Vertex end, boolean crossable) {
		super(origin, end, crossable);
		this.opposite = null;
		this.next = null;
		this.polygon = null;
		this.crossable = crossable;
	}
	
	public HalfEdge getNext() {
		return next;
	}

	public HalfEdge getOpposite() {
		return opposite;
	}
	
	public void setPolygon(final Polygon p) {
		this.polygon = p;
	}
	
	public void setOpposite(final HalfEdge o) {
		//System.out.println("arete "+origin.tag+" -> "+this.getEnd().tag+" new opposite : "+o.origin.tag+" -> "+o.getEnd().tag);
		this.opposite = o;
		o.opposite = this;
	}
	
	public void setNext(final HalfEdge n) {
		//System.out.println("arete "+origin.tag+" -> "+this.getEnd().tag+" new next : "+n.origin.tag+" -> "+n.getEnd().tag);
		this.next = n;
		//this.polygon = n.getPolygon();
	}
	
	@Override
	public void setCross(final boolean crossable) {
		this.crossable = crossable;
		this.opposite.crossable = crossable;
		// c'est plus simple quand c'est symétrique
	}
	
	public Polygon getPolygon() {
		return this.polygon;
	}
	
	HalfEdge previous() {
		HalfEdge p = this.opposite;
		while (p.next != this) {
			p = p.next.opposite;
		}
		return p;
	}

	double speedAlong() {
		return Math.max(this.getPolygon().coeffSpeed(this.getVector()), this.getOpposite().getPolygon().coeffSpeed(this.getVector()));
	}
	
	@Override
	public boolean equals(Object arg) {
		if (arg instanceof HalfEdge) {
			HalfEdge edge = (HalfEdge) arg;
			return (this.getEnd() == edge.getEnd() && this.getOrigin() == edge.getOrigin());
		} else {
			return false;
		}			
	}
}
