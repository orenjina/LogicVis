import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LogicVisView extends Application {
	
	private static int x = 960;
	private static int y = 720;
	private ActionGenerator action;
	private HashSet<ImageView> images;
	private HashSet<Label> labels;
	private Parser.Node myNode;
	private ImageView iv2;
	private Scene zoom;
	private Stage mainstage;
	private ScrollPane sp;
	private VBox vb;
	private Parser p;
	
	@Override
    public void start(Stage stage) {
        
        // new code
		// configure stage
		mainstage = stage;
		stage.setTitle("LogicVis");
		AnchorPane layout = new AnchorPane();
		AnchorPane picLayout = new AnchorPane();
		sp = new ScrollPane();
		sp.setPrefViewportHeight(y);
		sp.setPrefViewportWidth(x);
		vb = new VBox();
		vb.getChildren().add(layout);
		sp.setContent(vb);
		
		// configure zoomed view
		iv2 = new ImageView();
		AnchorPane.setLeftAnchor(iv2, 0.0);
		AnchorPane.setTopAnchor(iv2, 0.0);
		picLayout.getChildren().add(iv2);
		
		zoom = new Scene(picLayout, x, y);
		Scene scene = new Scene(sp, x, y);
		
		// configure label
//		Label valueLabel = new Label();
//		valueLabel.setPrefSize(x * 0.1, y * 0.05);
//		valueLabel.setText("Value Input:");
		
		Label inputLabel = new Label();
		inputLabel.setText("Code Input:");
		
		// configuring TextField
		TextArea inputText = new TextArea();
		
//		TextField valueText = new TextField();
//		valueText.setPrefSize(x * 0.1, y * 0.05);
		
		TextField outText = new TextField();
		outText.setEditable(false);
		outText.setText("Graph");
		
		// configuring button
		Button button = new Button();
		button.setText("Let's Do It!");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String input = inputText.getText();
				if (input != null) {
					// fix the error that the code cannot be executed if it does not has an extra
					// blank line
					input += "\n";
					System.out.println("input received");
					
					// back up previous
					Parser temp_p = p;
					Parser.Node temp_node = myNode;
					ActionGenerator temp_action = action;
					
					// parse node
					p = new Parser(input);
					Parser.Node node = p.traverseFirst();
					myNode = node;
					// get execution
					action = new ActionGenerator(input);

					if (action.getParameterNum() != 0) { // there are parameters, get them
						
						String[] types = action.getParameterTypes();
						String[] names = action.getParameterNames();
						// there are params, configure dialog
						Dialog<String[]> dialog = configureDialog(types, names);
						Optional<String[]> result = dialog.showAndWait();
						
						// dialog value is set
						result.ifPresent(value -> {
							action.execute(result.get());
							if (action.errorMessage == null) {
								ArrayList<GraphNode> currentNodes = action.getCurrentState();
								display(layout, currentNodes);
								if (layout.getChildren().contains(outText)) {
									layout.getChildren().remove(outText);
								}
							} else {
								showAlert("Warning", "Execution Failed", "Please check your code...");
							}
						});
						
						// if cancelled, go back
						if (!result.isPresent()) {
							p = temp_p;
							myNode = temp_node;
							action = temp_action;
						}
						
					} else { // no parameter, just display
						if (layout.getChildren().contains(outText)) {
							layout.getChildren().remove(outText);
						}
						GraphNode tempnode = new GraphNode(new ParamList(0, 0),0);
						ArrayList<GraphNode> temparr = new ArrayList<GraphNode>();
						temparr.add(tempnode);
						display(layout, temparr);
						System.out.println("no param");
					}
				}
			}
			
		});
		
		// configure the next button
		Button next = new Button();
		next.setText("NEXT");
		
		next.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("action is " + (action == null ? "null" : "not null"));
				if (action == null) {
//					Alert alert = new Alert(AlertType.INFORMATION);
//					alert.setTitle("Warning");
//					alert.setHeaderText("Warning");
//					alert.setContentText("Please press \"Lets do it!\" first");
//
//					alert.showAndWait();
					showAlert("Warning", "Warning", "Please press \"Lets do it!\" first");
				} else if (action.isDone()) {
//					Alert alert = new Alert(AlertType.INFORMATION);
//					alert.setTitle("Warning");
//					alert.setHeaderText("Warning");
//					alert.setContentText("No more steps...");
//
//					alert.showAndWait();
					showAlert("Warning", "Warning", "No more steps...");
				} else if (!layout.getChildren().contains(outText)) {
					// dialog value is set
					action.next();
					ArrayList<GraphNode> currentNodes = action.getCurrentState();
					display(layout, currentNodes);
					moveButtons(button, next);
				}
			}	
		});
		
		// configure zoom scene
		iv2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		         System.out.println("Graph pressed again ");
		         stage.setScene(scene);
		     }
		});
		
		// configure resizing window
		stage.setMinWidth(x);
		stage.setMinHeight(y);
		
		ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
			recalculateBounds();
			configureLayout(button, next, inputText, inputLabel, outText);
			if (action != null) {
				display(layout, action.getCurrentState());
			}
		};

		stage.widthProperty().addListener(stageSizeListener);
		stage.heightProperty().addListener(stageSizeListener);
		
		// configure scrolling buttons
		sp.hvalueProperty().addListener((obs, oldVal, newVal) -> {
			moveButtons(button, next);
		});

		configureLayout(button, next, inputText, inputLabel, outText);
		
		// adding button to the pane
		layout.getChildren().addAll(button, inputText, inputLabel, outText, next);
		
		// set scene
		stage.setScene(scene);
		
		// show scene
		stage.show();
    }
	
	// pop up a dialog with title, header, and content
	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
	}
	
	private void moveButtons(Button button, Button next) {
		// Value between 0 and 1
		double h = sp.getHvalue();
		double w = vb.getWidth();
		
		// buttons
		AnchorPane.setLeftAnchor(button, x * 0.45 + (w - x) * h);
		AnchorPane.setTopAnchor(button, y * 0.7);

		AnchorPane.setLeftAnchor(next, x * 0.45 + (w - x) * h);
		AnchorPane.setTopAnchor(next, y * 0.8);
	}
	
	// Recalculates x and y based on the current width and height
	private void recalculateBounds() {
		int w = (int) mainstage.getWidth();
		int h = (int) mainstage.getHeight();
		x = Math.min(h * 4 / 3, w);
		y = Math.min(w * 3 / 4, h);
	}
	
	// Configures the layout (position + size) of all components on the scene
	private void configureLayout(Button button, Button next, TextArea inputText, Label inputLabel, TextField outText) {
		// configuring anchorpane layout

		// buttons
		button.setPrefSize(x * 0.1, y * 0.05);
		next.setPrefSize(x * 0.1, y * 0.05);
		moveButtons(button, next);
		
		// inputText
		inputText.setPrefSize(x * 0.32, y * 0.5);
		AnchorPane.setLeftAnchor(inputText, x * 0.08);
		AnchorPane.setTopAnchor(inputText, y * 0.1);
		
		// inputLabel
		inputLabel.setPrefSize(x * 0.1, y * 0.05);
		AnchorPane.setLeftAnchor(inputLabel, x * 0.08);
		AnchorPane.setTopAnchor(inputLabel, y * 0.05);
		
		// outputText
		outText.setPrefSize(x * 0.32, y * 0.5);
		AnchorPane.setLeftAnchor(outText, x * 0.58);
		AnchorPane.setTopAnchor(outText, y * 0.1);
		

	}

    public static void main(String[] args) {
        launch();
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
    
    // draw or redraw the whole graph
    private void redraw(Pane layout, ArrayList<ImageView> views, ArrayList<Label> returns) {
    		if (images != null) {
        		for (ImageView iv : images) {
        			layout.getChildren().remove(iv);
        		}
    		}
    		
    		if (labels != null) {
    			for (Label l : labels) {
    				layout.getChildren().remove(l);
    			}
    		}
    		
    		double cur_x = x * 0.58;
    		double cur_y = y * 0.1;
    		double scale_x = x * 0.32;
    		
    		labels = new HashSet<Label>();
    		images = new HashSet<ImageView>();
    		
    		for (int i = 0; i < views.size(); i++) {
    			ImageView view = views.get(i);
    			Label l = returns.get(i);
    			
    			AnchorPane.setLeftAnchor(view, cur_x);
    			AnchorPane.setTopAnchor(view, cur_y);
    			AnchorPane.setLeftAnchor(l, cur_x);
    			AnchorPane.setTopAnchor(l, y * 0.65);
    			cur_x += scale_x + 30;
    			images.add(view);
    			labels.add(l);
    			layout.getChildren().addAll(view, l);
    		}
//    		
//    		for (ImageView view : views) {
//    			AnchorPane.setLeftAnchor(view, cur_x);
//    			AnchorPane.setTopAnchor(view, cur_y);
//    			cur_x += scale_x + 30;
//    			images.add(view);
//    			layout.getChildren().add(view);
//    		}
    }
    
    // draw images and configure imageviews, and then dislay them
    private void display(Pane layout, ArrayList<GraphNode> currentNodes) {
		ArrayList<ImageView> views = new ArrayList<ImageView>();
		ArrayList<Label> returns = new ArrayList<Label>();
		for (GraphNode graphnode : currentNodes) {
			GraphGenerator gg = new GraphGenerator(myNode, graphnode.getParameters());
			List<Parser.Node> list = p.getRecurNodes();
			if (list.size() == 0 || graphnode.highlightNode < 0) {
				gg.paint(null);
			} else {
				gg.paint(list.get(graphnode.highlightNode));
			}
//			System.out.println(graphnode.getReturnValue());
			// create image view
			Image image = gg.renderImage();
			ImageView temp = new ImageView();
			
			// configure image view
			temp.setImage(image);
			temp.setFitHeight(y * 0.5);
			temp.setFitWidth(x * 0.32);
			views.add(temp);
			
			// create Label
			Label l = new Label();
			l.setPrefSize(x * 0.32, y * 0.05);
			l.setText(graphnode.getReturnValue() == null ? "" : "This returns: " + graphnode.getReturnValue());
			
			returns.add(l);
			
			// if clicked then zoom
			temp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					double w = mainstage.getWidth();
					double h = mainstage.getHeight();
					System.out.println("Graph pressed ");
					iv2.setImage(temp.getImage());
					iv2.setFitHeight(h);
					iv2.setFitWidth(h * 4 / 3 * 0.64);
					mainstage.setScene(zoom);
					mainstage.setWidth(w);
					mainstage.setHeight(h);
				}
			});
		}
		redraw(layout, views, returns);
    }
}
