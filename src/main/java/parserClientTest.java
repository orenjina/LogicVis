import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import java.io.FileNotFoundException;
import java.util.*;

public class parserClientTest {

    public static void main(String[] args) {
        String testfile = "private int a() {\n" + 
        		"	for (int i = 0; i < 3; i ++) {\n" + 
        		"		int b = 2;\n" + 
        		"	}\n" + 
        		"	return 2;\n" + 
        		"}";
        parser par = new parser(testfile);
//        List<MethodDeclaration> methods = parser.getAllMethods();
//        methods.forEach(n -> System.out.println("Method Collected: " + n.getName()));
//        MethodDeclaration m = par.getMethod("recur");
        parser.Node root = par.traverse("a");
        printTree(root);
    }

    private static void printTree(parser.Node root) {
        System.out.println(root.getContent() + "has children: ");
        Map<parser.Node, String> children = root.getChildren();
        children.forEach((k, v) -> System.out.println("  " + k.getContent() + "," + v));
        children.forEach((k, v) -> printTree(k));
    }
}
