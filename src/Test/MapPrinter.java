package Test;
/*
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JFrame;

import geometry.BinaryEdge;
import geometry.Vertex;
import pathFinding.PhysicalMap;

class MapPrinter extends JComponent {
	
	private static final long serialVersionUID = 1L;
	private final PhysicalMap map;
	
	public MapPrinter(PhysicalMap map) {
		this.map = map;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
	    super.paintComponent(g2);
		 // draw Line2D.Double
		    for (BinaryEdge e : this.map.getEdges()) {
		    	if (e.getCross()) {
		    		g2.setPaint(Color.blue);
		    	} else {
		    		g2.setPaint(Color.black);
		    	}
		    	g2.draw(new Line2D.Double(e.getOrigin().getX(), e.getOrigin().getY(), e.getEnd().getX(), e.getEnd().getY()));
		    }
	}

	public void paint (Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
	 // draw Line2D.Double
	    for (BinaryEdge e : this.map.getEdges()) {
	    	if (e.getCross()) {
	    		g2.setPaint(Color.blue);
	    	} else {
	    		g2.setPaint(Color.black);
	    	}
	    	g2.draw(new Line2D.Double(e.getOrigin().getX(), e.getOrigin().getY(), e.getEnd().getX(), e.getEnd().getY()));
	    }
	    
	}
	
	private void update() {
		this.repaint();
	}
	
	public static void main(String[] args) {
		MapGenerator generator = new MapGenerator();
	    JFrame testFrame = new JFrame();
	    testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    final MapPrinter comp = new MapPrinter(generator.makePhysicalMap(10, 2));
	    comp.setPreferredSize(new Dimension(320, 200));
	    testFrame.getContentPane().add(comp, BorderLayout.CENTER);
	    comp.update();
	    System.out.println("fini");
	}
	
}*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import geometry.BinaryEdge;
import pathFinding.PhysicalMap;

public class MapPrinter extends JComponent{

	private static final long serialVersionUID = 1L;
	private PhysicalMap map;

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
	this.map = generator.makePhysicalMap(numPoints, numEdges);
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
    comp.setPreferredSize(new Dimension(100, 100));
    testFrame.getContentPane().add(comp, BorderLayout.CENTER);
    JPanel buttonsPanel = new JPanel();
    JButton newLineButton = new JButton("New Line");
    JButton clearButton = new JButton("Clear");
    buttonsPanel.add(newLineButton);
    buttonsPanel.add(clearButton);
    testFrame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    int[] i = new int[] {0};
    comp.makeMap(10, 3);
    BinaryEdge[] edges = comp.map.getEdges();
    
    newLineButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
        	if (i[0]<edges.length) {
        		int x1 = (int) Math.floor(edges[i[0]].getOrigin().getX());
        		int x2 = (int) Math.floor(edges[i[0]].getOrigin().getY());
        		int y1 = (int) Math.floor(edges[i[0]].getEnd().getX());
        		int y2 = (int) Math.floor(edges[i[0]].getEnd().getY());
        		Color color = Color.black;
        		if (edges[i[0]].getCross()) color = Color.blue;
        		comp.addLine(x1, y1, x2, y2, color);
        		i[0] += 1;
        	}
        }
    });
    clearButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            comp.clearLines();
        }
    });
    testFrame.pack();
    testFrame.setVisible(true);
}

}
