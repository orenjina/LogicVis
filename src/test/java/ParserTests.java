import static org.junit.Assert.*;

import java.util.*;
import org.junit.Test;

public class ParserTests {

    private static final String emptyMethod = "void emptyMethod() {}";
    private static final String returnMethod = "int returnMethod() { return 0; }";
    private static final String parameterMethod = "void parameterMethod(int x) { x++; }";
    private static final String privateMethod = "private void privateMethod() { int a = 5; }";
    private static final String recursiveMethod = "int recursiveMethod(int x) {"
	+ "if (x < 0) return 0; return x + recursiveMethod(x - 1); }";
    private static final String forLoopMethod = "void forLoopMethod() { int x; for (int i = 0; i < 5; i++) { x++; } }";

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
	if (p.getMethod("recursiveMethod") == null) {
	    fail("method6 not found in parser");
	}
    }

    @Test
    public void testEmptyTraverse() {
	parser p = new parser(emptyMethod);
	try {
	    parser.Node root = p.traverse("emptyMethod");
	    fail("NoSuchElementException should have been thrown");
	} catch (NoSuchElementException e) { }
    }

    @Test
    public void testSimpleTraverse() {
	parser p = new parser(parameterMethod);
	parser.Node root = p.traverse("parameterMethod");
	HashMap<parser.Node, String> map = root.getChildren();
	assertEquals(0, map.size());
	assertEquals("x++;", root.getContent());
    }

    @Test
    public void testRecursiveTraverse() {
	parser p = new parser(recursiveMethod);
	int size = 0;
	Queue<parser.Node> queue = new LinkedList<parser.Node>();
	Set<String> lines = new HashSet<String>();
	queue.add(p.traverse("recursiveMethod"));
	size++;
	boolean oneTrue = false;
	boolean oneFalse = false;
	do {
	    parser.Node next = queue.remove();
	    lines.add(next.getContent());
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
	assert lines.contains("x < 0");
	assert lines.contains("return 0;");
	assert lines.contains("return x + recursiveMethod(x - 1);");
    }

    @Test
    public void testForLoopTraverse() {
	parser p = new parser(forLoopMethod);
	int size = 0;
	Queue<parser.Node> queue = new LinkedList<parser.Node>();
	Set<String> lines = new HashSet<String>();
	queue.add(p.traverse("forLoopMethod"));
	size++;
	boolean oneTrue = false;
	do {
	    parser.Node next = queue.remove();
	    lines.add(next.getContent());
	    HashMap<parser.Node, String> map = next.getChildren();
	    for (parser.Node n : map.keySet()) {
	        String tag = map.get(n);
		if (tag.equals("True")) {
		    if (!oneTrue) {
			queue.add(n);
			oneTrue = true;
		    }
		} else {
		    queue.add(n);
		}
		size++;
	    }
	} while (!queue.isEmpty());
        assertEquals(5, lines.size());
	assert lines.contains("int x;");
	assert lines.contains("int i = 0");
	assert lines.contains("i < 5");
	assert lines.contains("i++");
	assert lines.contains("x++;");
    }
}





