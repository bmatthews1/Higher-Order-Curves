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
public class PointManager {
	private LinkedList<ControlPoint> controlPoints;
	private LinkedList<LinkedList<Line>> lineLists;
	private Pane pane;
	
	
	/**
	 * creates a new Point manager with a reference to a
	 * Widnow object
	 * 
	 * @param window - a Window object
	 */
	public PointManager(LinkedList<ControlPoint> controlPoints, Pane pane){
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
		
		if (primaryLines.size() < controlPoints.size() - 1){
			for (int i = primaryLines.size(); i < controlPoints.size(); i++){
				Line l = new Line();
				primaryLines.add(l);
				l.setStroke(Color.LIGHTGRAY);
				l.setStrokeWidth(2.3);
				pane.getChildren().add(l);
				l.toBack();
			}
		}
		
		if (primaryLines.size() >= controlPoints.size()){
			int pops = primaryLines.size() - controlPoints.size();
			for (int i = 0; i < pops; i++){
				Line l = primaryLines.pop();
				pane.getChildren().remove(l);
			}
		}
		
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
}
