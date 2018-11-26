package Test;

import java.util.Random;

import geometry.Point;
import pathFinding.PhysicalMap;
import pathFinding.ProcessedMap;

final class MapGenerator {

	private static final Random generator = new Random();
	
	
	private static Point[] randomPoints(int numPoints) {
		Point[] points = new Point[numPoints];

		for (int i = 0; i < numPoints; i++) {
			points[i] = new Point(generator.nextDouble() * 100, generator.nextDouble() * 100, generator.nextDouble(), i);
		}
		return points;
	}
	
	private static int[][] randomEdges(int numPoints, int numEdges) {
		int[][] edges = new int[numEdges][2];
		for (int i = 0; i < numEdges; i++) {
			int x = generator.nextInt(numPoints);
			int y = generator.nextInt(numPoints);
			if (x == y) {
				x = (x + 1) % numPoints;
			}
			edges[i] = new int[] {x, y};
		}
		return edges;
	}
	
	PhysicalMap makePhysicalMap(int numPoints, int numEdges) {
		return new PhysicalMap(randomPoints(numPoints), randomEdges(numPoints, numEdges));
	}
	
	ProcessedMap makeProcessedMap(int numPoints, int numEdges) {
		return new ProcessedMap(randomPoints(numPoints), randomEdges(numPoints, numEdges));
	}
}
