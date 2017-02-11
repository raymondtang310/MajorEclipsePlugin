package triangle;

/**
 * This program uses Runtime.getRuntime().exec to compile and run
 * two sample programs, Triangle and Main. 
 * 
 * @author Raymond Tang
 *
 */

public class ProcessMethod {

	public static void main(String[] args) {
		// Get the working directory
		String root = System.getProperty("user.dir");
		// src path
		String path = root + "/src/triangle/";
		// Program names
		String programName1 = "Triangle";
		String programName2 = "Main";
		String extension = ".java";
		// bin path
		String binPath = root + "/bin";
		try {
			// Compile programs
			Process p1 = Runtime.getRuntime().exec("javac -d " + binPath + " " + 
													path + programName1 + extension);
			p1.waitFor();
			Process p2 = Runtime.getRuntime().exec("javac -d " + binPath + " " + 
													path + programName2 + extension);
			p2.waitFor();
			// Run programs
			Process p3 = Runtime.getRuntime().exec("java -cp " + binPath + " " + 
													programName1);
			p3.waitFor();
			Process p4 = Runtime.getRuntime().exec("java -cp " + binPath + " " + 
													programName2);
			p4.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
