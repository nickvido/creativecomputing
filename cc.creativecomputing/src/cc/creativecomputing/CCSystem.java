package cc.creativecomputing;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cc.creativecomputing.util.CCStringUtil;

/**
 * 
 * @author texone
 * 
 */
public class CCSystem {

	public static enum CCOS {
		WINDOWS, MACOSX, LINUX, OTHER;
	}

	/**
	 * Current platform in use, one of WINDOWS, MACOSX, LINUX or OTHER.
	 */
	static public CCOS os;

	static {
		String myOsName = System.getProperty("os.name");

		if (myOsName.indexOf("Mac") != -1) {
			os = CCOS.MACOSX;

		} else if (myOsName.indexOf("Windows") != -1) {
			os = CCOS.WINDOWS;

		} else if (myOsName.equals("Linux")) { // true for the ibm vm
			os = CCOS.LINUX;

		} else {
			os = CCOS.OTHER;
		}
	}

	/**
	 * Current platform in use.
	 * <P>
	 * Equivalent to System.getProperty("os.name"), just used internally.
	 */
	static public String platformName = System.getProperty("os.name");

	/**
	 * Attempt to open a file using the platform's shell.
	 */
	static public void open(String filename) {
		if (os == CCOS.WINDOWS) {
			// just launching the .html file via the shell works
			// but make sure to chmod +x the .html files first
			// also place quotes around it in case there's a space
			// in the user.dir part of the url
			try {
				Runtime.getRuntime().exec("cmd /c \"" + filename + "\"");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not open " + filename);
			}

		} else if (os == CCOS.MACOSX) {
			// osx fix contributed by chandler for rev 0113
			try {
				// Java on OS X doesn't like to exec commands inside quotes
				// for some reason.. escape spaces with slashes just in case
				if (filename.indexOf(' ') != -1) {
					StringBuffer sb = new StringBuffer();
					char c[] = filename.toCharArray();
					for (int i = 0; i < c.length; i++) {
						if (c[i] == ' ') {
							sb.append("\\\\ ");
						} else {
							sb.append(c[i]);
						}
					}
					filename = sb.toString();
				}
				Runtime.getRuntime().exec("open " + filename);

			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not open " + filename);
			}

		} else { // give up and just pass it to Runtime.exec()
			open(new String[] { filename });
		}
	}

	/**
	 * Launch a process using a platforms shell, and an array of args passed on the command line.
	 */
	static public Process open(String args[]) {
		try {
			return Runtime.getRuntime().exec(args);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not open " + CCStringUtil.join(args, ' '));
		}
	}

	/** Path to sketch folder */
	static public String applicationPath = System.getProperty("user.dir");

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// PRINT
	//
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	static public void print(final byte theByte) {
		System.out.print(theByte);
		System.out.flush();
	}

	static public void print(final boolean theBoolean) {
		System.out.print(theBoolean);
		System.out.flush();
	}

	static public void print(final char theChar) {
		System.out.print(theChar);
		System.out.flush();
	}

	static public void print(int what) {
		System.out.print(what);
		System.out.flush();
	}

	static public void print(float what) {
		System.out.print(what);
		System.out.flush();
	}

	static public void print(double what) {
		System.out.print(what);
		System.out.flush();
	}

	static public void print(String what) {
		System.out.print(what);
		System.out.flush();
	}

	static public void print(Object what) {
		if (what == null) {
			// special case since this does fuggly things on > 1.1
			System.out.print("null");

		} else {
			String name = what.getClass().getName();
			if (name.charAt(0) == '[') {
				switch (name.charAt(1)) {
				case '[':
					// don't even mess with multi-dimensional arrays (case '[')
					// or anything else that's not int, float, boolean, char
					System.out.print(what);
					System.out.print(' ');
					break;

				case 'L':
					// print a 1D array of objects as individual elements
					Object poo[] = (Object[]) what;
					for (int i = 0; i < poo.length; i++) {
						System.out.print(poo[i]);
						System.out.print(' ');
					}
					break;

				case 'Z': // boolean
					boolean zz[] = (boolean[]) what;
					for (int i = 0; i < zz.length; i++) {
						System.out.print(zz[i]);
						System.out.print(' ');
					}
					break;

				case 'B': // byte
					byte bb[] = (byte[]) what;
					for (int i = 0; i < bb.length; i++) {
						System.out.print(bb[i]);
						System.out.print(' ');
					}
					break;

				case 'C': // char
					char cc[] = (char[]) what;
					for (int i = 0; i < cc.length; i++) {
						System.out.print(cc[i]);
						System.out.print(' ');
					}
					break;

				case 'I': // int
					int ii[] = (int[]) what;
					for (int i = 0; i < ii.length; i++) {
						System.out.print(ii[i]);
						System.out.print(' ');
					}
					break;

				case 'F': // float
					float ff[] = (float[]) what;
					for (int i = 0; i < ff.length; i++) {
						System.out.print(ff[i]);
						System.out.print(' ');
					}
					break;

				case 'D': // double
					double dd[] = (double[]) what;
					for (int i = 0; i < dd.length; i++) {
						System.out.print(dd[i]);
						System.out.print(' ');
					}
					break;

				default:
					System.out.print(what);
				}
			} else {
				System.out.print(what); // .toString());
			}
		}
	}

	//

	static public void println() {
		System.out.println();
	}

	//

	static public void println(byte what) {
		print(what);
		System.out.println();
	}

	static public void println(boolean what) {
		print(what);
		System.out.println();
	}

	static public void println(char what) {
		print(what);
		System.out.println();
	}

	static public void println(int what) {
		print(what);
		System.out.println();
	}

	static public void println(float what) {
		print(what);
		System.out.println();
	}

	static public void println(double what) {
		print(what);
		System.out.println();
	}

	static public void println(String what) {
		print(what);
		System.out.println();
	}

	static public void println(Object what) {
		if (what == null) {
			// special case since this does fuggly things on > 1.1
			System.out.println("null");

		} else {
			String name = what.getClass().getName();
			if (name.charAt(0) == '[') {
				switch (name.charAt(1)) {
				case '[':
					// don't even mess with multi-dimensional arrays (case '[')
					// or anything else that's not int, float, boolean, char
					System.out.println(what);
					break;

				case 'L':
					// print a 1D array of objects as individual elements
					Object poo[] = (Object[]) what;
					for (int i = 0; i < poo.length; i++) {
						System.out.println(poo[i]);
					}
					break;

				case 'Z': // boolean
					boolean zz[] = (boolean[]) what;
					for (int i = 0; i < zz.length; i++) {
						System.out.println(zz[i]);
					}
					break;

				case 'B': // byte
					byte bb[] = (byte[]) what;
					for (int i = 0; i < bb.length; i++) {
						System.out.println(bb[i]);
					}
					break;

				case 'C': // char
					char cc[] = (char[]) what;
					for (int i = 0; i < cc.length; i++) {
						System.out.println(cc[i]);
					}
					break;

				case 'I': // int
					int ii[] = (int[]) what;
					for (int i = 0; i < ii.length; i++) {
						System.out.println(ii[i]);
					}
					break;

				case 'F': // float
					float ff[] = (float[]) what;
					for (int i = 0; i < ff.length; i++) {
						System.out.println(ff[i]);
					}
					break;

				case 'D': // double
					double dd[] = (double[]) what;
					for (int i = 0; i < dd.length; i++) {
						System.out.println(dd[i]);
					}
					break;

				default:
					System.out.println(what);
				}
			} else {

				System.out.println(what); // .toString());
			}
		}
	}

	static public void printObject(final Object theObject, String theSpace) {
		try {
			final Field[] myFields = theObject.getClass().getDeclaredFields();
			System.out.println(theSpace + theObject.getClass().getName());
			theSpace = theSpace.concat(" ");
			for (Field myField : myFields) {
				myField.setAccessible(true);
				if (myField.getType().isPrimitive() || myField.getType().isInstance("")) {
					System.out.println(theSpace + myField.getType() + " " + myField.getName() + " " + myField.get(theObject));
				} else {
					System.out.println("noprimitiv:" + myField.getType().getName());
					// System.out.println(theSpace + myField.getType() + " " + myField.getName()+":");
					// printObject(myField.get(theObject),theSpace+" ");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to print object:", e);
		}
	}

	/**
	 * Returns the total amount of memory in the Java virtual machine. The value returned by this method may vary over
	 * time, depending on the host environment. Note that the amount of memory required to hold an object of any given
	 * type may be implementation-dependent.
	 * 
	 * @return the total amount of memory currently available for current and future objects, measured in bytes.
	 */
	public static long memoryTotal() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * Returns the amount of free memory in the Java Virtual Machine. Calling the gc method may result in increasing the
	 * value returned by freeMemory.
	 * 
	 * @return an approximation to the total amount of memory currently available for future allocated objects, measured
	 *         in bytes.
	 */
	public static long memoryFree() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * Returns the memory currently used by the program.
	 * 
	 * @return
	 */
	public static long memoryInUse() {
		return memoryTotal() - memoryFree();
	}
}
