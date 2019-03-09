import java.util.ArrayList;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class LogicVisView extends Application {
	
	private static int x = 960;
	private static int y = 720;
	
	@Override
    public void start(Stage stage) {
		// old code
//        String javaVersion = System.getProperty("java.version");
//        String javafxVersion = System.getProperty("javafx.version");
//        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
//        Scene scene = new Scene(new StackPane(l), 640, 480);
//        stage.setScene(scene);
//        stage.show();
        
        // new code
		// configure stage
		stage.setTitle("LogicVis");
		AnchorPane layout = new AnchorPane();
		AnchorPane picLayout = new AnchorPane();
		ImageView iv2 = new ImageView();
		iv2.setFitHeight(y);
		iv2.setFitWidth(x);
		AnchorPane.setLeftAnchor(iv2, 0.0);
		AnchorPane.setTopAnchor(iv2, 0.0);
		picLayout.getChildren().add(iv2);
		Dispatcher D = new Dispatcher();
		
		Scene zoom = new Scene(picLayout, x, y);
		Scene scene = new Scene(layout, x, y);
		
		
		// configure label
		Label valueLabel = new Label();
		valueLabel.setPrefSize(x * 0.1, y * 0.05);
		valueLabel.setText("Value Input:");
		
		Label inputLabel = new Label();
		inputLabel.setPrefSize(x * 0.1, y * 0.05);
		inputLabel.setText("Code Input:");
		
		// configuring TextField
		TextArea inputText = new TextArea();
		inputText.setPrefSize(x * 0.32, y * 0.5);
		
		TextField valueText = new TextField();
		valueText.setPrefSize(x * 0.1, y * 0.05);
		
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
				
				Dialog<ArrayList<String>> dialog = configureDialog();
				Optional<ArrayList<String>> result = dialog.showAndWait();
				result.ifPresent(value -> {
					System.out.println(result.toString());
				});
				
			}
			
		});
		
		Button next = new Button();
		next.setText("NEXT");
		
		next.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				String input = inputText.getText();
				String filename = valueText.getText();
				if (input != null && filename != null) {
					parser p = new parser(input);
					parser.Node node = p.traverseFirst();
					GraphGenerator gg = new GraphGenerator(node);
					gg.paint();
					Image image = gg.renderImage();
					ImageView temp = D.getNextImageView();
					
					temp.setImage(image);
					stage.setWidth(D.cur_x + D.scale_x + 30);
					layout.getChildren().add(temp);
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
		AnchorPane.setTopAnchor(button, y * 0.8);
		
		AnchorPane.setLeftAnchor(next, x * 0.45);
		AnchorPane.setTopAnchor(next, y * 0.9);
		
		// inputText
		AnchorPane.setLeftAnchor(inputText, x * 0.08);
		AnchorPane.setTopAnchor(inputText, y * 0.1);
		
		// valueText
		AnchorPane.setLeftAnchor(valueText, x * 0.45);
		AnchorPane.setTopAnchor(valueText, y * 0.7);
		
		// valueLabel
		AnchorPane.setLeftAnchor(valueLabel, x * 0.45);
		AnchorPane.setTopAnchor(valueLabel, y * 0.65);
		
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
		layout.getChildren().addAll(button, inputText, valueText, valueLabel, inputLabel, outText, next);
		
		// set scene
		stage.setScene(scene);
		
		// show scene
		stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    
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
    
    private Dialog<ArrayList<String>> configureDialog() {
    	Dialog<ArrayList<String>> dialog = new Dialog<>();
    	
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
    	
    	TextField t1 = new TextField();
    	t1.setPromptText("t1");
    	TextField t2 = new TextField();
    	t2.setPromptText("t2");
    	
    	grid.add(new Label("t1"), 0, 0);
    	grid.add(new Label("t2"), 0, 1);
    	grid.add(t1, 1, 0);
    	grid.add(t2, 1, 1);
    	
    	dialog.getDialogPane().setContent(grid);
    	
    	Platform.runLater(() -> t1.requestFocus());
    	
    	dialog.setResultConverter(dialogButton -> {
    		if (dialogButton == enterButtonType) {
    			ArrayList<String> result = new ArrayList<String>();
    			result.add(t1.getText());
    			result.add(t2.getText());
    			return result;
    		}
    		return null;
    	});
    	
    	return dialog;
    }
}
