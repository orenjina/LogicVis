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
	 * @param: name, the name of parameter; value the value of the string
	 * Add a String value into params in a format "String n <-- value"
	 */
	public void addParam(String name, String value) {
		params.add("String " + name + " <-- " + value);
	}
	
	/*
	 * @param: name, the name of parameter; value the value of the integer
	 * Add a Integer value into params in a format "int n <-- value"
	 */	
	public void addParam(String name, int value) {
		params.add("int " + name + " <-- " + value);
	} 
	
	/*
	 * @param: name, the name of parameter; value the value of the double
	 * Add a double value into params in a format "double n <-- value"
	 */
	public void addParam(String name, double value) {
		params.add("double " + name + " <-- " + value);
	}
	
	/*
	 * @param: name, the name of parameter; value the value of the char
	 * Add a char value into params in a format "char n <-- value"
	 */
	public void addParams(String name, char value) {
		params.add("char " + name + " <-- " + value);
	}
	
	/*
	 * @param: name, the name of the parameter; value, the value of the float
	 * Add a float value into params in a format "float n <-- value"
	 */
	public void addParams(String name, float value) {
		params.add("float " + name + " <-- " + value);
	}
	
	/*
	 *  @param: value, the value of the returned String
	 *  Set the returned value
	 */
	public void addReturn(String value) {
		this.returnValue = value;
	}
	
	/*
	 *  @param: value, the value of the returned int
	 *  Set the returned value as a String
	 */
	public void addReturn(int value) {
		this.returnValue = Integer.toString(value);
	}

	/*
	 *  @param: value, the value of the returned double
	 *  Set the returned value as a String
	 */
	public void addReturn(double value) {
		this.returnValue = Double.toString(value);
	}
	
	/*
	 *  @param: value, the value of the returned float
	 *  Set the returned value as a String
	 */
	public void addReturn(float value) {
		this.returnValue = Float.toString(value);
	}
	
	/*
	 *  @param: value, the value of the returned char
	 *  Set the returned value as a String
	 */
	public void addReturn(char value) {
		this.returnValue = Character.toString(value);
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
