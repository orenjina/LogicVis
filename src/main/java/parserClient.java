import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import java.io.FileNotFoundException;
import java.util.List;

public class parserClient {

    public static void main(String[] args) throws FileNotFoundException {
        String testfile = "src/main/resources/testCode.java";
        parser par = new parser(testfile);
//        List<MethodDeclaration> methods = parser.getAllMethods();
//        methods.forEach(n -> System.out.println("Method Collected: " + n.getName()));
//        MethodDeclaration m = par.getMethod("recur");
        par.traverse("recur");
    }
}
