import java.util.Map;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
		GraphGenerator gg = new GraphGenerator();
		AnchorPane layout = new AnchorPane();
		AnchorPane picLayout = new AnchorPane();
		ImageView iv2 = new ImageView();
		iv2.setFitHeight(y);
		iv2.setFitWidth(x * 0.64);
		AnchorPane.setLeftAnchor(iv2, 0.0);
		AnchorPane.setTopAnchor(iv2, 0.0);
		picLayout.getChildren().add(iv2);
		
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
				String input = inputText.getText();
				String filename = valueText.getText();
				if (input != null && filename != null) {
					if (layout.getChildren().contains(outText)) {
						layout.getChildren().remove(outText);
						layout.getChildren().add(iv);
					}
					parser p = new parser(input);
					parser.Node node = p.traverseFirst();
					gg.draw(node, true);
					Image image = gg.renderImage();
					iv.setImage(image);
					iv2.setImage(image);
					gg.reset();
				}
			}
			
		});
		
		// configure zoom scene
		
		Scene zoom = new Scene(picLayout, x, y);
		Scene scene = new Scene(layout, x, y);
		
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
		layout.getChildren().addAll(button, inputText, valueText, valueLabel, inputLabel, outText);
		
		// set scene
		stage.setScene(scene);
		
		// show scene
		stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
