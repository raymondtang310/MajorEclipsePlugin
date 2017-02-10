package triangle;

import java.io.File;

public class Mutator {

	public static void main(String[] args) {
		//String pwd = args[0];
		//String pwd = System.getProperty("user.dir");
		String root = "/home/raymond/workspace/org.rayzor.mutant";
		String majorOutputPath = root + "/mutatedBin";
		String path1 = root + "/src/triangle/Triangle.java";
		String command = "/home/raymond/major/bin/major ";
		String arg0 = "-d " + majorOutputPath + " ";
		String arg1 = "-J-Dmajor.export.mutants=true ";
		String arg2 = "-J-Dmajor.export.directory=" + root + "/mutants ";
		String arg3 = "-XMutator:ALL ";
		File majorDirectory = new File(majorOutputPath);
		majorDirectory.mkdir();
		try {
			Process p1 = Runtime.getRuntime().exec(command + arg0 + arg1 + arg2 + arg3 + path1);
			p1.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
