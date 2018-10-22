package pathFinding;

import geometry.LocatedPoint;
import geometry.Point;

public interface PathFinder {

	public Point[] shortestWay(LocatedPoint a, LocatedPoint b);
}
