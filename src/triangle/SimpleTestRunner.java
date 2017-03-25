package triangle;

import java.util.Collection;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import killmap.ExtendedTestFinder;
import killmap.TestMethod;
import major.mutation.Config;

/**
 * This program runs JUnit tests in TriangleTest.java.
 * This is a simple class used for testing. 
 * 
 * @author Raymond Tang
 *
 */
public class SimpleTestRunner {

	public static void main(String[] args) {
		Class<?> c = null;
		try {
			c = Class.forName("test.TriangleTest");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		Collection<TestMethod> testMethods = null;
		try {
			testMethods = ExtendedTestFinder.getTestMethods(c);
		} catch (Exception e) {
			System.out.println("tough luck kid");
			return;
		}
		Config.__M_NO = 0;
		for(TestMethod testMethod : testMethods) {
			Result result = (new JUnitCore()).run(Request.method(testMethod.getTestClass(), testMethod.getName()));
			System.out.println(testMethod.getName() + " " + result.wasSuccessful());
		}
		List<Integer> coveredMutants = Config.getCoverageList();
		for(Integer mut : coveredMutants) {
			System.out.println(mut);
		}
		Config.reset();
	}

}
