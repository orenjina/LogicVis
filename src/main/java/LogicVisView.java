import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LogicVisView extends Application implements EventHandler<ActionEvent>{
	 Button button;
	
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
		stage.setTitle("LogicVis");
		
		button = new Button();
		button.setText("Let's Do It!");
		button.setOnAction(this);
		
		StackPane layout = new StackPane();
		layout.getChildren().add(button);
		
		Scene scene = new Scene(layout, 1280, 960);
		stage.setScene(scene);
		stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

	@Override
	public void handle(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource() == button) {
			System.out.println("clicked");
		}
	}
}
