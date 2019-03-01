import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GGTest extends Application
{
    public static void main(String[] args) 
    {
        Application.launch(args);
    }
     
	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
        // Create the Canvas
        Canvas canvas = new Canvas(400, 200);
        // Set the width of the Canvas
        canvas.setWidth(400);
        // Set the height of the Canvas
        canvas.setHeight(200);
         
        GraphGenerator gg = new GraphGenerator(400, 200);
        // Get the graphics context of the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
         
        // Draw a Text
        gc.strokeText("Hello Canvas", 150, 100);
         
        // Create the Pane
        Pane root = new Pane();
        // Set the Style-properties of the Pane
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
         
        // Add the Canvas to the Pane
//        gg.draw("");
        canvas = gg.canvas;
        root.getChildren().add(canvas);
        // Create the Scene
        Scene scene = new Scene(root);
        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title of the Stage
        stage.setTitle("Creation of a Canvas");
        // Display the Stage
        stage.show();  
	}
	
	private int a() {
		int b = 2;
		if (1 == 1) {
			int one = 1;
		} else {
			int two = 2;
		}
		return 5;
	}
}
