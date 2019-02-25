import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class parserClient {

    public static void main(String[] args) throws FileNotFoundException {
        String testfile = "src/main/resources/testCode.java";
        parser par = new parser(testfile);
//        List<MethodDeclaration> methods = parser.getAllMethods();
//        methods.forEach(n -> System.out.println("Method Collected: " + n.getName()));
//        MethodDeclaration m = par.getMethod("recur");
        parser.Node root = par.traverse("recur");
        printTree(root);
    }

    public static void printTree(parser.Node root) {
        System.out.println(root.getContent() + ": ");
        Map<parser.Node, String> children = root.getChildren();
        children.forEach((k,v) -> System.out.println("  " + k.getContent() + "," + v));
        children.forEach((k,v) -> printTree(k));
    }
}
