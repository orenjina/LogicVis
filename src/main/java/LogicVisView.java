import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LogicVisView extends Application {
	
	private static int x = 640;
	private static int y = 480;
	
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
		
		// configure label
		Label valueLabel = new Label();
		valueLabel.setPrefSize(x * 0.1, y * 0.05);
		valueLabel.setText("Value Input:");
		
		// configuring button
		Button button = new Button();
		button.setText("Let's Do It!");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("clicked");
			}
			
		});
		
		button.setPrefSize(x * 0.1, y * 0.05);
		
		// configuring TextField
		TextField inputText = new TextField();
		inputText.setPrefSize(x * 0.32, y * 0.5);
		
		TextField valueText = new TextField();
		valueText.setPrefSize(x * 0.1, y * 0.05);
		
		// configuring anchorpane layout
		AnchorPane layout = new AnchorPane();
		
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
		
		// adding button to the pane
		layout.getChildren().addAll(button, inputText, valueText, valueLabel);
		
		// set scene
		Scene scene = new Scene(layout, x, y);
		stage.setScene(scene);
		
		// show scene
		stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
