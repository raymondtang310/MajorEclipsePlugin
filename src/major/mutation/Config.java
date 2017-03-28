// This package name is required by Major!
package major.mutation;

import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * A simple driver class for Major --
 * this class name is required by Major!
 */
public class Config {

    /*
     * The mutant identifier:
     *
     * __M_NO <  0 -> Run original version
     *
     * __M_NO == 0 -> Run original version and gather coverage information
     *
     * __M_NO >  0 -> Execute mutant with the corresponding id
     */
    public static int __M_NO = -1;

    // Set to store IDs of covered mutants
    public static Set<Integer> covSet = new TreeSet<Integer>();
    
    static {
    	String fileName = "/home/raymond/Desktop/Config.txt";
        try {
			PrintWriter writer = new PrintWriter(fileName);
			writer.println(Config.__M_NO);
			writer.println("Class config loaded, using classloader " + Config.class.getClassLoader());
			writer.println();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // The coverage method is called if and only if the
    // mutant identifier is set to 0!
    public static boolean COVERED(int from, int to) {
        String fileName = "/home/raymond/Desktop/Config.txt";
        try {
        	//ClassLoader.getSystemClassLoader().loadClass("major.mutation.Config");
			PrintWriter writer = new PrintWriter(new FileWriter(fileName, true));
			writer.println("COVERED was called");
			writer.println(Config.__M_NO);
			writer.println(Config.class.getClassLoader());
			writer.println();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	synchronized (covSet) {
            for (int i=from; i<=to; ++i) {
                covSet.add(i);
            }
        }
        // Always return false as required by
        // Conditional Mutation!
        return false;
    }

    /*
     * Additional methods for the mutation analysis back-end
     */
    // Reset the coverage information
    public static void reset() {
    	String fileName = "/home/raymond/Desktop/Config.txt";
        try {
        	//ClassLoader.getSystemClassLoader().loadClass("major.mutation.Config");
			PrintWriter writer = new PrintWriter(new FileWriter(fileName, true));
			writer.println("reset was called");
			writer.println(Config.__M_NO);
			writer.println(Config.class.getClassLoader());
			writer.println();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        synchronized (covSet) {
            covSet.clear();
        }
    }

    // Get list of all covered mutants
    public static List<Integer> getCoverageList() {
        synchronized (covSet) {
            return new ArrayList<Integer>(covSet);
        }
    }
}
