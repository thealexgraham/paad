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
		startSCLang("C:/Users/Alex/supercollider", sendPort, "run.scd");
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
						System.out.println("sc: " + s);
						
					    //do something with commandline output.
						if (s.contains("receivePort:")) {
							String port = s.split(":")[1];
							sendPort = Integer.valueOf(port);
							log("set send port to " + sendPort);
						}
						if (s.equals("ready")) {
 							log("sent receive port: " + receivePort);
						}
						if (s.equals("listener:/start/port")) {
							sendMessage("/start/port", new Object[] {receivePort});
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
	
	public void sendMessage(String address, Object[] args) {
		sendMessage(this.sendPort, address, args);
	}
		
	public void sendMessage(int port, String address, Object[] args) {
		try {
			OSCPortOut sender = new OSCPortOut(InetAddress.getLocalHost(), port);
	    	List<Object> arguments = new ArrayList<Object>();
	    	arguments.addAll(Arrays.asList(args));
	    	OSCMessage msg = new OSCMessage(address, arguments);
	    	sender.send(msg);
	    	log("sent msg: " + address + " " + arguments.toString());
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
		System.out.println("java: " + log);
	}
}
