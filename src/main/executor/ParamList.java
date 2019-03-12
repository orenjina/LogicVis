import java.util.ArrayList;
/*
 *  ParamList is a Linked list data structure that stores parameters for each depth as strings.
 */
public class ParamList{
	private ArrayList<String> params;		// An ArrayList that stores all parameters for this the current function call
	private int depth;			// depth indicates the depth of this node in recursion
	public ParamList next;			// Next node
	public String returnValue;
	/*
	 *Constructs a Paramlist and stores the depth information
	 */
	public ParamList(int depth) {
		this.params = new ArrayList<String>();
		this.depth = depth;
		this.next = null;
	}
	
	/*
	 * @param: name, the name of parameter; value the value of an Object
	 * Add a toString value into params in a format "String n <-- value"
	 */
	public void addParam(String name, Object value) {
		params.add(value.getClass().toString() + " " + name + " <-- " + value.toString());
	}
	
	/*
	 *  @param: value, the value of the returned Object
	 *  Set the returned value
	 */
	public void addReturn(Object value) {
		this.returnValue = value.toString();
	}
	
	
	// Return if the node has next node
	public boolean hasNext() {
		return this.next != null;
	}
	
	// Return the depth of this node
	public int getDepth() {
		return this.depth;
	}
	
	public String getReturnValue() {
		return returnValue;
	}
	
	// Return a copy of the parameter lists
	public ArrayList<String> getParams() {
		return (ArrayList<String>) this.params.clone();
	}
}
