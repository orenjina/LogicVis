import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

enum Type {
    // none is the general type without any special shapes
    NONE, CONDITION, RETURN
}

// parser class that currently takes in a file containing a class, which should contain the user
// input class.
public class parser {
    private CompilationUnit cu;
    private final String fileName = "methodFile.java";

    // takes in the user input method as one string
    public parser(String method) {
        try {
            PrintWriter out = new PrintWriter(fileName, "UTF-8");
            out.println("public class methodFile {");
            out.println(method);
            out.print("}");
            out.close();
            File file = new File(fileName);
            cu = JavaParser.parse(file);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public List<MethodDeclaration> getAllMethods() {
        List<MethodDeclaration> methods = new ArrayList<>();
        VoidVisitor<List<MethodDeclaration>> methodCollector = new parser.MethodCollector();
        methodCollector.visit(cu, methods);
        return methods;
    }

    public MethodDeclaration getMethod(String name) {
        List<MethodDeclaration> all = getAllMethods();
        for (MethodDeclaration method : all) {
            if (method.getName().asString().equals(name)) {
                return method;
            }
        }
        return null;
    }

    private static class MethodCollector extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
            super.visit(md, collector);
            collector.add(md);
        }
    }

    // traverse the method with the input name
    // returns null when method is empty
    public Node traverse(String name) {
        MethodDeclaration method = getMethod(name);
        if (method == null) {
            return null;
        }
        NodeList<Statement> stmts = method.getBody().get().getStatements();
        return listStmts(stmts, new HashSet<>());
    }

    // traverse the first method, which should be the user input method
    // returns null when method is empty
    public Node traverseFirst() {
        List<MethodDeclaration> methods = getAllMethods();
        if (methods == null || methods.size() == 0) {
            return null;
        }
        MethodDeclaration method = methods.get(0);
        if (method == null) {
            return null;
        }
        NodeList<Statement> stmts = method.getBody().get().getStatements();
        return listStmts(stmts, new HashSet<>());
    }

    // handles block statement
    private Node listStmts(NodeList<Statement> stmts, Set<Node> endNodes) {
        Node root = null;
        Set<Node> lastNodes = new HashSet<>();
        for (Statement stmt : stmts) {
            Set<Node> nextNodes = new HashSet<>();
            Node curNode = oneStmtDispatch(stmt, nextNodes);
            if (root == null) {
                root = curNode;
            } else {
                for (Node n : lastNodes) {
                    // After a for loop ends, we won't be able to know where it points at until
                    // we encounter the next line. So we check the last end node type, if it is a
                    // condition, we know it was an else branch.
                    if (n.getType() == Type.CONDITION) {
                        n.addChild(curNode, "False");
                    } else {
                        n.addChild(curNode, "");
                    }
                }
                lastNodes.clear();
            }
            lastNodes.addAll(nextNodes);
        }
        endNodes.addAll(lastNodes);
        return root;
    }

    private Node oneStmtDispatch(Statement cur, Set<Node> endNodes) {
        if (cur.isBlockStmt()) {
            return listStmts(cur.asBlockStmt().getStatements(), endNodes);
        } else if (cur.isIfStmt()) {
            IfStmt ifst = cur.asIfStmt();
            return ifOperations(ifst, endNodes);
        } else if (cur.isReturnStmt()) {
            Node next = new Node(Type.RETURN, cur.asReturnStmt().toString());
            endNodes.add(next);
            return next;
        } else if (cur.isForStmt()) {
            ForStmt forst = cur.asForStmt();
            return forOPerations(forst, endNodes);
        } else if (cur.isWhileStmt()) {
            WhileStmt whilest = cur.asWhileStmt();
            return whileOPerations(whilest, endNodes);
        } else { //normal statement
            Node next = new Node(Type.NONE, cur.toString());
            endNodes.add(next);
            return next;
        }
    }

    // if-elseif-else handler
    private Node ifOperations(IfStmt ifst, Set<Node> endNodes) {
        // takes care of the if condition check
        Node root = new Node(Type.CONDITION, ifst.getCondition().toString());
        Node trueBranch = oneStmtDispatch(ifst.getThenStmt(), endNodes);
        root.addChild(trueBranch, "True");
        if (ifst.hasElseBranch()) {
            Node falseBranch = oneStmtDispatch(ifst.getElseStmt().get(), endNodes);
            root.addChild(falseBranch, "False");
        }
        return root;
    }

    // list of expression handler
    private Node listExpOperations(NodeList<Expression> exp, List<Node> endNodes) {
        Node root = new Node(Type.NONE, exp.get(0).toString());
        Node last = root;
        for (int i = 1; i < exp.size(); i++) {
            Node temp = new Node(Type.NONE, exp.get(i).toString());
            last.addChild(temp, "");
            last = temp;
        }
        endNodes.add(last);
        return root;
    }

    // standard three arguments for loop handler
    private Node forOPerations(ForStmt forst, Set<Node> endNodes) {
        // INITIALIZATION
        NodeList<Expression> init = forst.getInitialization();
        List<Node> lastOne = new ArrayList<>();
        Node root = listExpOperations(init, lastOne);
        // assuming the initialization of a for loop should only contain one edge at the end
        Node last = lastOne.get(lastOne.size() - 1);

        // CONDITION
        Node cond = new Node(Type.CONDITION, forst.getCompare().get().toString());
        last.addChild(cond, ""); // added if condition
        // for operation always ends on the if condition
        endNodes.add(cond);

        // LOOP BODY
        Set<Node> tempEndNodes = new HashSet<>();
        Node trueBranch = oneStmtDispatch(forst.getBody(), tempEndNodes);
        cond.addChild(trueBranch, "True");

        // UPDATE
        lastOne.clear();
        Node update = listExpOperations(forst.getUpdate(), lastOne);
        // assuming the update of a for loop should only contain one edge at the end
        last = lastOne.get(lastOne.size() - 1);
        tempEndNodes.forEach(n -> n.addChild(update, ""));
        last.addChild(cond, "");
        return root;
    }

    // standard while loop handler (should handle all while loops since it doesn't have
    // formatting variations)
    private Node whileOPerations(WhileStmt whilest, Set<Node> endNodes) {
        Node cond = new Node(Type.CONDITION, whilest.getCondition().toString());
        // while operation always ends on the condition
        endNodes.add(cond);
        Set<Node> tempEndNodes = new HashSet<>();
        Node trueBranch = oneStmtDispatch(whilest.getBody(), tempEndNodes);
        cond.addChild(trueBranch, "True");
        tempEndNodes.forEach(n -> n.addChild(cond, ""));
        return cond;
    }

    // The official tree(implemented as a pure Node class) for the output tree structure
    public class Node {
        // Content is the string that should be shown in the shape. currently it just takes what
        // is given and show it directly with no modification (example: int x = 0; will be added
        // directly as content)
        private String content;

        // Type is an Enum (see top). Front end should be utilizing the type to build shapes
        // accordingly
        private Type t;

        // Maps child node to its edge label (example: if condition, labels could be "True" or
        // "False"). When no label needed, the value is set to be "".
        private HashMap<Node, String> children;

        public Node(Type t, String content) {
            this.t = t;
            this.children = new HashMap<>();
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public Type getType() {
            return t;
        }

        public HashMap<Node, String> getChildren() {
            return (HashMap<Node, String>) children.clone();
        }

        public boolean addChild(Node child, String tag) {
            if (children.containsKey(child)) {
                return false;
            }
            children.put(child, tag);
            return true;
        }
    }
}
