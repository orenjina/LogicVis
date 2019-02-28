import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	double scale_x = 61;
	double scale_y = 36;
	ResizableCanvas canvas;
	GraphicsContext gc;
	
	public GraphGenerator() {
		this(960 * 0.32, 720 * 0.5);
	}
	
	public GraphGenerator(double weight, double height) {
		cur_x = weight * 0.5;
		cur_y = height * 0.1;
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
	
	public void draw(String statements) {
		// MVP
		UIUtil.drawArrow(gc, cur_x, cur_y, cur_x, cur_y + scale_y);
		cur_y += scale_y;
		UIUtil.drawStatement(gc, statements, cur_x - this.scale_x / 2, cur_y, scale_x, scale_y);
		cur_y += scale_y;
	}
	
	public WritableImage renderImage() {
		WritableImage image = canvas.snapshot(null, null);
		File file = new File("chart.png");
		BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
	    try {
	        ImageIO.write(bImage, "png", file);
	    } catch (IOException e) {
	        // TODO: handle exception here
	    		e.printStackTrace();
	    }
	    return image;
	}
	
	private class ResizableCanvas extends Canvas {
		@Override
		public boolean isResizable() {
			return true;
		}
	}
	
}
