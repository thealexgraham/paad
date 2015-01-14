package AlexGraham.TestMaven.tests;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class SendOSC {
	
	public static void main(String[] args) throws SocketException, UnknownHostException {
		// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	System.out.println("Sent Message");
	}

}
