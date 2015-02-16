package net.alexgraham.thesis.supercollider;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.alexgraham.thesis.App;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class OSC {
	private static int sendPort;
	private static int receivePort;
	
	private static OSCPortOut sender;
	private static OSCPortIn receiver;
	
	static public void start(int send, int receive) throws SocketException, UnknownHostException {
		
		sendPort = send;
		receivePort = receive;
		
		sender = new OSCPortOut(InetAddress.getLocalHost(), sendPort);
		
		if (receiver != null) {
			receiver.stopListening();
			receiver.close();
		}
		
		receiver = new OSCPortIn(receivePort);
		receiver.startListening();
	}
	
	static public void setSendPort(int port) throws SocketException, UnknownHostException {
		sendPort = port;
		
		sender.close();
		sender = new OSCPortOut(InetAddress.getLocalHost(), sendPort);
	}
	
	static public void stop() {
		receiver.stopListening();
		receiver.close();
	}
	
	public static void sendMessage(String address, Object... args) {
		sendMessage(sendPort, address, args);
	}
	
	// create test for sending and receiving messages so I always know it works...
	public static void sendMessage(int port, String address, Object... args) {
		try {
			
			// Create OSC sending object for localhost (this might be better done for the class)
			// OSCPortOut sender = new OSCPortOut(InetAddress.getLocalHost(), port);
			
			// Create the OSC Message from the arguments
	    	List<Object> arguments = new ArrayList<Object>();
	    	arguments.addAll(Arrays.asList(args));
	    	OSCMessage msg = new OSCMessage(address, arguments);
	    	
	    	// Send the message
	    	sender.send(msg);
	    	// FIXME: Make log ignore more robust
	    	if (!address.contains("paramc")) {
	    		log("sent msg: " + address + " " + arguments.toString());	    		
	    	}
	    	App.sc.fireOutMessageUpdate();
	    	
		} catch (SocketException e) {
			// 
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// 
			e.printStackTrace();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		}
	}
	
	public static void createListener(String address, OSCListener listener) {
		receiver.addListener(address, listener);
	}
	
	public static double round(double val) {
		return Math.round(val * 100.0) / 100.0;
	}
	
	public static void log(String log) {
		System.out.println("java[ " + log);
	}
}
