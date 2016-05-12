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
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
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
	
	public CheckBox toggleSubLines;
	public CheckBox toggleControlPoints;
	public CheckBox togglePrimaryLines;
	public CheckBox toggleBezierCurve;
	
	private Button reset;
	private Button runAnimation;
	private Button calculateBezier;
	private Button randomPoints;
	private Random random;
	
	public Slider slider;
	public Slider tickRate;
	private LineManager lineManager;
	private AnimationTimer timer;
	private boolean showingBezier = false;
	private boolean animating = false;
	
	private long last;
	private double counter = 0;
	private double cutoff = 100;
	private double tps = 30; //ticks per second 
	
	@Override
	/**
	 * launches the application
	 */
	public void start(Stage stage) throws Exception {
		initVars();
		scene = new Scene(pane, 600, 600);
		
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();

		stage.setX(bounds.getMinX());
		stage.setY(bounds.getMinY());
		stage.setWidth(bounds.getWidth());
		stage.setHeight(bounds.getHeight());
		
		stage.setScene(scene);
		stage.show();
		timer.start();
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
		
		//buttons
		runAnimation = new Button("start animation");
		reset = new Button("Reset");
		calculateBezier = new Button("calculate Bezier");
		randomPoints = new Button("Generate Random Points");
		
		slider = new Slider();
		slider.setOrientation(Orientation.HORIZONTAL);
		slider.setMax(1);
		slider.setMinWidth(400);
		slider.setValue(.5);
		
		tickRate = new Slider();
		tickRate.setOrientation(Orientation.HORIZONTAL);
		tickRate.setMax(120);
		tickRate.setMinWidth(400);
		tickRate.setValue(30);
		
		//check boxes
		toggleSubLines = new CheckBox("Sub Lines");
		toggleSubLines.setTextFill(Color.WHITE);
		toggleSubLines.setSelected(true);
		
		toggleControlPoints = new CheckBox("Control Points");
		toggleControlPoints.setTextFill(Color.WHITE);
		toggleControlPoints.setSelected(true);
		
		togglePrimaryLines = new CheckBox("Primary Lines");
		togglePrimaryLines.setTextFill(Color.WHITE);
		togglePrimaryLines.setSelected(true);
		
		toggleBezierCurve = new CheckBox("Bezier Curve Render");
		toggleBezierCurve.setTextFill(Color.WHITE);
		toggleBezierCurve.setSelected(true);
		
		hbox.getChildren().add(runAnimation);
		hbox.getChildren().add(reset);
		hbox.getChildren().add(slider);
		
		hbox2.getChildren().add(calculateBezier);
		hbox2.getChildren().add(randomPoints);
		
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(hbox2);
		vbox.getChildren().add(tickRate);
		vbox.getChildren().add(toggleControlPoints);
		vbox.getChildren().add(togglePrimaryLines);
		vbox.getChildren().add(toggleSubLines);
		vbox.getChildren().add(toggleBezierCurve);
		
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
//					lineManager.calculateBezierCurve(200);
					lineManager.calculateSubLinePositions(.5);
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
				toggleAnimation();
			}
		});
		
		reset.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				for (ControlPoint c: controlPoints){
					pane.getChildren().remove(c);
				}
				if (animating) toggleAnimation();
				controlPoints.clear();
				lineManager.clear();
			}
		});
		
		calculateBezier.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				lineManager.calculateBezierCurve(200);
				if (toggleBezierCurve.isScaleShape()) lineManager.showBezierCurve();
			}
		});
		
		slider.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				lineManager.calculateSubLinePositions(newValue.doubleValue());
			}
		});
		
		slider.setOnMousePressed(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				animating = true;
				toggleAnimation();
			}
		});
		
		tickRate.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				tps = newValue.doubleValue();
				cutoff = (int)(((120- tps)/120)*75 + 25);
			}
		});
		
		randomPoints.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
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
					if (!toggleControlPoints.isSelected()) c.setVisible(false);
					pane.getChildren().add(c);
					lineManager.addLines();
				}
				lineManager.recalculate();
				lineManager.calculateBezierCurve(200);
				lineManager.calculateSubLinePositions(.5);
				if (toggleSubLines.isSelected()) lineManager.showSubLines();
			}
		});
		
		toggleControlPoints.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				if (toggleControlPoints.isSelected()){
					lineManager.showControlPoints();
				} else {
					lineManager.hideControlPoints();
				}
			}
		});
		
		togglePrimaryLines.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				if (togglePrimaryLines.isSelected()){
					lineManager.showPrimaryLines();
				} else {
					lineManager.hidePrimaryLines();
				}
			}
		});
		
		toggleSubLines.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				if (toggleSubLines.isSelected()){
					lineManager.showSubLines();
				} else {
					lineManager.hideSubLines();
				}
			}
		});
		
		toggleBezierCurve.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				if (toggleBezierCurve.isSelected()){
					lineManager.showBezierCurve();
				} else {
					lineManager.hideBezierCurve();
				}
			}
		});
		
		timer = new AnimationTimer(){
			@Override
			public void handle(long now) {
				if (animating) {
					if ((now - last) > (1 /tps) * 1_000_000_000) {
						lineManager.calculateSubLinePositions(counter / cutoff);
						slider.setValue(counter / cutoff);
						last = now;
						counter++;

					}
					if (counter > cutoff) {
						counter = 0;
					} 
				}
			}
		};
	}
	
	/**
	 * toggle the animation flag
	 */
	private void toggleAnimation(){
		if (!animating){
			for (ControlPoint c : controlPoints){
				c.setVisible(false);
			}
			runAnimation.setText("pause animation");
		} else {
			for (ControlPoint c : controlPoints){
				c.setVisible(true);
			}
			runAnimation.setText("start animation");
		}
		animating ^= true;
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
