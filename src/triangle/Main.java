package triangle;

import java.util.Collection;

import killmap.ExtendedTestFinder;
import killmap.TestMethod;

/**
 * This program prints the current working directory.
 * This is a simple class used for testing. 
 * 
 * @author Raymond Tang
 *
 */
public class Main {

	public static void main(String[] args) {
		String pwd = System.getProperty("user.dir");
		System.out.println(pwd);
        Class<test.TriangleTest> c = test.TriangleTest.class;
        Collection<TestMethod> testMethods = null;
        try {
        	testMethods = ExtendedTestFinder.getTestMethods(c);
        } catch (Exception e) {
        	System.out.println("tough luck kid");
        	return;
        }
        for(TestMethod testMethod : testMethods) {
        	System.out.println(testMethod);
        }
	}

}
