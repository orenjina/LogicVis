import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
//import resrouces;

import java.io.File;
import java.io.FileNotFoundException;

public class testJavaParse {

    public static void main(String[] args) {
        try {
            File file = new File("src/main/resources/testCode.java");
            CompilationUnit cu = JavaParser.parse(file);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
