import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import java.io.FileNotFoundException;
import java.util.*;

public class parserClientTest {

    public static void main(String[] args) {
        String testfile = "public int recur(int a) {\n" +
                "        a = a + 1;\n" +
                "        if (a == 1) {\n" +
                "            a++;\n" +
                "        } else if (a == 2) {\n" +
                "            return recur(a--) + recur(1);\n" +
                "        } else {\n" +
                "            recur(a);\n" +
                "        }\n" +
                "        if (this.recur(a+1) == 1)\n" +
                "            return 0;\n" +
                "        return a;\n" +
                "    }";
//        String testfile = "public int recur() {\n if ();\n}";
        parser par = new parser(testfile);
////        List<MethodDeclaration> methods = parser.getAllMethods();
////        methods.forEach(n -> System.out.println("Method Collected: " + n.getName()));
////        MethodDeclaration m = par.getMethod("recur");
////        parser.Node root = par.traverse("recur");
////        printTree(root);
//        par.getMethodCall("recur");
        par.traverseFirst();
//        for (int i = 0; i < 5; i++) {
//            System.out.println(par.getOrderOfRecurNode(i).getContent());
//        }
    }

//    private static void printTree(parser.Node root) {
//        System.out.println(root.getContent() + "has children: ");
//        Map<parser.Node, String> children = root.getChildren();
//        children.forEach((k, v) -> System.out.println("  " + k.getContent() + "," + v));
//        children.forEach((k, v) -> printTree(k));
//    }
}
