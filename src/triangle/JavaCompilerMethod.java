package triangle;

import java.io.File;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerMethod {

	public static void main(String[] args) {
		String root = System.getProperty("user.dir");
		String path1 = root + "/src/triangle/Triangle.java";
		String path2 = root + "/src/triangle/Main.java";
		File file1 = new File(path1);
		File file2 = new File(path2);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		String binPath = root + "/bin";
		String[] args1 = {"-d", binPath, file1.getPath()};
		String[] args2 = {"-d", binPath, file2.getPath()};
		compiler.run(null, null, null, args1);
		compiler.run(null, null, null, args2);
	}

}
