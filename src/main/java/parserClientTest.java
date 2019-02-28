import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import java.io.FileNotFoundException;
import java.util.*;

public class parserClientTest {

    public static void main(String[] args) {
        String testfile = "    public int recur() {\n" +
                "        int a = 0;\n" +
                "        a = a + 1;\n" +
                "        if (a == 1) {\n" +
                "            a++;\n" +
                "        } else if (a == 2) {\n" +
                "            a--;\n" +
                "        } else {\n" +
                "            a = a + 3;\n" +
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
        System.out.println(root.getContent() + "has children: ");
        Map<parser.Node, String> children = root.getChildren();
        children.forEach((k, v) -> System.out.println("  " + k.getContent() + "," + v));
        children.forEach((k, v) -> printTree(k));
    }
}
