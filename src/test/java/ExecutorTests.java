import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ExecutorTests {
	public final static String FIBONACCI = "fibonacci.bsh";
	public final static String HCF = "src/test/java/hcf.bsh";
	
	public static void main(String[] args) {
		// testPreprocessorFib();
		// testExecutorFib();
		// testPreprocessorHCF();
		 testExecutorHCF();
	}
	
	public static void testPreprocessorHCF() {
		String code = readFile(HCF);
		Preprocessor p = new Preprocessor(code);
		if (p.preprocess()) {
			System.out.println("Preprocessed code is: ");
			System.out.println(p.modifiedCode);
			System.out.println("Parameters: ");
			System.out.println(p.getParameters());
		} else {
			System.out.println("Preprocess failed!");
		}		
	}

	public static void testPreprocessorFib(){
		String code = readFile(FIBONACCI);
		Preprocessor p = new Preprocessor(code);
		if (p.preprocess()) {
			System.out.println("Preprocessed code is: ");
			System.out.println(p.modifiedCode);
			System.out.println("Parameters: ");
			System.out.println(p.getParameters());
		} else {
			System.out.println("Preprocess failed!");
		}
	}
	
	public static void testExecutorHCF() {
		String code = readFile(HCF);
		String[] args = new String[2];
		args[0] = Integer.toString(10);
		args[1] = Integer.toString(11);
		Executor exe = new Executor(new Preprocessor(code), args);
		exe.printList();		
	}
	
	public static void testExecutorFib() {
		String code = readFile(FIBONACCI);
		String[] args = new String[1];
		args[0] = Integer.toString(6);
		Executor exe = new Executor(new Preprocessor(code), args);
		exe.printList();
	}
	
	
	/*
	 * @Param: String filePath, a path to the file that has Java function code
	 * Read the file and return the context of the file.
	 */
	public static String readFile(String filePath)
	{
	    StringBuilder contentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
	    {
	        stream.forEach(s -> contentBuilder.append(s).append("\n"));
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	    return contentBuilder.toString();
	}

}
