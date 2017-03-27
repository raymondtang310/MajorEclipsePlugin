package util;

import java.io.IOException;
import java.io.File;
import java.net.URLClassLoader;

import java.net.URL;
import java.lang.reflect.Method;

public class ClassPathHacker {

	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}
	
	public static void addFile(File f) throws IOException {
		addURL(f.toURI().toURL());
	}
	
	public static void addURL(URL u) throws IOException {
		URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		try {
			Class<?>[] parameters = new Class[]{URL.class};
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(loader, new Object[]{u});
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Could not add URL to classloader");
		}
	}
}