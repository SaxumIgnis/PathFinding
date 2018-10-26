package Test;

import java.util.Random;

import geometry.Point;
import pathFinding.PhysicalMap;
import pathFinding.ProcessedMap;

class MapGenerator {

	PhysicalMap makePhysicalMap(int numPoints, int numEdges) {
		Random generator = new Random();
		Point[] points = new Point[numPoints];
		int[][] edges = new int[numEdges][2];
		for (int i = 0; i < numPoints; i++) {
			points[i] = new Point(generator.nextDouble() * 100, generator.nextDouble() * 100, generator.nextDouble(), i);
			System.out.println("Point " + i + " - x = " + points[i].getX() + " - y = " + points[i].getY() + " - z = " + points[i].getZ());
		}
		for (int i = 0; i < numEdges; i++) {
			int x = generator.nextInt(numPoints);
			int y = generator.nextInt(numPoints);
			if (x != y) {
				edges[i] = new int[] {x, y};
				System.out.println("arete " + x +" -> " + y);
			}
		}
		return new PhysicalMap(points, edges);
	}
	
	ProcessedMap makeProcessedMap(int numPoints, int numEdges) {
		Random generator = new Random();
		Point[] points = new Point[numPoints];
		int[][] edges = new int[numEdges][2];
		for (int i = 0; i < numPoints; i++) {
			points[i] = new Point(generator.nextDouble() * 100, generator.nextDouble() * 100, generator.nextDouble(), i);
		}
		for (int i = 0; i < numEdges; i++) {
			int x = generator.nextInt(numPoints);
			int y = generator.nextInt(numPoints);
			if (x != y)
				edges[i] = new int[] {x, y};
		}
		return new ProcessedMap(points, edges);
	}
	
}
