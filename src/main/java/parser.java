import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;

public class parser {
    CompilationUnit cu;

    public parser(String filePath) {
        File file = new File(filePath);
        try {
            CompilationUnit cu = JavaParser.parse(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void traverse(){

    }

    //for test
//    public static void main(String[] args) throws FileNotFoundException {
//    }

//    private static class LinePrinter extends TreeVisitor {
//
//        @Override
//        public void visitPreOrder(Node cu){
//            super.visitPreOrder(cu);
//            System.out.println("Statement Name Printed: " );
//        }
//    }
}
