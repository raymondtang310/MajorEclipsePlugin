
package analyzer;

/**
 * A TestMethod object represents a JUnit test. This stores information on a test method's name and test class. 
 * 
 * Taken from Rene Just's fault-localization-data
 * Link: https://bitbucket.org/rjust/fault-localization-data/src/1b5b3f155c41ef4d7c11ca422a94e5ff90b558e7/killmap/src/main/killmap/TestMethod.java?at=icse17&fileviewer=file-view-default
 * Tag: icse17
 * 
 * @author Raymond Tang
 *
 */
public class TestMethod implements Comparable<TestMethod> {
  // The class which contains this test method
  private final Class<?> testClass;
  // The name of this test method
  private final String name;

  private static final char SEPARATOR = '#';

  public TestMethod(Class<?> testClass, String name) {
    this.testClass = testClass;
    this.name = name;
  }

  /**
   * Returns the test class containing this test method
   * 
   * @return the test class containing this test method
   */
  public Class<?> getTestClass() {
    return this.testClass;
  }

  /**
   * Returns the name of this test method
   * 
   * @return the name of this test method
   */
  public String getName() {
    return this.name;
  }

  @Override
  public int hashCode() {
    return 37 * 19 * this.toString().hashCode();
  }

  /**
   * Returns true if the given test method comes from the same class
   * and has the same name as this test method. Returns false otherwise.
   * 
   * @param obj the test method to compare to this test method
   * @return true if the given test method comes from the same class
   *         and has the same name as this test method, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TestMethod) {
      TestMethod other = (TestMethod) obj;
      return this.toString().equals(other.toString());
    }
    return false;
  }

  /**
   * Compares the given test method with this test method by their toString() methods.
   * 
   * @param object the test method to compare to this tes tmethod
   * @return the value 0 if the test method is equal to this test method;
   * 		 a value less than 0 if this test method is less than this test method;
   * 		 and a value greater than 0 if this test method is greater than the given test method
   */
  @Override
  public int compareTo(TestMethod obj) {
    if (obj instanceof TestMethod) {
      TestMethod other = (TestMethod) obj;
      return this.toString().compareTo(other.toString());
    }
    return -1;
  }

  /**
   * Returns the name of this test method with the name of the test class prepended.
   * 
   * @return the name of this test method with the name of the test class prepended
   */
  @Override
  public String toString() {
    return this.testClass.getCanonicalName() + SEPARATOR + this.name;
  }
}
