import javafx.scene.canvas.Canvas;

public class GraphGenerator {
	double cur_x;
	double cur_y;
	Canvas canvas;
	
	public GraphGenerator() {
		this(960 * 0.32, 720 * 0.5);
	}
	
	public GraphGenerator(double weight, double height) {
		cur_x = weight * 0.01;
		cur_y = height * 0.01;
		canvas = new ResizableCanvas();
		canvas.resize(weight, height);
	}
	

	
	private class ResizableCanvas extends Canvas {
		@Override
		public boolean isResizable() {
			return true;
		}
	}
}
