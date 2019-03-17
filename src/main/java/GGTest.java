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
        
        String testfile = "private int a() {\n" + 
        		"	for (int i = 0; i < 3; i ++) {\n" + 
        		"		int b = 2;\n" + 
        		"	}\n" + 
        		"	return 2;\n" + 
        		"}";
        Parser par = new Parser(testfile);
//        List<MethodDeclaration> methods = parser.getAllMethods();
//        methods.forEach(n -> System.out.println("Method Collected: " + n.getName()));
//        MethodDeclaration m = par.getMethod("recur");
        Parser.Node r = par.traverse("a");
         
        GraphGenerator gg = new GraphGenerator(400, 200, r, null);
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
	
	private int a(int num) {
		int b = 2;
		if (1 == 1) {
			int one = 1;
			if (2==2) {
				return 1;
			} else {
				return 5;
			}
		} else {
			int two = 2;
		}
		int c = 2;
		c = 3;
		c = 4;
		c = 5;
		return 5;
	}
	
	private int forloop() {
		for (int i = 0; i < 3; i++) {
			int a = 0;
		}
		int b = 5;
		return 5;
	}
	
	private int recur(int num) {
		if (num > 0) {
			return recur(num - 1);
		}
		return num;
	}
}
