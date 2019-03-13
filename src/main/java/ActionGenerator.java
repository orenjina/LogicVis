
import java.util.ArrayList;
import java.util.LinkedList;

import bsh.Interpreter;

/*
 * ActionGenerator generates a list of GraphNode that shows the number of graph 
 * in the current state and the parameter values in each existing graph
 */
public class ActionGenerator {
	// The paramList generated by executor
	public ParamList list;
	// If there is an error occured, update erroe message
	public String errorMessage;
	// The preprocessed code
	private Preprocessor preprocessor;
	
	// A list of GraphNode that shows the current state of the graph
	public ArrayList<GraphNode> currentState;
	
	// Pass in a piece of Javacode, preprocess it and constructs an ActionGenerator
	public ActionGenerator(String code) {
		Preprocessor p = new Preprocessor(code);
		if (!p.preprocess()) {
			errorMessage = "Please type in valid Java code";
		} else {
			this.preprocessor = p;
			currentState = new ArrayList<GraphNode>();
		}
	}
	
	// Return an array of parameter types of this method
	public String[] getParameterTypes(){
		return this.preprocessor.parameterTypes;
	}
	
	// Return an array of parameter names of this method
	public String[] getParameterNames(){
		return this.preprocessor.parameterNames;
	}
	
	// Pass in an array of String that shows the value of each 
	// inputs
	public void execute(String[] args){
		String[] paramTypes = getParameterTypes();
		if(paramTypes.length != args.length) {
			errorMessage = "Invalid Input";
		}
		// Check if the input value matches the parameter types,
		// if not, set error message to Invalid Input
		for (int i = 0; i < args.length; i++) {
			if (!checkValidType(paramTypes[i], args[i])) {
				errorMessage = "Invalid Input";
				return;
			}
		}
		Executor exe = new Executor(preprocessor, args);
		list = exe.list;
		if(list == null) {
			// If execution failed, set the error message to
			// execution failed.
			errorMessage = "Execution failed";
		}
		currentState.add(new GraphNode(list, -1));
	}
	
	// Helper function that checks if the value in its String form matches the type
	private boolean checkValidType(String type, String value) {
		String codeLine = type + " test = " + value;
		Interpreter interpreter = new Interpreter();
		try{
			interpreter.eval(codeLine);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	// Every time, the user calls next, update the current State
	public void next() {
		ParamList next = list.next;
		GraphNode last = currentState.get(currentState.size() - 1);
		if (next != null) {
			if (next.getDepth() > last.getDepth()) {
				last.currentChildren += 1;
				currentState.add(new GraphNode(next, last.currentChildren));
				list = list.next;
			} else {
				removeCurrentState();
			}
		} else {
			if (!isDone()) {
				removeCurrentState();
			}
		}
	}
	
	// Check if there is no more depth can be explored in the input recursion function
	private boolean isDone() {
		return currentState.size() == 0;
	}
	
	// Helper function that remove a node in the currentState and update the returned value in the
	// previous node
	private void removeCurrentState() {
		GraphNode remove = currentState.remove(currentState.size() - 1);
		currentState.get(currentState.size() - 1).replaceFunctionCall(remove.getReturnValue());
	}
	
	// Return currentState
	public ArrayList<GraphNode> getCurrentState () {
		return (ArrayList<GraphNode>)currentState.clone();
	}
	
	// Print out the current state, used in debug.
	public void printCurrentState() {
		ArrayList<GraphNode> curr = getCurrentState();
		for (int i = 0; i < curr.size(); i++) {
			System.out.println("Node " + (i + 1));
			curr.get(i).print();
		}
	}	

}