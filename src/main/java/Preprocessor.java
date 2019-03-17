import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
/*
 * A helper that processes a piece of Java function finds the parameter types and names,
 * injects several lines that are necessary for Executor.
 * For example:
 * Original code:
 * int fibonacci(int n) {

	if(n == 0) {
		return 0;
	}
	if(n == 1) {
		return 1;
	}
	return fibonacci(n - 1) + fibonacci(n - 2);
}
 *
 *
 * Modified code:

int fibonacci(int depTH, int n) {
 root.next = new ParamList(depTH); 
 root = root.next; 
 root.addParam("n", n); 
ParamList curDEPTH = root;

	if(n == 0) {
		int returnVALUE = 0;
curDEPTH.addReturn(returnVALUE);
return returnVALUE;
	}
	if(n == 1) {
		int returnVALUE = 1;
curDEPTH.addReturn(returnVALUE);
return returnVALUE;
	}
	int returnVALUE = fibonacci(depTH + 1, n - 1) + fibonacci(depTH + 1, n - 2);
curDEPTH.addReturn(returnVALUE);
return returnVALUE;
}

 */
public class Preprocessor {
	public String code;		// The original code
	public String modifiedCode;		// The modified code after processing
	public String functionName;		// The name of this function call
	public String returnType;
	public String[] parameterTypes;		// The types of all parameters
	public String[] parameterNames;		// The names of all parameters
	
	// @Param: code, a java function source code
	// Constructs the Preprocessor with the original code
	public Preprocessor(String code) {
		this.code = code;
	}
		
	// @returns how many parameters do we have in the function code
	public int getParamNumber() {
		if (parameterTypes == null || parameterNames == null || parameterTypes != parameterNames) {
			return 0;
		} else {
			return parameterTypes.length;
		}
	}
	
	// Preprocess the code. If the original code follows standard java format, this method
	// will store the parameters' types and names of and inject some necessary lines for executor
	// to process. Return true if preprocessing succeeds, otherwise false.
	public boolean preprocess() {
		// Look for the first parentheses, so that we can get the parameter information from this
		// function
		int left = this.code.indexOf('(');
		int right = this.code.indexOf(')');
		// If there is no "()" in the code, return false
		if (left <= 0 || right <= 0) {
			return false;
		}
		String[] title = code.substring(0, left).trim().split(" ");
		// Get and store the function name
		if (title.length < 2) {
			return false;
		}
		this.functionName = title[title.length - 1].trim();
		this.returnType = title[title.length - 2].trim();
		String param = code.substring(left + 1, right);
		String[] params = param.split(",");
		int num = params.length;
		this.parameterTypes = new String[num];
		this.parameterNames = new String[num];
		// Store all the parameters' types and names
		for (int i = 0; i < num; i++) {
			String[] splits = params[i].trim().split(" ");
			if (splits.length == 2) {
				this.parameterTypes[i] = splits[0];
				this.parameterNames[i] = splits[1];
			}
		}
		// return if modifyCode succeeds or not
		return modifyCode();
	}
	
	// Modify the code by injecting necessary lines and parameters for executor.
	// Return true if succeeds; false otherwise.
	private boolean modifyCode(){
		StringBuilder sb = new StringBuilder();
		// get the Code with a parameter that stores depth.
		String codeWithDepth = addDepthParam(code, functionName);
		
		// If the method type is not void, inject code to extract return value of each function call
		if (!this.returnType.toLowerCase().equals("void")) {
			codeWithDepth = modifyReturn(codeWithDepth, returnType);
		}
		int start = codeWithDepth.indexOf('{');
		// If the code does not contain '{', return false
		if (start == -1) {
			return false;
		}
		// Inject code necessary for executor
		String injected = "\n ROOT.next = new ParamList(depTH, callFromLAST); \n ROOT = ROOT.next; \n";
		for (int i = 0; i < parameterNames.length; i++) {
			injected += " ROOT.addParam(\"" + this.parameterNames[i] +"\", " + this.parameterNames[i] +"); \n";
		}
		injected += "ParamList curDEPTH = ROOT;\n";
		sb.append(codeWithDepth.substring(0, start + 1));
		sb.append(injected);
		sb.append(codeWithDepth.substring(start + 1));
		this.modifiedCode = sb.toString();
		return true;
	}
	
	/*
	 *  A helper function that takes a java function code and the name of this function code
	 *  return a modifiedjava function code that has one more parameter "int depTH".
	 */
	private String addDepthParam(String code, String name) {
		StringBuilder sb = new StringBuilder();
		int i = code.indexOf(name);
		if (i == -1) {
			return null;
		}
		i += name.length();
		while(code.charAt(i) == ' ') {
			i++;
		}
		i++;
		sb.append(code.substring(0, i));
		sb.append("int depTH, ");
		sb.append("int callFromLAST, ");
		// Add "depTH + 1" to all the recursive call
		int recurNum = 0;
		while(code.indexOf(name, i) != -1) {
			int j = code.indexOf(name, i);
			j += name.length();
			while (code.charAt(j) == ' ') {
				j++;
			}
			if (code.charAt(j) != '(') {
				continue;
			} else {
				sb.append(code.substring(i, j + 1));
				sb.append("depTH + 1, ");
				sb.append(recurNum + ", ");
				recurNum++;
			}
			i = j + 1;
		}
		sb.append(code.substring(i));
		return sb.toString();
	}
	
	/*
	 * A helper function that takes a String of Java code and the type of the return value,
	 * inject a few lines before all the "return" so that we can store the return value into the
	 * ParamList node.
	 */
	private String modifyReturn(String code, String type) {
		StringBuilder sb = new StringBuilder();
		int i = code.indexOf("return");
		if (i == -1) {
			return code;
		}
		int semi = -1;
		while (code.indexOf("return", semi) != -1) {
			i = code.indexOf("return", semi);
			sb.append(code.substring(semi + 1, i));
			semi = code.indexOf(";", i);
			String returnExpr = code.substring(i + "return".length(), semi).trim();
			String injectedCode = type + " " + "returnVALUE = " + returnExpr + ";\n";
			injectedCode += "curDEPTH.addReturn(returnVALUE);\nreturn returnVALUE;"; 
			sb.append(injectedCode);
		}
		sb.append(code.substring(semi + 1, code.length() - 1));
		return sb.toString();
	}
	
	/*
	 * Return a string representation of all parameter types and names. For example
	 * int a = 1, char b = b
	 */
	public String getParameters(){
		if (this.parameterNames == null || this.parameterTypes == null 
				|| this.parameterNames.length != this.parameterTypes.length){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parameterNames.length; i++) {
			sb.append(parameterTypes[i] + " " + parameterNames[i] +", ");
		}
		return sb.toString();
	}
	
	// Return whether the original code has been successfully processed
	public boolean isEmpty() {
		return this.code == null || this.functionName == null || this.modifiedCode == null;
	}
	
}
