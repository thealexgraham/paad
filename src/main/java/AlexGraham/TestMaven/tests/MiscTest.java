package AlexGraham.TestMaven.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MiscTest {

	public static void main(String[] args) throws IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, 
	ClassNotFoundException, NoSuchMethodException, SecurityException {
		// TODO Auto-generated method stub
		
//		java.lang.reflect.Method method;
//		this.getClass().getMethod("testMethod", Integer.class);
		
		Class<?> c = Class.forName("MiscTest");
		Method method = c.getDeclaredMethod("testMethod", Integer.class);
		method.invoke(MiscTest.class, 5);
		
	       System.out.println("Working Directory = " +
	               System.getProperty("user.dir"));
		
		String test = "Test";
		
		if (test == "Test") {
			System.out.println("worked");
		}
		
		testArguments(new Object[] {"test", 3, 5});
	}
	
	public static void testArguments(Object... args) {
		for (Object object : args) {
			System.out.println(object);
		}
	}
	
	public static int testMethod(int i) {
		System.out.println(i);
		return i;
	}

}
