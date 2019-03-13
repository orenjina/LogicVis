import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * enum Type, NONE is the general type without any special shapes reflected on the GUI
 */
enum Type {
    NONE, CONDITION, RETURN
}

/**
 * parser is a class that takes in java methods (recursive or non recursive) and parse them into
 * a tree structure (Node) defined in this file
 */
public class parser {
    // head of the AST
    private CompilationUnit cu;

    // temporary storage of the method
    private final String fileName = "methodFile.java";

    // list containing all recursion nodes in the order of the code and number of appearances of
    // the node matches with the number of recursion calls in the line
    private List<Node> containRecurNode;

    /**
     * Constructor
     * @param method the entire user input method in a string
     */
    public parser(String method) {
        try {
            PrintWriter out = new PrintWriter(fileName, "UTF-8");
            out.println("public class methodFile {");
            out.println(method);
            out.print("}");
            out.close();
            File file = new File(fileName);
            cu = JavaParser.parse(file);
            containRecurNode = new ArrayList<>();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * get all methods in AST structure
     * @return a list of AST with MethodDeclaration as a root
     */
    public List<MethodDeclaration> getAllMethods() {
        List<MethodDeclaration> methods = new ArrayList<>();
        VoidVisitor<List<MethodDeclaration>> methodCollector = new parser.MethodCollector();
        methodCollector.visit(cu, methods);
        return methods;
    }

    /**
     * get the specific method with the given name in AST structure
     * @param name the user input method name as a string
     * @return MethodDeclaration(root) of the AST
     */
    public MethodDeclaration getMethod(String name) {
        List<MethodDeclaration> all = getAllMethods();
        for (MethodDeclaration method : all) {
            if (method.getName().asString().equals(name)) {
                return method;
            }
        }
        return null;
    }

    /**
     * traverse the method with the given name, build tree and output the root node of the tree
     * @param name the user input method name as a string
     * @return the root node of the tree generated from the method with the given name, null when
     * the method is empty or does not exist
     */
    public Node traverse(String name) {
        MethodDeclaration method = getMethod(name);
        if (method == null) {
            return null;
        }
        NodeList<Statement> stmts = method.getBody().get().getStatements();
        return listStmts(stmts, new HashSet<>());
    }

    /**
     * traverse the first method (in this implementation should be the user input method) , build
     * tree and output the root node of the tree
     * @return the root node of the tree generated from the first method, null when the method is
     * empty or does not exist
     */
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

    /**
     * outputs a list containing nodes with recursions in them
     * @return a list of nodes containing recursion calls in the order of appearing in the
     * original code, a node that contains multiple recursion calls which show up the same number
     * of times in the list returned
     * @precondition traverse or traverseFirst has to be called before this method gets called,
     * since the returned list reflects the recursion calls in the method which was just traversed
     */
    public List<Node> getRecurNodes() {
        List<Node> newList = new ArrayList<>(containRecurNode);
        return newList;
    }

    /**
     * outputs the first function name in the file
     * @return a string containing the first function name in the written file
     */
    public String getFirstFunctionName() {
        List<MethodDeclaration> methods = getAllMethods();
        if (methods == null || methods.size() == 0) {
            return null;
        }
        MethodDeclaration method = methods.get(0);
        if (method == null) {
            return null;
        }
        return method.getNameAsString();
    }

    /**
     * creates a subtree of a NodeList of Statements (processed block statements)
     * @param stmts a NodeList of statements
     * @param endNodes the leave nodes of this subtree
     * @return the root node of this subtree
     */
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

    /**
     * creates a subtree of a JavaParser Statement
     * @param cur a JavaParser Statement that will be contained in the subtree returned
     * @param endNodes the leave nodes of this subtree
     * @return the root node of this subtree
     */
    private Node oneStmtDispatch(Statement cur, Set<Node> endNodes) {
        if (cur.isBlockStmt()) {
            return listStmts(cur.asBlockStmt().getStatements(), endNodes);
        } else if (cur.isIfStmt()) {
            IfStmt ifst = cur.asIfStmt();
            return ifOperations(ifst, endNodes);
        } else if (cur.isReturnStmt()) {
            Node next = expOperations(Type.RETURN,
                    cur.asReturnStmt().getExpression().get());
            endNodes.add(next);
            return next;
        } else if (cur.isForStmt()) {
            ForStmt forst = cur.asForStmt();
            return forOPerations(forst, endNodes);
        } else if (cur.isWhileStmt()) {
            WhileStmt whilest = cur.asWhileStmt();
            return whileOPerations(whilest, endNodes);
        } else if (cur.isExpressionStmt()) {
            ExpressionStmt expst = cur.asExpressionStmt();
            return expstmtOperations(expst, endNodes);
        } else { //normal statement
            Node next = new Node(Type.NONE, cur.toString());
            endNodes.add(next);
            return next;
        }
    }

    /**
     * creates a subtree of a JavaParser ExpressionStmt
     * @param expst a JavaParser ExpressionStmt that will be contained in the subtree returned
     * @param endNodes the leave nodes of this subtree
     * @return the root node of this subtree
     */
    private Node expstmtOperations(ExpressionStmt expst, Set<Node> endNodes) {
        Expression exp = expst.getExpression();
        Node root = expOperations(Type.NONE, exp);
        endNodes.add(root);
        return root;
    }

    /**
     * creates a subtree of a JavaParser IfStmt
     * @param ifst a JavaParser IfStmt that will be contained in the subtree returned
     * @param endNodes the leave nodes of this subtree
     * @return the root node of this subtree
     */
    private Node ifOperations(IfStmt ifst, Set<Node> endNodes) {
        // takes care of the if condition check
        Node root = expOperations(Type.CONDITION, ifst.getCondition());
        Node trueBranch = oneStmtDispatch(ifst.getThenStmt(), endNodes);
        root.addChild(trueBranch, "True");
        if (ifst.hasElseBranch()) {
            Node falseBranch = oneStmtDispatch(ifst.getElseStmt().get(), endNodes);
            root.addChild(falseBranch, "False");
        } else {
            endNodes.add(root);
        }
        return root;
    }

    /**
     * creates a node for a JavaParser Expression, updates the containRecurNode field
     * @param t a Type that will be the Type of the node created
     * @param ex a JavaParser Expression that is parsed to be this node
     * @return the node of this Expression
     */
    private Node expOperations(Type t, Expression ex) {
        Node n = new Node(t, ex.toString());
        List<MethodCallExpr> allMethodCall = new ArrayList<>();
        VoidVisitor<List<MethodCallExpr>> expVisitor = new parser.ExpressionVisitor();
        MethodCallExpr expst = new MethodCallExpr(null, getFirstFunctionName()+"wrapper",
                new NodeList<>(ex));
        expVisitor.visit(expst, allMethodCall);
        for (MethodCallExpr e : allMethodCall) {
                if (e.getScope().isEmpty() || e.getScope().get().toString().equals(
                        "this")) {
                    if (e.getName().asString().equals(getFirstFunctionName())) {
                        containRecurNode.add(n);
                    }
                }
        }
        return n;
    }

    /**
     * creates a subtree of a JavaParser NodeList of Expression
     * @param exp a JavaParser NodeList of Expression that will be contained in the subtree returned
     * @param endNodes the leave nodes of this subtree
     * @return the root node of this subtree
     */
    private Node listExpOperations(NodeList<Expression> exp, List<Node> endNodes) {
        Node root = expOperations(Type.NONE, exp.get(0));
        Node last = root;
        for (int i = 1; i < exp.size(); i++) {
            Node temp = expOperations(Type.NONE, exp.get(i));
            last.addChild(temp, "");
            last = temp;
        }
        endNodes.add(last);
        return root;
    }

    /**
     * creates a subtree of a JavaParser ForStmt
     * @param forst a JavaParser ForStmt that will be contained in the subtree returned
     * @param endNodes the leave nodes of this subtree
     * @return the root node of this subtree
     */
    private Node forOPerations(ForStmt forst, Set<Node> endNodes) {
        // INITIALIZATION
        NodeList<Expression> init = forst.getInitialization();
        List<Node> lastOne = new ArrayList<>();
        Node root = listExpOperations(init, lastOne);
        // assuming the initialization of a for loop should only contain one edge at the end
        Node last = lastOne.get(lastOne.size() - 1);

        // CONDITION
        Node cond = expOperations(Type.CONDITION, forst.getCompare().get());
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

    /**
     * creates a subtree of a JavaParser WhileStmt
     * @param whilest a JavaParser WhileStmt that will be contained in the subtree returned
     * @param endNodes the leave nodes of this subtree
     * @return the root node of this subtree
     */
    private Node whileOPerations(WhileStmt whilest, Set<Node> endNodes) {
        Node cond = expOperations(Type.CONDITION, whilest.getCondition());
        // while operation always ends on the condition
        endNodes.add(cond);
        Set<Node> tempEndNodes = new HashSet<>();
        Node trueBranch = oneStmtDispatch(whilest.getBody(), tempEndNodes);
        cond.addChild(trueBranch, "True");
        tempEndNodes.forEach(n -> n.addChild(cond, ""));
        return cond;
    }

    /**
     * Overrides the original MethodDeclaration visit, this method visits all MethodDeclaration 
     * nodes in the given MethodDeclaration as well as itself and add it into the list collector
     */
    private static class MethodCollector extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
            super.visit(md, collector);
            collector.add(md);
        }
    }

    /**
     * Overrides the original MethodCallExpr visit, this method visits all MethodCallExpr 
     * nodes in the given MethodCallExpr as well as itself and add it into the list collector
     */
    private static class ExpressionVisitor extends VoidVisitorAdapter<List<MethodCallExpr>> {
        @Override
        public void visit(MethodCallExpr exp, List<MethodCallExpr> collector) {
            // Found a method call
            // Don't forget to call super, it may find more method calls inside the arguments of this method call, for example.
            super.visit(exp, collector);
            collector.add(exp);
        }
    }

    /**
     * Node class that contains all the information from the original code base
     */
    public class Node {
        // Content is the string that is shown in the shape. It contains the code from the line
        // that is corresponding to this node
        private String content;

        // Type is an Enum (see top). Front end should be utilizing the type to build shapes
        // accordingly
        private Type t;

        // Maps child node to its edge label (example: if condition, labels could be "True" or
        // "False"). When no label needed, the value is set to be an empty string
        private HashMap<Node, String> children;

        /**
         * constructor
         * @param t enum Type, possible values are NONE, CONDITION, and RETURN
         * @param content a string containing the code, which is displayed in the shape in GUI
         */
        public Node(Type t, String content) {
            this.t = t;
            this.children = new HashMap<>();
            this.content = content.trim();
            if (this.content.endsWith(";"))
                this.content = this.content.substring(0, this.content.length()-1);
        }

        /**
         * get the content of the node
         * @return a string containing the code, which is displayed in the shape in GUI
         */
        public String getContent() {
            return content;
        }

        /**
         * get the type of the node
         * @return enum Type, possible values are NONE, CONDITION, and RETURN
         */
        public Type getType() {
            return t;
        }

        /**
         * get the children with the tag on the arrows
         * @return a HashMap<Node, String> that maps the child node to the label on the arrow
         * pointing from the parent to the child
         */
        public HashMap<Node, String> getChildren() {
            return (HashMap<Node, String>) children.clone();
        }

        /**
         * add a child node to this node
         * @param child the child node that is being added
         * @param tag a string containing the label on the arrow pointing from the parent to the
         *            child
         * @return true when the child does not already exist and sucessfully got added to the
         * parent, false when the child already exists
         */
        public boolean addChild(Node child, String tag) {
            if (children.containsKey(child)) {
                return false;
            }
            children.put(child, tag);
            return true;
        }
    }
}
