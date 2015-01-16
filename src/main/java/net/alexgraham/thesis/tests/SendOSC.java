package net.alexgraham.thesis.tests;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class SendOSC {
	
	public static void main(String[] args) throws SocketException, UnknownHostException {
		OSCPortOut sender;
		OSCPortIn receiver;
		sender = new OSCPortOut(InetAddress.getLocalHost(), 57120);
		receiver = new OSCPortIn(1253);
		
		
    	ArrayList<Object> arguments = new ArrayList<Object>();
    	arguments.add("whatever");
    	OSCMessage msg = new OSCMessage("/porter", arguments);
    	
    	try {
			sender.send(msg);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	System.out.println("Sent Message");
	}
	
	public static void testOSC() throws IOException {
    	OSCPortIn receiver = new OSCPortIn(1292);
		
    	OSCListener listener = new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			System.out.println(message.getArguments());
    			
    	    	// Send
    	    	OSCPortOut sender;
				try {
					sender = new OSCPortOut(InetAddress.getLocalHost(), 57120);
	    	    	
					ArrayList<Object> arguments = new ArrayList<Object>();
	    	    	arguments.add(new Integer(3));
	    	    	arguments.add("hello");
	    	    	OSCMessage msg = new OSCMessage("/good/news", arguments);
					
					sender.send(msg);
				} catch (SocketException e1) {

					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

    			System.out.println(" ");
    		}
    		
    	};
    	
    	
    	receiver.addListener("/sayhello", listener);
    	receiver.startListening();
    	
    	// Send
    	OSCPortOut sender = new OSCPortOut(InetAddress.getLocalHost(), 23232);
    	ArrayList<Object> arguments = new ArrayList<Object>();
    	arguments.add(new Integer(3));
    	arguments.add("hello");
    	OSCMessage msg = new OSCMessage("/goodbye", arguments);

    	try {
			sender.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	// Send
		try {
			sender = new OSCPortOut(InetAddress.getLocalHost(), 1292);
	    	
			arguments = new ArrayList<Object>();
	    	arguments.add(new Integer(3));
	    	arguments.add("hello");
	    	msg = new OSCMessage("/sayhello", arguments);
			
			sender.send(msg);
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

    	 // receive
    	

    	System.out.println("Listening");

    	System.in.read();

    	receiver.close();
    	
        System.out.println( "Done." );
	}

}
