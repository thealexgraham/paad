package AlexGraham.TestMaven.supercollider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.Port;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class SCLang {
	private int sendPort;
	private int receivePort;
	
	//OSCPortIn sender;
	OSCPortIn receiver;
	
	public SCLang (int sendPort, int receivePort) throws SocketException {
		this.sendPort = sendPort;
		this.receivePort = receivePort;
		
		// Create the receiver and start listening
		receiver = new OSCPortIn(this.receivePort);
		receiver.startListening();
	}
	
	public void startSCLang() throws IOException {
		//String pwd = System.getProperty("user.dir");
		String scFile = "C:/Users/Alex/Dropbox/Thesis/thesis-code/workspace/agthesis-java/src-sc/run.scd";
		startSCLang("C:/Users/Alex/supercollider", sendPort, scFile);
	}
	
	public void startSCLang(String scDir, int scPort, String runFile ) throws IOException {
		
		// Create Process to run sclang 
		ProcessBuilder pb=new ProcessBuilder(scDir + "/sclang.exe", "-u", String.valueOf(scPort), runFile);
		pb.directory(new File(scDir));
		pb.redirectErrorStream(true);
		
		final Process scProcess=pb.start();

		// Shut down the supercollider process when we stop
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	        	log("Shutting down process");
	        	sendMessage("/quit", 1);
	            scProcess.destroy();
	        }
	    });
	    
	    // Get output stream for SuperCollider process
	    
	    new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				log("Started Thread");
				BufferedReader inStreamReader = new BufferedReader(
					    new InputStreamReader(scProcess.getInputStream())); 

				String s;

				try {
					while((s = inStreamReader.readLine()) != null){
						System.out.println("sc[ " + s);
						
						
						if (s.contains("receivePort:")) {
							
							// Set apps send port to supercollider's receive port
							String port = s.split(":")[1];
							sendPort = Integer.valueOf(port);
							log("set send port to " + sendPort);
						}
						if (s.equals("ready")) {
							
						}
						if (s.equals("listener:/start/port")) {
							sendMessage("/start/port", receivePort);
						}


					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}
	
	public void stopSCLang() {
		receiver.stopListening();
		receiver.close();
	}
	
	public void sendMessage(String address, Object... args) {
		sendMessage(this.sendPort, address, args);
	}
	
	// create test for sending and receiving messages so I always know it works...
		
	public void sendMessage(int port, String address, Object... args) {
		try {
			
			// Create OSC sending object for localhost (this might be better done for the class)
			OSCPortOut sender = new OSCPortOut(InetAddress.getLocalHost(), port);
			
			// Create the OSC Message from the arguments
	    	List<Object> arguments = new ArrayList<Object>();
	    	arguments.addAll(Arrays.asList(args));
	    	OSCMessage msg = new OSCMessage(address, arguments);
	    	
	    	// Send the message
	    	sender.send(msg);
	    	log("sent msg: " + address + " " + arguments.toString());
	    	
	    	sender.close();
	    	
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createListener(String address, OSCListener listener) {
		receiver.addListener(address, listener);
	}
	
	public static void log(String log) {
		System.out.println("java[ " + log);
	}
}
