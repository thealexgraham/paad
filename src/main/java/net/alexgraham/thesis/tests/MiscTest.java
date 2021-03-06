package net.alexgraham.thesis.tests;

import java.io.Console;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.UnknownHostException;

import net.alexgraham.thesis.supercollider.OSC;

public class MiscTest {

	public static void main(String[] args) throws IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, 
	ClassNotFoundException, NoSuchMethodException, SecurityException, SocketException, UnknownHostException {
		
		String test = "testing";
		System.out.println(test);
		boolean bool = false;
		System.out.println(String.valueOf(bool));
		
	}
	interface StringFunc {
		   String func(String s);
		}
	interface IntFunc {
		int func(int a, int b);
	}

	static void doNumbers(IntFunc funk) {
		System.out.println(funk.func(5, 20));
	}
		static void doSomething(StringFunc funk) {
		   System.out.println(funk.func("whatever"));
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
	
	public static void old() {
	       System.out.println("Working Directory = " +
	               System.getProperty("user.dir"));
		
		String test = "Test";
		
		if (test == "Test") {
			System.out.println("worked");
		}
		
		testArguments(new Object[] {"test", 3, 5});
	}

}
