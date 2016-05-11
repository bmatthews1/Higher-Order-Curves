package visualizations;

import java.util.LinkedList;
import java.util.Random;

import common.ControlPoint;
import common.LineManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
	private Scene scene;
	private Pane pane;
	private LinkedList<ControlPoint> controlPoints;
	private Button reset;
	private Button runAnimation;
	private Button showSubLines;
	private Button calculateBezier;
	private Button randomPoints;
	private Random random;
	private Slider slider;
	private LineManager lineManager;
	private AnimationTimer timer;
	
	private long last;
	private double counter;
	private double cutoff;
	
	@Override
	/**
	 * launches the application
	 */
	public void start(Stage stage) throws Exception {
		initVars();
		scene = new Scene(pane, 600, 600);
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Initializes all of the variables and objects
	 */
	private void initVars(){
		random = new Random();
		pane = new Pane();
		HBox hbox = new HBox();
		HBox hbox2 = new HBox();
		VBox vbox = new VBox();
		pane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		runAnimation = new Button("run animation");
		reset = new Button("Reset");
		showSubLines = new Button("show sub lines");
		calculateBezier = new Button("calculate Bezier");
		randomPoints = new Button("Generate Random Points");
		
		slider = new Slider();
		slider.setOrientation(Orientation.HORIZONTAL);
		slider.setMax(1);
		slider.setMinWidth(400);
		slider.setValue(.5);
		hbox.getChildren().add(runAnimation);
		hbox.getChildren().add(reset);
		hbox.getChildren().add(showSubLines);
		hbox.getChildren().add(slider);
		
		hbox2.getChildren().add(calculateBezier);
		hbox2.getChildren().add(randomPoints);
		
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(hbox2);
		
		pane.getChildren().add(vbox);
		controlPoints = new LinkedList<>();
		lineManager = new LineManager(controlPoints, pane);
		
		addMethods();
	}
	
	/**
	 * adds the methods and logic to the various objects
	 */
	private void addMethods(){
		
		pane.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.SECONDARY) {
                	ControlPoint c = new ControlPoint(e.getX(), e.getY(), lineManager);
					controlPoints.add(c);
					pane.getChildren().add(c);
					lineManager.addLines();
					lineManager.recalculate();
                }
            }
        });
		
//		pane.setOnMouseReleased(new EventHandler<MouseEvent>(){
//			@Override
//			public void handle(MouseEvent e) {
//				if (e.isSecondaryButtonDown()){
//					ControlPoint c = new ControlPoint(e.getX(), e.getY(), lineManager);
//					controlPoints.add(c);
//					pane.getChildren().add(c);
//					lineManager.addLines();
//					lineManager.recalculate();
//				}
//			}
//		});
		
		runAnimation.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				animate();
			}
		});
		
		reset.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				if (timer != null) timer.stop();
				for (ControlPoint c: controlPoints){
					pane.getChildren().remove(c);
				}
				controlPoints.clear();
				lineManager.clear();
			}
		});
		
		showSubLines.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				lineManager.showSubLines();
				lineManager.calculateSubLinePositions(.5);
			}
		});
		
		calculateBezier.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				lineManager.calculateBezierCurve(500);
				lineManager.showBezierCurve();
			}
		});
		
		slider.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				lineManager.calculateSubLinePositions(newValue.doubleValue());
			}
		});
		
		randomPoints.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				if (timer != null) timer.stop();
				int numPoints = random.nextInt(40) + 10;
				lineManager.clear();
				for (ControlPoint c : controlPoints){
					pane.getChildren().remove(c);
				}
				controlPoints.clear();
				
				for (int i = 0; i < numPoints; i++){
					double x = random.nextDouble()*scene.getWidth();
					double y = random.nextDouble()*scene.getHeight();
					
					ControlPoint c = new ControlPoint(x, y, lineManager);
					controlPoints.add(c);
					lineManager.addLines();
				}
				lineManager.recalculate();
				animate();
			}
		});
	}
	
	private void animate(){
//		lineManager.calculateBezierCurve(500);
		lineManager.showSubLines();
		lineManager.hidePrimaryLines();
		
		for (ControlPoint c : controlPoints){
			c.setVisible(false);
		}
		
		last = System.nanoTime();
		counter = 0;
		cutoff = 100;
		
		timer = new AnimationTimer(){
			@Override
			public void handle(long now) {
				if ((now - last) > (1/60d)*1_000_000_000){
					lineManager.calculateSubLinePositions(counter/cutoff);
					last = now;
					counter++;
					
				}
				
				if (counter > cutoff){
					counter = 0;
				}
			}
		};
		timer.start();
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
