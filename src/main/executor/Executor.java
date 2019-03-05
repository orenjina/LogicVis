// Import bean shell interpreter
import bsh.Interpreter;

import java.util.ArrayList;

/*
 * This is the Executor takes a piece of recursive java function code and test if the code is executable.
 * If so, constructs ParamList that stores the parameter values and depth for each recursive call.
 */
public class Executor {
	// A line of code that initialize the ParamList
	public final static String BUILDLIST = "ParamList root = new ParamList(-1)";
	// Set a pointer so that we can iterate through the paramlist
	public final static String BUILDPOINTER = "ParamList begin = root;";
	
	// The paramlist that stores the parameter values and depth in a String format "int i <-- 5"
	// for each recursive call.
	public ParamList list = null;
	// The starting parameter values, set by users.
	String args[];
	
	// Pass a Java code function as a String and the initial parameter values set by user.
	public Executor(String code, String args[]) {
		this.args = args;
		// Preprocess the code by injecting some lines into the original code
		Preprocessor p = new Preprocessor(code);
		if (p.preprocess()) {
			list = getList(p);
		}
	}
	
	// A helper function that execute the modified code from Preprocessor, if execution failed, 
	// it will print out the error message; otherwise, it will return a ParamList that stores
	// the parameter values and depth for each recursive call.
	private ParamList getList(Preprocessor p) {
		if (p.isEmpty()) {
			return null;
		}
		ParamList root;
		try{
			Interpreter interpreter = new Interpreter();
			StringBuilder functionCall = new StringBuilder();
			functionCall.append(p.functionName + "(0");
			for (int i = 0; i < args.length; i++) {
				functionCall.append(", " + args[i]);
			}
			functionCall.append(")");
			interpreter.eval(BUILDLIST);
			interpreter.eval(BUILDPOINTER);
			interpreter.eval(p.modifiedCode);
			interpreter.eval(functionCall.toString());
			root = (ParamList) interpreter.get("begin");
			return root.next;
		} catch(Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	// Print out the Paramlist in a format "Depth: 0 Parameters: int i <-- 5 ..."
	// Helps debug
	public void printList() {
		ParamList temp = list;
		if (temp != null) {
			System.out.println("Depth: " + temp.getDepth());
			printParams(temp);
		}
		while (temp.hasNext()) {
			temp = temp.next;
			System.out.println("Depth: " + temp.getDepth());
			printParams(temp);
		}
	}
	
	// A helper function that prints out the parameters
	private static void printParams(ParamList node) {
		ArrayList<String> params = node.getParams();
		System.out.println("Parameters: ");
		for(String i: params){
			System.out.println(i);
		}
	}

}
