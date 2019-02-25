import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

enum Type {
    NONE, CONDITION, RETURN;
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

    // throws NoSuchElementException when method is empty
    public Node traverse(String name) throws NoSuchElementException {
        MethodDeclaration method = getMethod(name);
        NodeList<Statement> stmts = method.getBody().get().getStatements();
        Set<Node> endNodes = new HashSet<>();
        Node root = listStmts(stmts, endNodes);
        return root;
    }

    // handles block statement
    private Node listStmts(NodeList<Statement> stmts, Set<Node> endNodes) {
        Node root = null;
        Set<Node> lastNodes = new HashSet<>();
        for (int i = 0; i < stmts.size(); i++) {
            Set<Node> nextNodes = new HashSet<>();
            Node curNode = oneStmtDispatch(stmts.get(i), nextNodes);
            if (root == null) {
                root = curNode;
            } else {
                lastNodes.forEach(n -> n.addChild(curNode, ""));
                lastNodes.clear();
            }
            nextNodes.forEach(n -> lastNodes.add(n));
        }
        lastNodes.forEach(n -> endNodes.add(n));
        return root;
    }

    private Node oneStmtDispatch(Statement cur, Set<Node> endNodes) {
        if (cur.isBlockStmt()) {
            return listStmts(cur.asBlockStmt().getStatements(), endNodes);
        } else if (cur.isIfStmt()) {
            IfStmt ifst = cur.asIfStmt();
            return ifOperations(ifst, endNodes);
        } else { //normal statement
            Node result = new Node(Type.NONE, cur.toString());
            endNodes.add(result);
            return result;
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
    }

    private static class MethodCollector extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
            super.visit(md, collector);
            collector.add(md);
        }
    }

}
