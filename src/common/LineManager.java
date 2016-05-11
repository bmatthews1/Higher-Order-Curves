package common;

import java.util.LinkedList;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * @author Ben
 *
 * The PointManager class is responsible for keeping track of the control points
 * and render points as well as recalculating point positions if a control point is moved
 */
public class LineManager {
	private LinkedList<ControlPoint> controlPoints;
	private LinkedList<LinkedList<Line>> lineLists;
	private Pane pane;
	
	
	/**
	 * creates a new Point manager with a reference to a
	 * Widnow object
	 * 
	 * @param window - a Window object
	 */
	public LineManager(LinkedList<ControlPoint> controlPoints, Pane pane){
		this.controlPoints = controlPoints;
		lineLists = new LinkedList<>();
		lineLists.add(new LinkedList<Line>());
		this.pane = pane;
	}
	
	/**
	 * Recalculates all of the relative RenderPoint and line positions
	 */
	public void recalculate(){
		
		LinkedList<Line> primaryLines = lineLists.get(0);
		
		for (int i = 0; i < controlPoints.size()-1; i++){
			ControlPoint current = controlPoints.get(i);
			ControlPoint next = controlPoints.get(i+1);
			
			Line line  = primaryLines.get(i);
			
			line.setStartX(current.getCenterX());
			line.setStartY(current.getCenterY());
			line.setEndX(next.getCenterX());
			line.setEndY(next.getCenterY());
		}
	}
	
	/**
	 * adds lines to this Manager
	 */
	public void addLines(){
		if (controlPoints.size() == 1) return;
		
		lineLists.add(new LinkedList<Line>());
		int numControlLines = controlPoints.size() - 1;
		
		for (int i = 0; i < numControlLines; i++){
			Line line = new Line();
			if (i == 0){
				line.setStroke(Color.LIGHTGRAY);
				line.setStrokeWidth(2);
				pane.getChildren().add(line);
				line.toBack();
			} else {
				double hue = 360 * (((i-1)%10)/10d);
				double sat = .6;
				double bri = 1;
				line.setStroke(Color.hsb(hue, sat, bri));
				line.setStrokeWidth(2);
				pane.getChildren().add(line);
				line.setVisible(false);
			}
			LinkedList<Line> list = lineLists.get(i);
			list.add(line);
		}
	}
	
	/**
	 * shows the sub lines
	 */
	public void showSubLines(){
		for (int i = 1; i < lineLists.size(); i++){
			LinkedList<Line> lines = lineLists.get(i);
			for (Line l : lines){
				l.setVisible(true);
			}
		}
	}
	
	/**
	 * hides the sub lines
	 */
	public void hideSubLines(){
		for (int i = 1; i < lineLists.size(); i++){
			LinkedList<Line> lines = lineLists.get(i);
			for (Line l : lines){
				l.setVisible(false);
			}
		}
	}
	
	/**
	 * calculates the subLine positions based off of the given percent
	 * 
	 * @param percent - the percent of the line to traverse
	 */
	public void calculateSubLinePositions(double percent){
		for (int i = 1; i < lineLists.size(); i++){
			LinkedList<Line> parent = lineLists.get(i-1);
			LinkedList<Line> child = lineLists.get(i);
			
			for (int j = 0; j < child.size(); j++){
				Line pLine1 = parent.get(j);
				Line pLine2 = parent.get(j+1);
				Line cLine = child.get(j);
				
				double x1, y1, x2, y2, diffX, diffY;
				x1 = pLine1.getStartX();
				y1 = pLine1.getStartY();
				x2 = pLine1.getEndX();
				y2 = pLine1.getEndY();
				diffX = x2 - x1;
				diffY = y2 - y1;
				
				cLine.setStartX(x1 + diffX*percent);
				cLine.setStartY(y1 + diffY*percent);
				
				x1 = pLine2.getStartX();
				y1 = pLine2.getStartY();
				x2 = pLine2.getEndX();
				y2 = pLine2.getEndY();
				diffX = x2 - x1;
				diffY = y2 - y1;
				
				cLine.setEndX(x1 + diffX*percent);
				cLine.setEndY(y1 + diffY*percent);
				
				cLine.toBack();
			}
		}
	}
	
	/**
	 * clears all of the lines and resets the lists
	 */
	public void clear(){
		for (LinkedList<Line> list : lineLists){
			for (Line l : list){
				pane.getChildren().remove(l);
			}
			list.clear();
		}
		lineLists.clear();
		lineLists.add(new LinkedList<Line>());
	}
}
