package triangle;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;

/**
 * Test class.
 * 
 * @author Raymond Tang
 *
 */
public class MyTest {
	
    /*
     * Check that .class files get generated in the bin directory
     */
    @Test
    public void testClassFileGeneration() {
    	String path1 = "/home/raymond/workspace/org.rayzor.mutant/bin/triangle/Triangle.class";
    	String path2 = "/home/raymond/workspace/org.rayzor.mutant/bin/triangle/Triangle$Type.class";
        File file1 = new File(path1);
        File file2 = new File(path2);
        file1.delete();
        file2.delete();
        JavaCompilerMethod.main(null);
    	boolean actual1 = file1.exists() && !file1.isDirectory();
    	boolean actual2 = file2.exists() && !file2.isDirectory();
        boolean expected = true;
        assertEquals (expected, actual1);
        assertEquals (expected, actual2);
    }
    
    /*
     * Check that the mutatedBin directory gets generated
     */
    @Test
    public void testMutatedBinDirectoryGeneration() {
    	String path = "/home/raymond/workspace/org.rayzor.mutant/mutatedBin";
        File file = new File(path);
        file.delete();
        Mutator.main(null);
    	boolean actual = file.exists() && file.isDirectory();
        boolean expected = true;
        assertEquals (expected, actual);
    }
    
    /*
     * Check that .class files get generated in the mutatedBin directory
     */
    @Test
    public void testMutatedClassFileGeneration() {
    	String path1 = "/home/raymond/workspace/org.rayzor.mutant/mutatedBin/triangle/Triangle.class";
    	String path2 = "/home/raymond/workspace/org.rayzor.mutant/mutatedBin/triangle/Triangle$Type.class";
        File file1 = new File(path1);
        File file2 = new File(path2);
        file1.delete();
        file2.delete();
        Mutator.main(null);
    	boolean actual1 = file1.exists() && !file1.isDirectory();
    	boolean actual2 = file2.exists() && !file2.isDirectory();
        boolean expected = true;
        assertEquals (expected, actual1);
        assertEquals (expected, actual2);
    }
    
    /*
     * Check that the mutants directory gets generated
     */
    @Test
    public void testMutantsDirectoryGeneration() {
    	String path = "/home/raymond/workspace/org.rayzor.mutant/mutants";
        File file = new File(path);
        file.delete();
        Mutator.main(null);
    	boolean actual = file.exists() && file.isDirectory();
        boolean expected = true;
        assertEquals (expected, actual);
    }
    
    /*
     * Check that the mutants directory is not empty
     */
    @Test
    public void testMutantsNotEmpty() {
    	String path = "/home/raymond/workspace/org.rayzor.mutant/mutants";
        File file = new File(path);
        file.delete();
        Mutator.main(null);
        boolean actual = file.list().length > 0;
    	boolean expected = true;
        assertEquals (expected, actual);
    }
}
