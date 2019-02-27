import static org.junit.Assert.*;

import java.util.*;
import org.junit.Test;

public class ParserTests {

    private static final String emptyMethod = "void method1() {}";
    private static final String returnMethod = "int method3() { return 0; }";
    private static final String parameterMethod = "void method4(int x) { x++; }";
    private static final String privateMethod = "private void method5() { int a = 5; }";
    private static final String recursiveMethod = "int method6(int x) {"
	+ "if (x < 0) return 0; return x + method6(x - 1); }";


    /*
     * Constructor Tests
     */

    @Test
    public void testParseEmptyMethod() {
	parser p = new parser(emptyMethod);
	assertEquals(1, p.getAllMethods().size());
    }

    @Test
    public void testParseReturnMethod() {
	parser p = new parser(returnMethod);
	assertEquals(1, p.getAllMethods().size());
    }

    @Test
    public void testParseParameterMethod() {
	parser p = new parser(parameterMethod);
	assertEquals(1, p.getAllMethods().size());
    }

    @Test
    public void testParsePrivateMethod() {
	parser p = new parser(emptyMethod);
	assertEquals(1, p.getAllMethods().size());
    }

    @Test
    public void testParseMultipleMethod() {
	parser p = new parser(emptyMethod + returnMethod);
	assertEquals(2, p.getAllMethods().size());
    }

    @Test
    public void testParseRecursiveMethod() {
	parser p = new parser(recursiveMethod);
	assertEquals(1, p.getAllMethods().size());
    }


    /*
     * Method Tests
     */

    @Test
    public void testGetMethod() {
	parser p = new parser(recursiveMethod);
	assertEquals(1, p.getAllMethods().size());
	assertEquals(null, p.getMethod("helloworld"));
	if (p.getMethod("method6") == null) {
	    fail("method6 not found in parser");
	}
    }

    @Test
    public void testTraverse() {
	parser p = new parser(recursiveMethod);
	int size = 0;
	Queue<parser.Node> queue = new LinkedList<parser.Node>();
	queue.add(p.traverse("method6"));
	size++;
	boolean oneTrue = false;
	boolean oneFalse = false;
	do {
	    parser.Node next = queue.remove();
	    HashMap<parser.Node, String> map = next.getChildren();
	    for (parser.Node n : map.keySet()) {
	        String tag = map.get(n);
		if (tag.equals("True")) {
		    if (!oneTrue) {
			oneTrue = true;
		    } else {
			fail("More than one True tag found");
		    }
		} else if (tag.equals("False")) {
		    if (!oneFalse) {
			oneFalse = true;
		    } else {
			fail("More than one False tag found");
		    }
		}
		size++;
		queue.add(n);
	    }
	} while (!queue.isEmpty());
	if (!oneTrue) {
	    fail("No true tag found");
	}
	assertEquals(3, size);
    }


}





