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
	
	public static void drawArrow(GraphicsContext gc, double from_x, double from_y, double to_x, double to_y, Boolean down) {
		gc.strokeLine(from_x, from_y, to_x, to_y);
		double dir_x = to_x - from_x;
		double dir_y = to_y - from_y;
		if (down) {
			gc.strokeLine(to_x - 0.15 * dir_x + 5, to_y - 0.15 * dir_y, to_x, to_y);
			gc.strokeLine(to_x - 0.15 * dir_x - 5, to_y - 0.15 * dir_y, to_x, to_y);
		} else {
			gc.strokeLine(to_x - 0.15 * dir_x, to_y - 0.15 * dir_y + 5, to_x, to_y);
			gc.strokeLine(to_x - 0.15 * dir_x, to_y - 0.15 * dir_y - 5, to_x, to_y);
		}
	}
	
	public static void drawArrowDown(GraphicsContext gc, String statement, double from_x, double from_y, double scale) {
		drawArrow(gc, from_x, from_y, from_x, from_y + scale, true);
		gc.strokeText(statement, from_x + 5, from_y + scale / 2 + 10, 100);
	}
	
	public static void drawArrowRight(GraphicsContext gc, String statement, double from_x, double from_y, double scale) {
		drawArrow(gc, from_x, from_y, from_x + scale, from_y, false);
		gc.strokeText(statement, from_x + 5, from_y - 2, 100);
	}
	
	public static void drawArrowLeft(GraphicsContext gc, String statement, double from_x, double from_y, double scale) {
		drawArrow(gc, from_x, from_y, from_x - scale, from_y, false);
		gc.strokeText(statement, from_x - scale + 5, from_y - 2, 100);
	}
	
	public static void drawArrowUp(GraphicsContext gc, String statement, double from_x, double from_y, double to_x, double to_y) {
		gc.strokeLine(from_x, from_y, from_x - 10, from_y);
		drawLineArrowHorizontal(gc, statement, from_x - 10, from_y, to_x, to_y);
	}
	
	// Must point from left to right
	public static void drawLine(GraphicsContext gc, String statement, double from_x, double from_y, double to_x, double to_y) {
		gc.strokeLine(from_x, from_y, to_x, to_y);
		double dir_x = to_x - from_x;
		double dir_y = to_y - from_y;
		gc.strokeText(statement, from_x + 0.25 * dir_x + 2, from_y + 0.75 * dir_y + 2, 0.5 * dir_x);
	}
	
	public static void drawLineArrowVertical(GraphicsContext gc, String statement, double from_x, double from_y, double to_x, double to_y) {
		drawLine(gc, statement, Math.min(from_x, to_x), from_y, Math.max(from_x, to_x), from_y);
		drawArrow(gc, to_x, from_y, to_x, to_y, true);
	}
	public static void drawLineArrowHorizontal(GraphicsContext gc, String statement, double from_x, double from_y, double to_x, double to_y) {
		drawLine(gc, statement, from_x, Math.min(from_y, to_y), from_x , Math.max(to_y, from_y));
		drawArrow(gc, from_x, to_y, to_x, to_y, false);
	}
	
	public static void drawParameter(GraphicsContext gc, String statement, double pos_x, double pos_y, double scale_x, double scale_y) {
		drawArrow(gc, pos_x - 0.2 * scale_x, pos_y + 0.5 * scale_y, pos_x, pos_y + 0.5 * scale_y, false);
		drawStatement(gc, statement, pos_x, pos_y, scale_x, scale_y);
	}
	
	public static void drawReturn(GraphicsContext gc, String statement, double pos_x, double pos_y, double scale_x, double scale_y) {
		drawArrow(gc, pos_x, pos_y + 0.5 * scale_y, pos_x  + 0.2 * scale_x, pos_y + 0.5 * scale_y, false);
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
