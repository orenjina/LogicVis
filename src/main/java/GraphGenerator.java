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
	double cur_x;
	double cur_y;
	double weight;
	double height;
	double scale_x = 80;
	double scale_y = 36;
	ResizableCanvas canvas;
	GraphicsContext gc;
	
	public GraphGenerator() {
		this(960 * 0.32, 720 * 0.5);
	}
	
	public GraphGenerator(double weight, double height) {
		cur_x = 89;
		cur_y = 30;
		canvas = new ResizableCanvas();
		canvas.setWidth(weight);
		canvas.setHeight(height);
		this.weight = weight;
		this.height = height;
		this.gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(1);
		UIUtil.drawStart(gc, cur_x - this.scale_x / 2, cur_y, this.scale_x, this.scale_y);
		cur_y += this.scale_y; 
	}
	
	public void draw(parser.Node node, Boolean withArrow) {
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
	
	private void drawReturnStatement(parser.Node node, Boolean withArrow) {
		// MVP
		if (withArrow) {
			UIUtil.drawArrow(gc, cur_x, cur_y, cur_x, cur_y + scale_y, true);
			cur_y += scale_y;
		}
		UIUtil.drawRecurse(gc, node.getContent(), cur_x - this.scale_x / 2, cur_y, scale_x, scale_y);
		cur_y += scale_y;
		
		Map<parser.Node, String> children = node.getChildren();
		for (parser.Node cur : children.keySet()) {
			draw(cur, true);
		}
	}
	
	private void drawConditionStatement(parser.Node node, Boolean withArrow) {
		if (withArrow) {
			UIUtil.drawArrow(gc, cur_x, cur_y, cur_x, cur_y + scale_y, true);
			cur_y += scale_y;
		}
		UIUtil.drawConditional(gc, node.getContent(), cur_x - scale_x / 2, cur_y, scale_x, scale_y);
		cur_y += this.scale_y; 
		double temp_x = cur_x;
		double temp_y = cur_y;
		Map<parser.Node, String> children = node.getChildren();
		for (parser.Node cur : children.keySet()) {
			if (children.get(cur).equals("True")) {
				double start_x = cur_x + scale_x / 2;
				double start_y = cur_y - scale_y / 2;
				UIUtil.drawArrowRight(gc, "TRUE", start_x, start_y, scale_x / 2);
				cur_x = start_x + scale_x;
				cur_y -= scale_y;
				draw(cur, false);
				cur_x = temp_x;
				cur_y = temp_y;
			} else {
				UIUtil.drawArrowDown(gc, "FALSE", cur_x, cur_y, scale_y);
				cur_y += scale_y;
				draw(cur, false);
				cur_x = temp_x;
				cur_y = temp_y;
			}
		}
	}
	
	private void drawPlainStatement(parser.Node node, Boolean withArrow) {
		// MVP
		if (withArrow) {
			UIUtil.drawArrow(gc, cur_x, cur_y, cur_x, cur_y + scale_y, true);
			cur_y += scale_y;
		}
		UIUtil.drawStatement(gc, node.getContent(), cur_x - this.scale_x / 2, cur_y, scale_x, scale_y);
		cur_y += scale_y;
		
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
//		// save file
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
		cur_x = 89;
		cur_y = 30;
		canvas = new ResizableCanvas();
		canvas.setWidth(weight);
		canvas.setHeight(height);
		this.gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(1);
		UIUtil.drawStart(gc, cur_x - this.scale_x / 2, cur_y, this.scale_x, this.scale_y);
		cur_y += this.scale_y; 
	}
	
}
