import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javafx.embed.swing.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;


public class GraphGenerator {
	// Factor at which to increase the size of the canvas
	private static final double SCALE_THRESHOLD = 1.5;
	
	// Starting draw coordinates
	private static final int INITIAL_X = 150;
	private static final int INITIAL_Y = 30;
	
	// Current coordinates to draw at
	double cur_x;
	double cur_y;
	
	double width;
	double height;
	ResizableCanvas canvas;
	GraphicsContext gc;
	
	public GraphGenerator() {
		this(960 * 0.32, 720 * 0.5);
	}
	
	public GraphGenerator(double width, double height) {
		reset();
	}
	
	public void draw(parser.Node node, boolean withArrow) {
		expand();
		
		if (withArrow) {
			UIUtil.drawArrow(gc, cur_x, cur_y, true);
			cur_y += UIUtil.VERTICAL_ARROW_LENGTH;
		}
		
		System.out.println(node.getContent());
		switch (node.getType()) {
			case CONDITION:
				drawConditionStatement(node, withArrow);
				break;
			case RETURN:
				drawReturnStatement(node, withArrow);
				break;
			case NONE:
				drawPlainStatement(node, withArrow);
				break;
			default:
		}
	}
	
	private void drawReturnStatement(parser.Node node, boolean withArrow) {
		// MVP
		String statement = node.getContent();
		double width = UIUtil.calculateTextboxWidth(statement);
		UIUtil.drawRecurse(gc, statement, cur_x - width / 2, cur_y);
		cur_y += UIUtil.SHAPE_HEIGHT;
		
		// Shouldn't have to do this?
		/*
		Map<parser.Node, String> children = node.getChildren();
		for (parser.Node cur : children.keySet()) {
			draw(cur, true);
		}
		*/
	}
	
	private void drawConditionStatement(parser.Node node, boolean withArrow) {
		String statement = node.getContent();
		double width = UIUtil.calculateTextboxWidth(statement);
		UIUtil.drawConditional(gc, statement, cur_x - width / 2, cur_y);
		cur_y += UIUtil.SHAPE_HEIGHT; 
		double temp_x = cur_x;
		double temp_y = cur_y;
		Map<parser.Node, String> children = node.getChildren();
		for (parser.Node cur : children.keySet()) {
			if (children.get(cur).equals("False")) {
				double start_x = cur_x + width / 2;
				double start_y = cur_y - UIUtil.SHAPE_HEIGHT / 2;
				UIUtil.drawArrowRight(gc, "False", start_x, start_y);
				cur_x = start_x + UIUtil.HORIZONTAL_ARROW_LENGTH + UIUtil.calculateTextboxWidth(cur.getContent()) / 2;
				cur_y -= UIUtil.SHAPE_HEIGHT;
				draw(cur, false);
				
				// Reset draw coordinates
				cur_x = temp_x;
				cur_y = temp_y;
			} else {
				UIUtil.drawArrowDown(gc, "True", cur_x, cur_y);
				cur_y += UIUtil.VERTICAL_ARROW_LENGTH;
				draw(cur, false);
				
				// Reset draw coordinates
				cur_x = temp_x;
				cur_y = temp_y;
			}
		}
	}
	
	private void drawPlainStatement(parser.Node node, boolean withArrow) {
		// MVP
		String statement = node.getContent();
		double width = UIUtil.calculateTextboxWidth(statement);
		UIUtil.drawStatement(gc, statement, cur_x - width / 2, cur_y);
		cur_y += UIUtil.SHAPE_HEIGHT;
		
		Map<parser.Node, String> children = node.getChildren();
		for (parser.Node cur : children.keySet()) {
			draw(cur, true);
		}
	}
	
	public WritableImage renderImage() {
		WritableImage image = canvas.snapshot(null, null);
		
		// debug
		save(image);

		return image;
	}
	
	private void save(WritableImage image) {
		// save file
		File file = new File("chart.png");
		BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
	    try {
	        ImageIO.write(bImage, "png", file);
	    } catch (IOException e) {
	        // TODO: handle exception here
	    		e.printStackTrace();
	    }
	}
	
	private class ResizableCanvas extends Canvas {
		@Override
		public boolean isResizable() {
			return true;
		}
	}
	
	public void reset() {
		reset(960 * 0.32, 720 * 0.5);
	}
	
	public void reset(double weight, double height) {
		cur_x = INITIAL_X;
		cur_y = INITIAL_Y;
		canvas = new ResizableCanvas(); 
		canvas.setWidth(weight);
		canvas.setHeight(height);
		this.width = weight;
		this.height = height;
		this.gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(1);
		UIUtil.drawStart(gc, cur_x - UIUtil.MIN_WIDTH / 2, cur_y);
		cur_y += UIUtil.SHAPE_HEIGHT; 
	}
	
	private void expand() {
		if (cur_y + SCALE_THRESHOLD * UIUtil.SHAPE_HEIGHT > height || cur_x + SCALE_THRESHOLD * UIUtil.MIN_WIDTH > width) {
			// expand 
			System.out.println("expand");
			height *= SCALE_THRESHOLD;
			width *= SCALE_THRESHOLD;
			canvas.setHeight(height);
			canvas.setWidth(width);
		}
	}
}
