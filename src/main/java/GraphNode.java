import java.util.ArrayList; 

// A graph node that stores the information for a control flow graph. GraphNode is
// mainly used in the currentState field in ActionGenerator.
public class GraphNode {
	// If recursive functions in the current call have been returned,
	// functionCallReplace stores the returned value in a String form.
	// The index of arrayList indicates which function call needs to be
	// replaced by the corresponding value when we show the graph. e.g. if we
	// have a function recur(n) {return recur(n-1) + recur(n-2)} and functionCallReplace[0] = "boo";
	// This means, in the current graph, recur(n-1) has been fully explored and get a return
	// value "boo" but recur(n-2) has not been fully explored. Therefore, when showing the graph,
	// We replace recur(n-1) with "boo" in the graph
	public ArrayList<String> functionCallReplace;
	
	// A ParamList node that stores the parameters and return value of one function call
	private ParamList node;
	
	// Indicates which recursive function call from current function is calling next function call.
	// In the example recur(n) {return recur(n-1) + recur(n-2)}; connectedArrow == 0 means previous 
	// function call recur(n-1) called this depth, 1 means previous recur(n-2) called this depth
	public int highlightNode;

	
	// Constructs this GraphNode with a paramlistNode and an integer that shows which function
	// call it is connected to in the previous one
	public GraphNode(ParamList node, int highlightNode) {
		this.highlightNode = highlightNode;
		functionCallReplace = new ArrayList<String>();
		this.node = node;
	}
	
	// add a String to the functionCallReplace that indicates which function call
	// is replaced by what value. For example: recur(n - 1) replaced by 3
	public void replaceFunctionCall(String value, int index) {
		while (functionCallReplace.size() <= index) {
			functionCallReplace.add(null);
		}
		functionCallReplace.set(index, value);
	}
	
	// Get the return value from this step, null if there is no reture value
	public String getReturnValue() {
		if (node == null) {
			return null;
		}
		return node.returnValue;
	}
	
	// Get the parameter value from this call in an ArrayList of String
	public ArrayList<String> getParameters() {
		if (node == null) {
			return null;
		}
		return node.getParams();
	}
	
	// Get the depth of this call
	public int getDepth() {
		if (node == null) {
			return -1;
		}
		return node.getDepth();
	}
	
	// Print the information of this graphnode. Used in debug
	public void print() {
		ArrayList<String> parameters = getParameters();
		System.out.println("Parameters: ");
		for(int i = 0; i < parameters.size(); i++) {
			System.out.print(" " + parameters.get(i));
		}
		System.out.println("Function call replaced: ");
		for(int i = 0; i < functionCallReplace.size(); i++) {
			System.out.print(" " + functionCallReplace.get(i));
		}
		System.out.println("The Node connected to next graph: " + highlightNode);
	}
}
