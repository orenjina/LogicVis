import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.*;

// a util class
// shapes are horizontally centered
public class UIUtil {
	// A few constants used throughout GraphGenerator and UIUtil
	public static final double SHAPE_HEIGHT = 40;
	public static final double VERTICAL_ARROW_LENGTH = 30;
	public static final double HORIZONTAL_ARROW_LENGTH = 80;
	public static final double MIN_WIDTH = 80;
	
	// A few constants used in drawing the shapes
	private static final double MAX_WIDTH = 180;
	private static final double TEXT_MARGIN = 10;
	private static final double TEXT_HEIGHT = 12;
	private static final double ARROW_PART_LENGTH = 5;

	/**
	 * Returns the width of the text box that would hold the given statement, in pixels.
	 * @param statement - the String to calculate
	 * @return the width of a text box containing the statement
	 */
	public static double calculateTextboxWidth(String statement) {
		Text text = new Text(statement);
		text.applyCss();
		return Math.min(Math.max(MIN_WIDTH, text.getLayoutBounds().getWidth() + 2 * TEXT_MARGIN), MAX_WIDTH);
	}
	
	/**
	 * Draws text centered at (pos_x, pos_y), and returns the required width of its text box, in pixels.
	 * @param gc - the GraphicsContext to draw on
	 * @param statement - the String to draw
	 * @param pos_x - x-coordinate to center statement around
	 * @param pos_y - y-coordinate to center statement around
	 * @return the minimum required width for a text box that contains this statement, in pixels.
	 */
	private static double drawText(GraphicsContext gc, String statement, double pos_x, double pos_y) {
		// find width
		double text_width = 0;
		int original_length = statement.length();
		int cur_length = statement.length();
		do {
			statement = statement.substring(0, cur_length);
			cur_length--;
			Text text = new Text(statement);
			text.applyCss();
			text_width = text.getLayoutBounds().getWidth() + 2 * TEXT_MARGIN;
		} while (text_width > MAX_WIDTH);
		
		if (cur_length + 1 != original_length) {
			statement = statement.substring(0, cur_length - 1) + "...";
		}
		
		double shape_width = Math.max(MIN_WIDTH, text_width);
		
		gc.strokeText(statement, pos_x + TEXT_MARGIN - text_width / 2 + shape_width / 2, pos_y + SHAPE_HEIGHT / 2 + TEXT_HEIGHT / 2);
		return shape_width;
	}
	
	/**
	 * Draws the start node (oval) horizontally left-aligned at (pos_x, pos_y), and returns the width of
	 * the node, in pixels.
	 * @param gc - the GraphicsContext to draw on
	 * @param pos_x - x-coordinate to center node around
	 * @param pos_y - y-coordinate of the top of the node
	 * @return the width of the node.
	 */
	public static double drawStart(GraphicsContext gc, double pos_x, double pos_y) {
		double shape_width = drawText(gc, "Start", pos_x, pos_y);
		gc.strokeOval(pos_x, pos_y, shape_width, SHAPE_HEIGHT);
		return shape_width;
	}
	
	/**
	 * Draws a condition node (diamond) horizontally left-aligned at (pos_x, pos_y), and returns the width 
	 * of the node, in pixels.
	 * @param gc - the GraphicsContext to draw on
	 * @param statement - the statement to draw
	 * @param pos_x - x-coordinate to center node around
	 * @param pos_y - y-coordinate of the top of the node
	 * @return the width of the node.
	 */
	public static double drawConditional(GraphicsContext gc, String statement, double pos_x, double pos_y) {
		double shape_width = drawText(gc, statement, pos_x, pos_y);
		gc.strokePolygon(new double[] {
				pos_x, pos_x + 0.5 * shape_width, pos_x + shape_width, pos_x + 0.5 * shape_width
		}, new double[] {
				pos_y + 0.5 * SHAPE_HEIGHT, pos_y, pos_y + 0.5 * SHAPE_HEIGHT, pos_y + SHAPE_HEIGHT
		}, 4);
		return shape_width;
	}
	
	/**
	 * Draws a statement node (rectangle) horizontally left-aligned at (pos_x, pos_y), and returns the width 
	 * of the node, in pixels.
	 * @param gc - the GraphicsContext to draw on
	 * @param statement - the statement to draw
	 * @param pos_x - x-coordinate to center node around
	 * @param pos_y - y-coordinate of the top of the node
	 * @return the width of the node.
	 */
	public static double drawStatement(GraphicsContext gc, String statement, double pos_x, double pos_y) {
		double shape_width = drawText(gc, statement, pos_x, pos_y);
		gc.strokeRect(pos_x, pos_y, shape_width, SHAPE_HEIGHT);
		return shape_width;
	}
	
	/**
	 * Draws an arrow originating at (from_x, from_y) downwards if down is true, and rightwards otherwise.
	 * @param gc - the GraphicsContext to draw on
	 * @param from_x - x-coordinate the arrow starts at
	 * @param from_y - y-coordinate the arrow starts at
	 * @param down - true means draw the arrow downwards, false means draw it rightwards
	 */
	public static void drawArrow(GraphicsContext gc, double from_x, double from_y, boolean down) {
		if (down) {
			double to_y = from_y + VERTICAL_ARROW_LENGTH;
			gc.strokeLine(from_x, from_y, from_x, to_y);
			gc.strokeLine(from_x - ARROW_PART_LENGTH, to_y - ARROW_PART_LENGTH, from_x, to_y);
			gc.strokeLine(from_x + ARROW_PART_LENGTH, to_y - ARROW_PART_LENGTH, from_x, to_y);
		} else {
			double to_x = from_x + HORIZONTAL_ARROW_LENGTH;
			gc.strokeLine(from_x, from_y, to_x, from_y);
			gc.strokeLine(to_x - ARROW_PART_LENGTH, from_y - ARROW_PART_LENGTH, to_x, from_y);
			gc.strokeLine(to_x - ARROW_PART_LENGTH, from_y + ARROW_PART_LENGTH, to_x, from_y);
		}
	}
	
	public static void drawArrowDown(GraphicsContext gc, String statement, double from_x, double from_y) {
		drawArrow(gc, from_x, from_y, true);
		gc.strokeText(statement, from_x + TEXT_MARGIN, from_y + VERTICAL_ARROW_LENGTH / 2);
	}
	
	public static void drawArrowRight(GraphicsContext gc, String statement, double from_x, double from_y) {
		drawArrow(gc, from_x, from_y, false);
		Text text = new Text(statement);
		text.applyCss();
		double width = text.getLayoutBounds().getWidth();
		gc.strokeText(statement, from_x + HORIZONTAL_ARROW_LENGTH / 2 - width / 2, from_y - TEXT_MARGIN / 2);
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
		drawArrow(gc, mid_x, mid_y, true);
	}
	
	public static void drawParameter(GraphicsContext gc, String statement, double pos_x, double pos_y) {
		drawArrow(gc, pos_x - HORIZONTAL_ARROW_LENGTH, pos_y + 0.5 * SHAPE_HEIGHT, false);
		drawStatement(gc, statement, pos_x, pos_y);
	}
	
	public static void drawReturn(GraphicsContext gc, String statement, double pos_x, double pos_y) {
		drawArrow(gc, pos_x, pos_y + 0.5 * SHAPE_HEIGHT, false);
		drawStatement(gc, statement, pos_x, pos_y);
	}
	
	public static void drawRecurse(GraphicsContext gc, String statement, double pos_x, double pos_y) {
		double width = drawStatement(gc, statement, pos_x, pos_y);
		pos_x += width;
		
		// Coordinate scale values
		double y1 = 0.35;
		double y2 = 0.2;
		double y3 = 0.65;
		double y4 = 0.8;
		double y5 = 0.5;
		
		double x1 = 0.1;
		double x2 = 0.25;
		
		//-
		//-
		gc.strokeLine(pos_x, pos_y + y1 * SHAPE_HEIGHT, pos_x + x1 * MIN_WIDTH, pos_y + y1 * SHAPE_HEIGHT);
		gc.strokeLine(pos_x, pos_y + y3 * SHAPE_HEIGHT, pos_x + x1 * MIN_WIDTH, pos_y + y3 * SHAPE_HEIGHT);
		
		// |
		//-
		//-
		// |
		gc.strokeLine(pos_x + x1 * MIN_WIDTH, pos_y + y1 * SHAPE_HEIGHT, pos_x + x1 * MIN_WIDTH, pos_y + y2 * SHAPE_HEIGHT);
		gc.strokeLine(pos_x + x1 * MIN_WIDTH, pos_y + y3 * SHAPE_HEIGHT, pos_x + x1 * MIN_WIDTH, pos_y + y4 * SHAPE_HEIGHT);
		
		// |\
		//-  \
		//-  /
		// |/
		gc.strokeLine(pos_x + x1 * MIN_WIDTH, pos_y + y2 * SHAPE_HEIGHT, pos_x + x2 * MIN_WIDTH, pos_y + y5 * SHAPE_HEIGHT);
		gc.strokeLine(pos_x + x1 * MIN_WIDTH, pos_y + y4 * SHAPE_HEIGHT, pos_x + x2 * MIN_WIDTH, pos_y + y5 * SHAPE_HEIGHT);
	}
}
