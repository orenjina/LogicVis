import com.github.javaparser.*;
import com.github.javaparser.ast.stmt.Statement;

public class testJavaParse {

    public static void main(String[] args) {
        Statement expression = JavaParser.parseStatement("int a = 0;");
    }

}
