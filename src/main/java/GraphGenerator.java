import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javafx.embed.swing.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

/*
 * Takes in a tree of parsed code and the parameters passed in and generates a flow chart
 */
public class GraphGenerator {
	double cur_x;
	double cur_y;
	double weight;
	double height;
	double scale_x = 80;
	double scale_y = 36;
	ResizableCanvas canvas;
	GraphicsContext gc;
	HashMap<Parser.Node, Pos> map;
	HashMap<Parser.Node, HashMap<Parser.Node, String>> graph;
	double range_x;
	double range_y;
	Parser.Node root;
	double latest_y;
	
	/*
	 * Default constructing graph generator
	 */
	public GraphGenerator(Parser.Node node, ArrayList<String> param) {
		this(960 * 0.32, 720 * 0.5, node, param);
	}
	
	/*
	 * Constructing graph generator with a different canvas size
	 */
	public GraphGenerator(double weight, double height, Parser.Node node, ArrayList<String> param) {
		cur_x = 89;
		cur_y = 30;
		canvas = new ResizableCanvas();
		canvas.setWidth(weight);
		canvas.setHeight(height);
		this.weight = weight;
		this.height = height;
		this.gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(1);
		map = new HashMap<Parser.Node, Pos>();
		graph = new HashMap<Parser.Node, HashMap<Parser.Node, String>>();
		this.root = node;
		UIUtil.drawStart(gc, cur_x - this.scale_x / 2, cur_y, this.scale_x, this.scale_y);
		cur_y += this.scale_y; 
		range_x = cur_x;
		range_y = cur_y;
		if (param != null) {
			for (String cur : param) {
				UIUtil.drawArrow(gc, cur_x, cur_y, cur_x, cur_y + scale_y, true);
				cur_y += this.scale_y; 
				UIUtil.drawParameter(gc, cur, cur_x - scale_x / 2, cur_y, scale_x, scale_y);
				cur_y += this.scale_y; 
				range_y = cur_y;
			}
		}
		latest_y = cur_y;
		construct();
	}
	
	/*
	 * Actually draw the nodes and edges on canvas
	 */
	private void draw(Parser.Node node) {
//		System.out.println(node.getContent());
		switch (node.getType()) {
			case CONDITION:
				drawConditionStatement(node);
				break;
			case RETURN:
				drawReturnStatement(node);
				break;
			case NONE:
				drawPlainStatement(node);
				break;
			default:
		}
	}
	
	// draw return statement
	private void drawReturnStatement(Parser.Node node) {
		// MVP
		Pos cur = map.get(node);
		UIUtil.drawRecurse(gc, node.getContent(), cur.x - this.scale_x / 2, cur.y, scale_x, scale_y);
	}
	
	// draw condition statement
	private void drawConditionStatement(Parser.Node node) {
		Pos cur = map.get(node);
		UIUtil.drawConditional(gc, node.getContent(), cur.x - scale_x / 2, cur.y, scale_x, scale_y);
	}
	
	// draw a normal statement
	private void drawPlainStatement(Parser.Node node) {
		// MVP
		Pos cur = map.get(node);
		UIUtil.drawStatement(gc, node.getContent(), cur.x - this.scale_x / 2, cur.y, scale_x, scale_y);
	}
	
	// link start to the first node
	private void drawStartToRoot() {
		Pos cur = map.get(root);
		UIUtil.drawArrow(gc, 89, latest_y, cur.x, cur.y, true);
	}
	
	// the method to call to draw the whole graph on canvas
	public void paint() {
		drawStartToRoot();
		
		for(Parser.Node node : map.keySet()) {
			draw(node);
		}
		
		for(Parser.Node node : graph.keySet()) {
			for (Parser.Node dst : graph.get(node).keySet()) {
				paintConnect(node, dst, graph.get(node).get(dst));
			}
		}
	}
	
	// draw edge
	private void paintConnect(Parser.Node src, Parser.Node dst, String statement) {
		Pos x = map.get(src);
		Pos y = map.get(dst);
		
		if (x.x == y.x) {
			if (x.y < y.y) {
				UIUtil.drawArrowDown(gc, statement, x.x, x.y + scale_y, y.y - x.y - scale_y);
			} else {
				UIUtil.drawArrowUp(gc, statement, y.x - scale_x / 2, y.y + scale_y / 2, x.x - scale_x / 2, x.y + scale_y / 2);
			}

		} else if (x.y == y.y) {
			if (x.x < y.x) {
				System.out.println("right");
				UIUtil.drawArrowRight(gc, statement, x.x + scale_x / 2, x.y + scale_y / 2, y.x - x.x - scale_x);
			} else {
				UIUtil.drawArrowLeft(gc, statement, x.x + scale_x / 2, x.y + scale_y / 2, x.x - y.x - scale_x);
			}
		} else {
			if (x.x < y.x) {
				System.out.println(1);
				UIUtil.drawLineArrowVertical(gc, statement, x.x, x.y + scale_y, y.x + scale_x / 2 , y.y + scale_y / 2);
			} else {
				System.out.println(2);
				UIUtil.drawLineArrowHorizontal(gc, statement, x.x, x.y + scale_y, y.x + scale_x / 2 , y.y + scale_y / 2);
			}
		}
	}
	
	// render the canvas in to an image
	public WritableImage renderImage() {
		WritableImage image = canvas.snapshot(null, null);
		
		// debug
		save(image);

		return image;
	}
	
	// save the image to chart.png
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
	
	// making the canvas resizable
	private class ResizableCanvas extends Canvas {
		@Override
		public boolean isResizable() {
			return true;
		}
	}
	
	// enpand the canvas when needed
	private void expand() {
		if (cur_y + 2 * scale_y > height || cur_x + 2 * scale_x > weight) {
			// expand
			System.out.println("expand");
			height *= 1.5;
			canvas.setHeight(height);
			System.out.println("height = " + height);
			System.out.println("expand");
			weight *= 1.5;
			canvas.setWidth(weight);
			System.out.println("weight = " + weight);
		}
	}
	
	// construct the whole graph structure
	private void construct() {
		cur_y += scale_y;
		range_y = cur_y;
		configure(root);
	}
	
	// find the true node among all children
	private Parser.Node getTrue(Map<Parser.Node, String> children) {
		for (Parser.Node cur : children.keySet()) {
			if (children.get(cur).equals("True")) {
				return cur;
			}
		}
		return null;
	}
	
	// find the false node among all children
	private Parser.Node getFalse(Map<Parser.Node, String> children) {
		for (Parser.Node cur : children.keySet()) {
			if (children.get(cur).equals("False")) {
				return cur;
			}
		}
		return null;
	}
	
	// configure the whole graph structure
	public void configure(Parser.Node node) {
		System.out.println(node.getContent() + " " + cur_x);
		expand();
		switch (node.getType()) {
			case CONDITION:
				configureConditionStatement(node);
				break;
			case RETURN:
				configureReturnStatement(node);
				break;
			case NONE:
				configurePlainStatement(node);
				break;
			default:
		}	

	}
	
	// configure return statement
	private void configureReturnStatement(Parser.Node node) {
		// TODO Auto-generated method stub
		configurePlainStatement(node);
	}

	// configure plain statement
	private void configurePlainStatement(Parser.Node node) {
		// TODO Auto-generated method stub
		map.put(node, new Pos(cur_x, cur_y));
		
		Map<Parser.Node, String> children = node.getChildren();
		for (Parser.Node cur : children.keySet()) {
			cur_y += 2 * scale_y;
			range_y = Math.max(cur_y, range_y);
			connect(node, cur, "");
			if (!map.containsKey(cur)) configure(cur);
		}
	}

	// configure conditional statement
	private void configureConditionStatement(Parser.Node node) {
		// TODO Auto-generated method stub
		map.put(node, new Pos(cur_x, cur_y));
		
		Map<Parser.Node, String> children = node.getChildren();
		Parser.Node not = getFalse(children);
		Parser.Node yes = getTrue(children);
		double temp_x = cur_x;
		double temp_y = cur_y;
		
		// handle true first
		cur_y += 2 * scale_y;
		range_y = Math.max(cur_y, range_y);
		connect(node, yes, "True");
		if (!map.containsKey(yes)) configure(yes);
		cur_x = temp_x;
		cur_y = temp_y;
		
		// handle false later
		if (not == null) {
			System.out.println("null");
		} else {
			cur_x = (3.0 / 2) * scale_x + range_x;
			range_x = cur_x;
			connect(node, not, "False");
			if (!map.containsKey(not)) configure(not);
		}
	}
	
	// add edges to graph
	private void connect(Parser.Node src, Parser.Node dst, String statement) {
		if (!graph.containsKey(src)) {
			graph.put(src, new HashMap<Parser.Node, String>());
		}
		graph.get(src).put(dst, statement);
	}

	// an object that stores the position of a node
	private class Pos {
		public double x;
		public double y;
		
		public Pos() {
			this.x = 0.0;
			this.y = 0.0;
		}
		
		public Pos(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
	
}
