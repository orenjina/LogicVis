import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class test {
	public final static String FIBONACCI = "fibonacci.bsh";
	public final static String HCF = "hcf.bsh";
	
	public static void main(String[] args) {
		testPreprocessorFib();
		// testExecutorFib();
		// testPreprocessorHCF();
		// testExecutorHCF();
		// testActionFib();
		//testActionHCF();
	}
	
	public static void testActionFib() {
		String code = readFile(FIBONACCI);
		String[] args = new String[1];
		args[0] = Integer.toString(3);
		ActionGenerator action = new ActionGenerator(code);
		action.execute(args);
		action.next();
		action.next();
		action.next();
		action.next();
		action.next();
		action.printCurrentState();
	}
	
	public static void testActionHCF() {
		String code = readFile(HCF);
		String[] args = new String[2];
		args[0] = Integer.toString(10);
		args[1] = Integer.toString(12);
		ActionGenerator action = new ActionGenerator(code);
		action.execute(args);
		action.next();
		action.next();
		action.next();
		action.printCurrentState();
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
		Preprocessor p = new Preprocessor(code);
		if (p.preprocess()) {
			Executor exe = new Executor(p, args);
			exe.printList();		
		}
	}
	
	public static void testExecutorFib() {
		String code = readFile(FIBONACCI);
		String[] args = new String[1];
		args[0] = Integer.toString(6);
		Preprocessor p = new Preprocessor(code);
		if (p.preprocess()) {
			Executor exe = new Executor(p, args);
			exe.printList();
		}
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
