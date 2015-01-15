package AlexGraham.TestMaven.tests;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;
import java.util.concurrent.TimeUnit;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

public class SocketSpeed {
	
	static OSCPortOut sender;

	public static void main(String[] args) throws IOException {
		int n = 20000;
		// TODO Auto-generated method stub
	
		for (int j = 0; j < 10; j++) {
			n = (int) Math.pow(10, j);
			System.out.println("\nRunning: " + n);
			runTests(n);
		}
	}
	
	public static void runTests(int n) throws IOException {
		
		long startTime, endTime; 
		double shared, multi;

		sender = new OSCPortOut(InetAddress.getLocalHost(), 17000);
		
		//System.out.println(testSingle(1));
		//testMulti(1);
		testSingle(1);
		testMulti(1);
//		int i = 0;
//		
//		startTime = System.nanoTime();
//		for (i=0; i < n; i++){
//			singleSender();
//		}
//		endTime = System.nanoTime();
//		shared = (double)(endTime - startTime) / 1000000.0;
//		System.out.println("share: " + shared);
//		
//		
//		
//		startTime = System.nanoTime();
//		for (i=0; i < n; i++){
//			multiSender();
//		}
//		endTime = System.nanoTime();
//		multi = (double)(endTime - startTime) / 1000000.0;
//		System.out.println("multi: " + multi);
//		
//		
//		
//		startTime = System.nanoTime();
//		for (i=0; i < n; i++){
//			singleSender();
//		}
//		endTime = System.nanoTime();
//		shared = (double)(endTime - startTime) / 1000000.0;
//		System.out.println("share: " + shared);
//		
//		
//		startTime = System.nanoTime();
//		for (i=0; i < n; i++){
//			multiSender();
//		}
//		endTime = System.nanoTime();
//		multi = (double)(endTime - startTime) / 1000000.0;
//		System.out.println("multi: " + multi);
		double avg;
		int nTests = 10;
		
		double count = 0;
		double highest = 0;
		
		for (int i = 0; i < nTests; i++) {
			double speed = testSingle(n);
			highest = (speed > highest) ? speed : highest;
			//System.out.println("share: " + speed);
			count += speed;
		}
		avg = count / nTests;
		System.out.println("share avg: " + avg + " dif: " + round(avg - highest));
		
		count = 0;
		highest = 0;
		for (int i = 0; i < nTests; i++) {
			double speed = testMulti(n);
			highest = (speed > highest) ? speed : highest;
			count += speed;
		}
		
		avg = count / nTests;
		System.out.println("multi avg: " + avg + " dif: " + round(avg - highest));


		
		testMulti(n);
		testMulti(n);
		testMulti(n);
		
		

	
	}
	
	public static double testSingle(int n) throws IOException {
		long startTime, endTime; 
		double time;
		int i;
		
		startTime = System.nanoTime();
		for (i=0; i < n; i++){
			singleSender();
		}
		endTime = System.nanoTime();
		time = (double)(endTime - startTime) / 1000000.0;
		//shared = TimeUnit.SECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
		//System.out.println("share: " + shared);
		return time;
	}
	
	public static double testMulti(int n) throws IOException {
		long startTime, endTime; 
		double multi;
		int i = 0;
		
		startTime = System.nanoTime();
		for (i=0; i < n; i++){
			multiSender();
		}
		endTime = System.nanoTime();
		multi = (double)(endTime - startTime) / 1000000.0;
		return multi;
	}
	
	public static void singleSender() throws IOException {
		// Create OSC sending object for localhost (this might be better done for the class)
		
		// Create the OSC Message from the arguments
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add("test");
    	OSCMessage msg = new OSCMessage("/whatever", arguments);
    	
    	// Send the message
    	sender.send(msg);
	}
	
	public static void multiSender() throws IOException {
		// Create OSC sending object for localhost (this might be better done for the class)
		OSCPortOut sender = new OSCPortOut(InetAddress.getLocalHost(), 17000);
		
		// Create the OSC Message from the arguments
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add("test");
    	OSCMessage msg = new OSCMessage("/whatever", arguments);
    	
    	// Send the message
    	sender.send(msg);
    	sender.close();
	}
	
	public static double round(double val) {
		return Math.round(val * 100.0) / 100.0;
	}

}
