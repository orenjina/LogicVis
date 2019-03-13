import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class LogicVisView extends Application {
	
	private static int x = 960;
	private static int y = 720;
	private ActionGenerator action;
	private HashSet<ImageView> images;
	private parser.Node myNode;
	private ImageView iv2;
	private Scene zoom;
	private Stage mainstage;
	
	
	@Override
    public void start(Stage stage) {
        
        // new code
		// configure stage
		mainstage = stage;
		stage.setTitle("LogicVis");
		AnchorPane layout = new AnchorPane();
		AnchorPane picLayout = new AnchorPane();
		iv2 = new ImageView();
		iv2.setFitHeight(y);
		iv2.setFitWidth(x);
		AnchorPane.setLeftAnchor(iv2, 0.0);
		AnchorPane.setTopAnchor(iv2, 0.0);
		picLayout.getChildren().add(iv2);
		Dispatcher D = new Dispatcher();
		
		zoom = new Scene(picLayout, x, y);
		Scene scene = new Scene(layout, x, y);
		
		// configure label
//		Label valueLabel = new Label();
//		valueLabel.setPrefSize(x * 0.1, y * 0.05);
//		valueLabel.setText("Value Input:");
		
		Label inputLabel = new Label();
		inputLabel.setPrefSize(x * 0.1, y * 0.05);
		inputLabel.setText("Code Input:");
		
		// configuring TextField
		TextArea inputText = new TextArea();
		inputText.setPrefSize(x * 0.32, y * 0.5);
		
//		TextField valueText = new TextField();
//		valueText.setPrefSize(x * 0.1, y * 0.05);
		
		TextField outText = new TextField();
		outText.setPrefSize(x * 0.32, y * 0.5);
		outText.setEditable(false);
		outText.setText("Graph");
		
		// configuring image
		ImageView iv = new ImageView();
		iv.resize(x * 0.32, y * 0.5);
		iv.setFitHeight(y * 0.5);
		iv.setFitWidth(x * 0.32);
		iv.setPreserveRatio(false);
		
		// configuring button
		Button button = new Button();
		button.setText("Let's Do It!");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
//				String input = inputText.getText();
//				String filename = valueText.getText();
//				if (input != null && filename != null) {
//					if (layout.getChildren().contains(outText)) {
//						layout.getChildren().remove(outText);
//						layout.getChildren().add(iv);
//					}
//					parser p = new parser(input);
//					parser.Node node = p.traverseFirst();
//					GraphGenerator gg = new GraphGenerator(node);
//					gg.paint();
//					Image image = gg.renderImage();
//					iv.setImage(image);
//					iv2.setImage(image);
//				}
				String input = inputText.getText();
				if (input != null) {
					// parse node
					parser p = new parser(input);
					parser.Node node = p.traverseFirst();
					myNode = node;
					// get execution
					action = new ActionGenerator(input);
					String[] types = action.getParameterTypes();
					String[] names = action.getParameterNames();
					if (types.length != 0) {
						// there are params, configure dialog
						Dialog<String[]> dialog = configureDialog(types, names);
						Optional<String[]> result = dialog.showAndWait();
						
						// dialog value is set
						result.ifPresent(value -> {
							action.execute(result.get());
							ArrayList<GraphNode> currentNodes = action.getCurrentState();
							display(layout, currentNodes);
							if (layout.getChildren().contains(outText)) {
								layout.getChildren().remove(outText);
							}
						});
					}
				}
			}
			
		});
		
		Button next = new Button();
		next.setPrefSize(x * 0.1, y * 0.05);
		next.setText("NEXT");
		
		next.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (!layout.getChildren().contains(outText)) {
					// dialog value is set
					action.next();
					ArrayList<GraphNode> currentNodes = action.getCurrentState();
					display(layout, currentNodes);
				}
			}	
		});
		
		// configure zoom scene

		iv.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		         System.out.println("Graph pressed ");
		         stage.setScene(zoom);
		     }
		});
		
		iv2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		         System.out.println("Graph pressed again ");
		         stage.setScene(scene);
		     }
		});
		
		
		button.setPrefSize(x * 0.1, y * 0.05);
		
		// configuring anchorpane layout
		
		// set aspect ratio
		// button
		AnchorPane.setLeftAnchor(button, x * 0.45);
		AnchorPane.setTopAnchor(button, y * 0.7);
		
		AnchorPane.setLeftAnchor(next, x * 0.45);
		AnchorPane.setTopAnchor(next, y * 0.8);
		
		// inputText
		AnchorPane.setLeftAnchor(inputText, x * 0.08);
		AnchorPane.setTopAnchor(inputText, y * 0.1);
		
		// inputLabel
		AnchorPane.setLeftAnchor(inputLabel, x * 0.08);
		AnchorPane.setTopAnchor(inputLabel, y * 0.05);
		
		// outputText
		AnchorPane.setLeftAnchor(outText, x * 0.58);
		AnchorPane.setTopAnchor(outText, y * 0.1);
		
		// image
		AnchorPane.setLeftAnchor(iv, x * 0.58);
		AnchorPane.setTopAnchor(iv, y * 0.1);
		
		// adding button to the pane
		layout.getChildren().addAll(button, inputText, inputLabel, outText, next);
		
		// set scene
		stage.setScene(scene);
		
		// show scene
		stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    
    
    // dispatches image view
    private class Dispatcher {
		public double cur_x;
		public double cur_y;
		public double scale_x = x * 0.32;
		public Stack<ImageView> s;
		
		public Dispatcher() {
			cur_x = x * 0.58;
			cur_y = y * 0.1;
			s = new Stack<ImageView>();
		}
		
		public ImageView getNextImageView() {
			ImageView iv = new ImageView();
			iv.setFitHeight(y * 0.5);
			iv.setFitWidth(x * 0.32);
			cur_x += scale_x + 30;
			AnchorPane.setLeftAnchor(iv, cur_x);
			AnchorPane.setTopAnchor(iv, cur_y);
			s.push(iv);
			return iv;
		}
		
		public ImageView peek() {
			return s.peek();
		}
		
		public ImageView pop() {
			return s.pop();
		}
		
		public boolean isEmpty() {
			return s.isEmpty();
		}
    }
    
    // returns a pop up window
    private Dialog<String[]> configureDialog(String[] types, String[] names) {
	    	Dialog<String[]> dialog = new Dialog<>();
	    	
	    	// configuration
	    	
	    	dialog.setTitle("Parameter Input");
	    	dialog.setHeaderText("Please type in your parameter values");
	    	
	    	// set the button types
	    	ButtonType enterButtonType = new ButtonType("Enter", ButtonData.OK_DONE);
	    	dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);
	    	
	    	// Create Fields
	    	GridPane grid = new GridPane();
	    	grid.setHgap(10);
	    	grid.setVgap(10);
	    	grid.setPadding(new Insets(20, 150, 10, 10));
	    	
	    	ArrayList<TextField> textFields = new ArrayList<TextField>();
	    	
	    	for (int i = 0; i < types.length; i++) {
	    		TextField temp = new TextField();
	    		temp.setPromptText("Parameter Value");
	    		grid.add(new Label(names[i] + " : " + types[i]), 0, i);
	    		grid.add(temp, 1, i);
	    		textFields.add(temp);
	    	}
	    	
//	    	TextField t1 = new TextField();
//	    	t1.setPromptText("t1");
//	    	TextField t2 = new TextField();
//	    	t2.setPromptText("t2");
//	    	
//	    	grid.add(new Label("t1"), 0, 0);
//	    	grid.add(new Label("t2"), 0, 1);
//	    	grid.add(t1, 1, 0);
//	    	grid.add(t2, 1, 1);
	    	
	    	dialog.getDialogPane().setContent(grid);
	    	
	    	Platform.runLater(() -> textFields.get(0).requestFocus());
	    	
	    	dialog.setResultConverter(dialogButton -> {
	    		if (dialogButton == enterButtonType) {
	    			String[] result = new String[textFields.size()];
	    			for (int i = 0; i < textFields.size(); i ++) {
	    				result[i] = textFields.get(i).getText();
	    			}
	    			return result;
	    		}
	    		return null;
	    	});
	    	
	    	return dialog;
    }
    
    private void redraw(Pane layout, ArrayList<ImageView> views) {
    		if (images != null) {
        		for (ImageView iv : images) {
        			layout.getChildren().remove(iv);
        		}
    		}
    		
    		double cur_x = x * 0.58;
    		double cur_y = y * 0.1;
    		double scale_x = x * 0.32;
    		
    		images = new HashSet<ImageView>();
    		
    		for (ImageView view : views) {
    			AnchorPane.setLeftAnchor(view, cur_x);
    			AnchorPane.setTopAnchor(view, cur_y);
    			cur_x += scale_x + 30;
    			images.add(view);
    			layout.getChildren().add(view);
    		}
    }
    
    private void display(Pane layout, ArrayList<GraphNode> currentNodes) {
		ArrayList<ImageView> views = new ArrayList<ImageView>();
		for (GraphNode graphnode : currentNodes) {
			GraphGenerator gg = new GraphGenerator(myNode, graphnode.getParameters());
			gg.paint();
			
			// create image view
			Image image = gg.renderImage();
			ImageView temp = new ImageView();
			
			// configure image view
			temp.setImage(image);
			temp.setFitHeight(y * 0.5);
			temp.setFitWidth(x * 0.32);
			views.add(temp);
			
			temp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			     @Override
			     public void handle(MouseEvent event) {
			         System.out.println("Graph pressed ");
			         iv2.setImage(temp.getImage());
			         mainstage.setScene(zoom);
			     }
			});
		}
		redraw(layout, views);
    }
}
