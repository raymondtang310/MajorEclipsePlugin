package triangle;

public class ProcessMethod {

	public static void main(String[] args) {
		String root = System.getProperty("user.dir");
		String path1 = root + "/src/triangle/Triangle.java";
		String path2 = root + "/src/triangle/Main.java";
		String binPath = root + "/bin";
		try {
			Process p1 = Runtime.getRuntime().exec("javac -d " + binPath + " " + path1);
			p1.waitFor();
			Process p2 = Runtime.getRuntime().exec("javac -d " + binPath + " " + path2);
			p2.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
