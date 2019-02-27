import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import java.io.FileNotFoundException;
import java.util.*;

public class parserClient {

    public static void main(String[] args) throws FileNotFoundException {
        String testfile = "public int recur() {\n" +
                "        int a = 5;\n" +
                "        a = a + 1;\n" +
                "        for (int i = 0; i < 5; i++) {\n" +
                "            a++;\n" +
                "        }\n" +
                "        return a;\n" +
                "    }";
        parser par = new parser(testfile);
//        List<MethodDeclaration> methods = parser.getAllMethods();
//        methods.forEach(n -> System.out.println("Method Collected: " + n.getName()));
//        MethodDeclaration m = par.getMethod("recur");
        parser.Node root = par.traverse("recur");
        printTree(root);
    }

    private static void printTree(parser.Node root) {
        System.out.println(root.getContent() + ": ");
        Map<parser.Node, String> children = root.getChildren();
        children.forEach((k, v) -> System.out.println("  " + k.getContent() + "," + v));
        children.forEach((k, v) -> printTree(k));
    }
}
