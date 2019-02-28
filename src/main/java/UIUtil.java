import javafx.scene.canvas.GraphicsContext;

// a util class
public class UIUtil {
	public static void drawStart(GraphicsContext gc, double pos_x, double pos_y, double scale_x, double scale_y) {
		gc.strokeOval(pos_x, pos_y, scale_x, scale_y);
		gc.strokeText("Start", pos_x + 0.25 * scale_x, pos_y + 0.75 * scale_y, 0.5 * scale_x);
	}
	
	public static void drawConditional(GraphicsContext gc, String statement, double pos_x, double pos_y, double scale_x, double scale_y) {
		gc.strokePolygon(new double[] {
				pos_x, pos_x + 0.5 * scale_x, pos_x + scale_x, pos_x + 0.5 * scale_x
		}, new double[] {
				pos_y + 0.5 * scale_y, pos_y, pos_y + 0.5 * scale_y, pos_y + scale_y
		}, 4);
		gc.strokeText(statement, pos_x + 0.25 * scale_x, pos_y + 0.75 * scale_y, 0.5 * scale_x);
	}
	
	public static void drawStatement(GraphicsContext gc, String statement, double pos_x, double pos_y, double scale_x, double scale_y) {
		gc.strokeRect(pos_x, pos_y, scale_x, scale_y);
		gc.strokeText(statement, pos_x + 0.25 * scale_x, pos_y + 0.75 * scale_y, 0.5 * scale_x);
	}
	
	public static void drawArrow(GraphicsContext gc, double from_x, double from_y, double to_x, double to_y) {
		gc.strokeLine(from_x, from_y, to_x, to_y);
		double dir_x = to_x - from_x;
		double dir_y = to_y - from_y;
		gc.strokeLine(to_x - 0.15 * dir_x + 5, to_y - 0.15 * dir_y, to_x, to_y);
		gc.strokeLine(to_x - 0.15 * dir_x - 5, to_y - 0.15 * dir_y, to_x, to_y);
	}
	
	// Must point from left to right
	public static void drawLine(GraphicsContext gc, String statement, double from_x, double from_y, double to_x, double to_y) {
		gc.strokeLine(from_x, from_y, to_x, to_y);
		double dir_x = to_x - from_x;
		double dir_y = to_y - from_y;
		gc.strokeText(statement, from_x + 0.25 * dir_x, from_y + 0.75 * dir_y, 0.5 * dir_x);
	}
	
	public static void drawLineArrow(GraphicsContext gc, String statement, double from_x, double from_y, double to_x, double to_y, double mid_x, double mid_y) {
		drawLine(gc, statement, Math.min(from_x, mid_x), Math.min(from_y, mid_y), Math.max(from_x, mid_x), Math.max(from_y, mid_y));
		drawArrow(gc, mid_x, mid_y, to_x, to_y);
	}
	
	public static void drawParameter(GraphicsContext gc, String statement, double pos_x, double pos_y, double scale_x, double scale_y) {
		drawArrow(gc, pos_x - 0.2 * scale_x, pos_y + 0.5 * scale_y, pos_x, pos_y + 0.5 * scale_y);
		drawStatement(gc, statement, pos_x, pos_y, scale_x, scale_y);
	}
	
	public static void drawReturn(GraphicsContext gc, String statement, double pos_x, double pos_y, double scale_x, double scale_y) {
		drawArrow(gc, pos_x, pos_y + 0.5 * scale_y, pos_x  + 0.2 * scale_x, pos_y + 0.5 * scale_y);
		drawStatement(gc, statement, pos_x, pos_y, scale_x, scale_y);
	}
	
	public static void drawRecurse(GraphicsContext gc, String statement, double pos_x, double pos_y, double scale_x, double scale_y) {
		drawStatement(gc, statement, pos_x, pos_y, scale_x, scale_y);
		//-
		//-
		gc.strokeLine(pos_x + scale_x, pos_y + 0.25 * scale_y, pos_x + 1.1 * scale_x, pos_y + 0.25 * scale_y);
		gc.strokeLine(pos_x + scale_x, pos_y + 0.75 * scale_y, pos_x + 1.1 * scale_x, pos_y + 0.75 * scale_y);
		
		// |
		//-
		//-
		// |
		gc.strokeLine(pos_x + 1.1 * scale_x, pos_y + 0.25 * scale_y, pos_x + 1.1 * scale_x, pos_y + 0.15 * scale_y);
		gc.strokeLine(pos_x + 1.1 * scale_x, pos_y + 0.75 * scale_y, pos_x + 1.1 * scale_x, pos_y + 0.85 * scale_y);
		
		// |\
		//-  \
		//-  /
		// |/
		gc.strokeLine(pos_x + 1.1 * scale_x, pos_y + 0.15 * scale_y, pos_x + 1.2 * scale_x, pos_y + 0.5 * scale_y);
		gc.strokeLine(pos_x + 1.1 * scale_x, pos_y + 0.85 * scale_y, pos_x + 1.2 * scale_x, pos_y + 0.5 * scale_y);
	}
}
