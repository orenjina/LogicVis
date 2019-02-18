import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class parser {
    private static CompilationUnit cu;

    public parser(String filePath) {
        File file = new File(filePath);
        try {
            cu = JavaParser.parse(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<MethodDeclaration> getAllMethods(){
        List<MethodDeclaration> methods = new ArrayList<>();
        VoidVisitor<List<MethodDeclaration>> methodCollector = new parser.MethodCollector();
        methodCollector.visit(cu, methods);
        return methods;
    }

    public static MethodDeclaration getMethod(String name) {
        List<MethodDeclaration> all = getAllMethods();
        for (MethodDeclaration method : all) {
            if (method.getName().asString().equals(name)) {
                return method;
            }
        }
        return null;
    }

    // throws NoSuchElementException when method is empty
    public static void traverse(String name) throws NoSuchElementException {
        MethodDeclaration method = getMethod(name);
        NodeList<Statement> stmts = method.getBody().get().getStatements();
        for (int i = 0; i < stmts.size(); i++) {
            System.out.println("Statement " + i + ": " + stmts.get(i));
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
