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
	Parser p = new Parser(emptyMethod);
	assertEquals(1, p.getAllMethods().size());
    }

    @Test
    public void testParseReturnMethod() {
	Parser p = new Parser(returnMethod);
	assertEquals(1, p.getAllMethods().size());
    }

    @Test
    public void testParseParameterMethod() {
	Parser p = new Parser(parameterMethod);
	assertEquals(1, p.getAllMethods().size());
    }

    @Test
    public void testParsePrivateMethod() {
	Parser p = new Parser(emptyMethod);
	assertEquals(1, p.getAllMethods().size());
    }

    @Test
    public void testParseMultipleMethod() {
	Parser p = new Parser(emptyMethod + returnMethod);
	assertEquals(2, p.getAllMethods().size());
    }

    @Test
    public void testParseRecursiveMethod() {
	Parser p = new Parser(recursiveMethod);
	assertEquals(1, p.getAllMethods().size());
    }


    /*
     * Method Tests
     */

    @Test
    public void testGetMethod() {
	Parser p = new Parser(recursiveMethod);
	assertEquals(1, p.getAllMethods().size());
	assertEquals(null, p.getMethod("helloworld"));
	if (p.getMethod("recursiveMethod") == null) {
	    fail("method6 not found in parser");
	}
    }

    @Test
    public void testEmptyTraverse() {
	Parser p = new Parser(emptyMethod);
	Parser.Node root = p.traverse("emptyMethod");
	assertEquals(null, root);
    }

    @Test
    public void testSimpleTraverse() {
	Parser p = new Parser(parameterMethod);
	Parser.Node root = p.traverse("parameterMethod");
	HashMap<Parser.Node, String> map = root.getChildren();
	assertEquals(0, map.size());
	assertEquals("x++", root.getContent());
    }

    @Test
    public void testRecursiveTraverse() {
	Parser p = new Parser(recursiveMethod);
	int size = 0;
	Queue<Parser.Node> queue = new LinkedList<Parser.Node>();
	Set<String> lines = new HashSet<String>();
	queue.add(p.traverse("recursiveMethod"));
	size++;
	boolean oneTrue = false;
	boolean oneFalse = false;
	do {
	    Parser.Node next = queue.remove();
	    lines.add(next.getContent());
	    HashMap<Parser.Node, String> map = next.getChildren();
	    for (Parser.Node n : map.keySet()) {
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
	assertEquals(4, size);
	assert lines.contains("x < 0");
	assert lines.contains("0");
	assert lines.contains("x + recursiveMethod(x - 1)");
    }

    @Test
    public void testForLoopTraverse() {
	Parser p = new Parser(forLoopMethod);
	int size = 0;
	Queue<Parser.Node> queue = new LinkedList<Parser.Node>();
	Set<String> lines = new HashSet<String>();
	queue.add(p.traverse("forLoopMethod"));
	size++;
	boolean oneTrue = false;
	do {
	    Parser.Node next = queue.remove();
	    lines.add(next.getContent());
	    HashMap<Parser.Node, String> map = next.getChildren();
	    for (Parser.Node n : map.keySet()) {
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
	assert lines.contains("int x");
	assert lines.contains("int i = 0");
	assert lines.contains("i < 5");
	assert lines.contains("i++");
	assert lines.contains("x++");
    }
}





