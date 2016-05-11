package common;



import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import common.PointManager;

/**
 * @author Ben
 *
 * The ControlPoint Class extends circle and represents a interactable
 * point that is used as the seed to render the fractal
 */
public class ControlPoint extends Circle{
	private static final Color FILL = Color.GRAY;
	private static final double RADIUS = 5;
	
	private PointManager pointManager;
	
	/**
	 * Constructor for the ControlPoint takes a position [x, y] and
	 * a pointer to a PointManager object
	 * 
	 * @param x
	 * @param y
	 * @param pointManager
	 */
	public ControlPoint(double x, double y, PointManager pointManager){
		
		this.pointManager = pointManager;
		this.setFill(FILL);
		setCenter(x, y);
		this.setRadius(RADIUS);
		
		this.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				setHovered(true);
			}
		});
		this.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				setHovered(false);
			}
		});
		
		this.setOnMouseDragged(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e) {
				setCenter(e.getX(), e.getY());
			}
		});
	}
	
	/**
	 * sets the position of the ControlPoint [x, y]
	 * 
	 * @param x
	 * @param y
	 */
	private void setCenter(double x, double y){
		this.setCenterX(x);
		this.setCenterY(y);
		pointManager.recalculate();
	}
	
	/**
	 * Changes the ControlPoint to White if the node is hovered over
	 * by the mouse
	 * 
	 * @param hovered
	 */
	private void setHovered(boolean hovered){
		if (hovered) this.setFill(Color.WHITE);
		else this.setFill(FILL);
	}
}
