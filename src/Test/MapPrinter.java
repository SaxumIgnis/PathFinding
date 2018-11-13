package Test;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import geometry.BinaryEdge;
import pathFinding.ProcessedMap;

public class MapPrinter extends JComponent{

	private static final long serialVersionUID = 1L;
	private ProcessedMap map;

private static class Line{
    final int x1; 
    final int y1;
    final int x2;
    final int y2;   
    final Color color;

    public Line(int x1, int y1, int x2, int y2, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }               
}

private final LinkedList<Line> lines = new LinkedList<Line>();

public void addLine(int x1, int x2, int x3, int x4) {
    addLine(x1, x2, x3, x4, Color.black);
}

public void addLine(int x1, int x2, int x3, int x4, Color color) {
    lines.add(new Line(x1,x2,x3,x4, color));        
    repaint();
}

public void clearLines() {
    lines.clear();
    repaint();
}

public void makeMap(int numPoints, int numEdges) {
	MapGenerator generator = new MapGenerator();
	this.map = generator.makeProcessedMap(numPoints, numEdges);
}

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    for (Line line : lines) {
        g.setColor(line.color);
        g.drawLine(line.x1, line.y1, line.x2, line.y2);
    }
}

public static void main(String[] args) {
    JFrame testFrame = new JFrame();
    testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    final MapPrinter comp = new MapPrinter();
    comp.setPreferredSize(new Dimension(800, 800));
    testFrame.getContentPane().add(comp, BorderLayout.CENTER);
    comp.makeMap(10, 5);
    BinaryEdge[] edges = comp.map.getEdges();
    System.out.println("Computation terminated");
    
    for (BinaryEdge edge : edges) {
    	int x1 = (int) (8*Math.floor(edge.getOrigin().getX()));
    	int y1 = (int) (8*Math.floor(edge.getOrigin().getY()));
    	int x2 = (int) (8*Math.floor(edge.getEnd().getX()));
    	int y2 = (int) (8*Math.floor(edge.getEnd().getY()));
    	Color color = Color.black;
    	if (edge.crossable()) color = Color.blue;
    	comp.addLine(x1, y1, x2, y2, color);
    	//System.out.println("Affiche " + edge);
    }
    testFrame.pack();
    testFrame.setVisible(true);
}

}
