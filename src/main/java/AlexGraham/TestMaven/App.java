package AlexGraham.TestMaven;

import java.awt.List;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import com.illposed.osc.*;


public class App 
{
	static final int SC_PORT = 57120;

    public static void main( String[] args ) throws IOException
    {
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	 // receive
    	

    	System.out.println("Listening");

    	System.in.read();

    	receiver.close();
    	
        System.out.println( "Done." );
    }
}
