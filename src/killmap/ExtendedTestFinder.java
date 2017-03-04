package killmap;

import java.util.ArrayList;
import java.util.Collection;

public class ExtendedTestFinder extends TestFinder {

	public static Collection<TestMethod> getTestMethods(Class<?> clazz) {
		Collection<Class<?>> col = new ArrayList<Class<?>>();
        col.add(clazz);
        return TestFinder.getTestMethods(col);
	}
	
}
