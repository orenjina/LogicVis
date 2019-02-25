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
import java.util.*;

enum Type {
    NONE, CONDITION, RETURN
}

public class parser {
    private CompilationUnit cu;

    public parser(String filePath) {
        File file = new File(filePath);
        try {
            cu = JavaParser.parse(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<MethodDeclaration> getAllMethods(){
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

    // throws NoSuchElementException when method is empty
    public Node traverse(String name) throws NoSuchElementException {
        MethodDeclaration method = getMethod(name);
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
                    // After a for loop ends, we won't be able to know where it points at until we encounter the next
                    // line. So we check the last end node type, if it is a condition, we know it was an else branch.
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
        } else { //normal statement
            Node next = new Node(Type.NONE, cur.toString());
            endNodes.add(next);
            return next;
        }
    }

    private Node ifOperations(IfStmt ifst, Set<Node> endNodes) {
//        System.out.println("Statement " + i + ": " + ifst.getCondition().toString());
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

    private Node forOPerations(ForStmt forst, Set<Node> endNodes) {
        NodeList<Expression> init = forst.getInitialization();
        List<Node> lastOne = new ArrayList<>();
        Node root = listExpOperations(init, lastOne);
        Node last = lastOne.get(lastOne.size()-1);
        Node cond = new Node(Type.CONDITION, forst.getCompare().get().toString());
        last.addChild(cond, ""); // added if condition
        // for operation always ends on the if condition
        endNodes.add(cond);
        Set<Node> tempEndNodes = new HashSet<>();
        Node trueBranch = oneStmtDispatch(forst.getBody(), tempEndNodes);
        cond.addChild(trueBranch, "True");
        lastOne.clear();
        Node update = listExpOperations(forst.getUpdate(), lastOne);
        last = lastOne.get(lastOne.size()-1);
        tempEndNodes.forEach(n -> n.addChild(update, ""));
        last.addChild(cond, "");
        return root;
    }

    public class Node {
        private String content;
        private Type t;
        private HashMap<Node, String> children;

        public Node (Type t, String content) {
            this.t = t;
            this.children = new HashMap<>();
            this.content = content;
        }

        public String getContent(){
            return content;
        }

        public Type getType(){
            return t;
        }

        public HashMap<Node, String> getChildren(){
            return (HashMap) children.clone();
        }

        public boolean addChild(Node child, String tag) {
            if (children.containsKey(child)) {
                return false;
            }
            children.put(child, tag);
            return true;
        }

//        @Override
//        public boolean equals(Object n2) {
//            if (this == n2) {
//                return true;
//            }
//            if (n2 == null || this.getClass() != n2.getClass()) {
//                return false;
//            }
//            Node n = (Node) n2;
//            return this.content.equals(n.content) && this.t==n.t && this.children.equals(n.children);
//        }
    }
}
