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
	HashMap<parser.Node, Pos> map;
	HashMap<parser.Node, HashMap<parser.Node, String>> graph;
	double range_x;
	double range_y;
	parser.Node root;
	double latest_y;
	
	/*
	 * Default constructing graph generator
	 */
	public GraphGenerator(parser.Node node, ArrayList<String> param) {
		this(960 * 0.32, 720 * 0.5, node, param);
	}
	
	/*
	 * Constructing graph generator with a different canvas size
	 */
	public GraphGenerator(double weight, double height, parser.Node node, ArrayList<String> param) {
		cur_x = 89;
		cur_y = 30;
		canvas = new ResizableCanvas();
		canvas.setWidth(weight);
		canvas.setHeight(height);
		this.weight = weight;
		this.height = height;
		this.gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(1);
		map = new HashMap<parser.Node, Pos>();
		graph = new HashMap<parser.Node, HashMap<parser.Node, String>>();
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
	private void draw(parser.Node node) {
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
	private void drawReturnStatement(parser.Node node) {
		// MVP
		Pos cur = map.get(node);
		UIUtil.drawRecurse(gc, node.getContent(), cur.x - this.scale_x / 2, cur.y, scale_x, scale_y);
	}
	
	// draw condition statement
	private void drawConditionStatement(parser.Node node) {
		Pos cur = map.get(node);
		UIUtil.drawConditional(gc, node.getContent(), cur.x - scale_x / 2, cur.y, scale_x, scale_y);
	}
	
	// draw a normal statement
	private void drawPlainStatement(parser.Node node) {
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
		
		for(parser.Node node : map.keySet()) {
			draw(node);
		}
		
		for(parser.Node node : graph.keySet()) {
			for (parser.Node dst : graph.get(node).keySet()) {
				paintConnect(node, dst, graph.get(node).get(dst));
			}
		}
	}
	
	// draw edge
	private void paintConnect(parser.Node src, parser.Node dst, String statement) {
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

		if (cur_y + 2 * scale_y > height) {
			// expand vertically
			System.out.println("expand");
			canvas.setHeight(height * 2);
			height *= 2;
			System.out.println("height = " + height);
		}
		
		if (cur_x + 2 * scale_x > weight) {
			// expand horizontally
			System.out.println("expand");
			canvas.setWidth(weight * 2);
			weight *= 2;
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
	private parser.Node getTrue(Map<parser.Node, String> children) {
		for (parser.Node cur : children.keySet()) {
			if (children.get(cur).equals("True")) {
				return cur;
			}
		}
		return null;
	}
	
	// find the false node among all children
	private parser.Node getFalse(Map<parser.Node, String> children) {
		for (parser.Node cur : children.keySet()) {
			if (children.get(cur).equals("False")) {
				return cur;
			}
		}
		return null;
	}
	
	// configure the whole graph structure
	public void configure(parser.Node node) {
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
	private void configureReturnStatement(parser.Node node) {
		// TODO Auto-generated method stub
		configurePlainStatement(node);
	}

	// configure plain statement
	private void configurePlainStatement(parser.Node node) {
		// TODO Auto-generated method stub
		map.put(node, new Pos(cur_x, cur_y));
		
		Map<parser.Node, String> children = node.getChildren();
		for (parser.Node cur : children.keySet()) {
			cur_y += 2 * scale_y;
			range_y = Math.max(cur_y, range_y);
			connect(node, cur, "");
			if (!map.containsKey(cur)) configure(cur);
		}
	}

	// configure conditional statement
	private void configureConditionStatement(parser.Node node) {
		// TODO Auto-generated method stub
		map.put(node, new Pos(cur_x, cur_y));
		
		Map<parser.Node, String> children = node.getChildren();
		parser.Node not = getFalse(children);
		parser.Node yes = getTrue(children);
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
		cur_x = (3.0 / 2) * scale_x + range_x;
		range_x = cur_x;
		connect(node, not, "False");
		if (not == null) System.out.println("null");
		if (!map.containsKey(not)) configure(not);
	}
	
	// add edges to graph
	private void connect(parser.Node src, parser.Node dst, String statement) {
		if (!graph.containsKey(src)) {
			graph.put(src, new HashMap<parser.Node, String>());
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
