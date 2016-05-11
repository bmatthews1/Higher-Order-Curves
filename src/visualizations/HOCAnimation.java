package visualizations;

import java.util.LinkedList;

import common.ControlPoint;
import common.PointManager;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author Ben Matthews
 * 
 * This class allows the user to construct multiple control points
 * and run an animation that displays a visual representation of the
 * method of drawing higher order bezier curves
 *
 */
public class HOCAnimation extends Application{
	private Pane pane;
	private LinkedList<ControlPoint> controlPoints;
	private boolean controlDown = false;
	private Button reset;
	private Button runAnimation;
	private PointManager pointManager;
	
	@Override
	/**
	 * launches the application
	 */
	public void start(Stage stage) throws Exception {
		initVars();
		Scene scene = new Scene(pane, 600, 600);
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Initializes all of the variables and objects
	 */
	private void initVars(){
		pane = new Pane();
		HBox hbox = new HBox();
		pane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		runAnimation = new Button("run animation");
		reset = new Button("Reset");
		hbox.getChildren().add(runAnimation);
		hbox.getChildren().add(reset);
		
		pane.getChildren().add(hbox);
		controlPoints = new LinkedList<>();
		pointManager = new PointManager(controlPoints, pane);
		
		addMethods();
	}
	
	/**
	 * adds the methods and logic to the various objects
	 */
	private void addMethods(){
		pane.setOnKeyPressed(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent k) {
				if (k.isControlDown()){
					controlDown = true;
					pane.setCursor(Cursor.CROSSHAIR);
				}
			}
		});
		
		pane.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent k) {
				if (!k.isControlDown()){
					controlDown = false;
					pane.setCursor(Cursor.DEFAULT);
				}
				
			}
		});
		
		pane.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e) {
				if (controlDown){
					ControlPoint c = new ControlPoint(e.getX(), e.getY(), pointManager);
					controlPoints.add(c);
					pane.getChildren().add(c);
					pointManager.recalculate();
				}
			}
		});
		
		reset.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				for (ControlPoint c: controlPoints){
					pane.getChildren().remove(c);
				}
				controlPoints.clear();
				pointManager.recalculate();
			}
		});
	}
	
	/**
	 * 
	 * entry point for the program
	 * 
	 * @param args
	 */
	public static void main(String args[]){
		launch();
	}
}
