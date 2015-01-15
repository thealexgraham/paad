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
import javax.swing.JTextArea;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class SCLang {
	private int sendPort;
	private int receivePort;
	
	OSCPortOut sender;
	OSCPortIn receiver;
	private Process scProcess;
	
	private JTextArea consoleText;
	
	private boolean running;
	
	public SCLang (int sendPort, int receivePort) throws SocketException, UnknownHostException {
		running = false;
		
		this.sendPort = sendPort;
		this.receivePort = receivePort;
		
		sender = new OSCPortOut(InetAddress.getLocalHost(), this.sendPort);
		
		// Create the receiver and start listening
		receiver = new OSCPortIn(this.receivePort);
		receiver.startListening();
	}
	
	public void setConsoleArea(JTextArea area) {
		consoleText = area;
	}
	
	public void setSendPort(int port) throws SocketException, UnknownHostException {
		// Need this so we can recreate the sender
		this.sendPort = port;
		
		sender.close();
		sender = new OSCPortOut(InetAddress.getLocalHost(), this.sendPort);
	}
	
	public void startSCLang() throws IOException {
		//String pwd = System.getProperty("user.dir");
		String scFile = "C:/Users/Alex/Dropbox/Thesis/thesis-code/workspace/agthesis-java/src-sc/run.scd";
		startSCLang("C:/Users/Alex/supercollider", sendPort, scFile);
	}
	
	public void startSCLang(String scDir, int scPort, String runFile ) throws IOException {
		running = true;
		
		// Create Process to run sclang 
		ProcessBuilder pb=new ProcessBuilder(scDir + "/sclang.exe", "-u", String.valueOf(scPort), runFile);
		pb.directory(new File(scDir));
		pb.redirectErrorStream(true);
		
		scProcess=pb.start();

		// Shut down the supercollider process when we stop
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	        	if (running) {
	        		stopSCLang();
	        	}
	        }
	    });
	    
	    // Output Stream Process
	    new Thread(new PostRunnable()).start();
	}
	class PostRunnable implements Runnable {
		public void run() {
			
			// TODO Auto-generated method stub

			BufferedReader inStreamReader = new BufferedReader(
				    new InputStreamReader(scProcess.getInputStream())); 

			String s;

			try {
				boolean command = false;
				String[] splitString;
				
				while((s = inStreamReader.readLine()) != null){
					
					if (!command) {

						if (s.equals("|")) {
							command = true;
						} else {
							System.out.println("sc[ " + s);
							
							if (consoleText != null) {
								consoleText.append(s+"\n"); // Write to the console window
								consoleText.setCaretPosition(consoleText.getDocument().getLength());
							}
						}
					} else {
						command = false;
						switch ((splitString = s.split(":"))[0]) {
							case "avgCPU":
								
								break;
							case "peakCPU":
								
								break;
						}
					}
				
					// Change to switch statement

					// command
//					if (s.substring(0, 1).equals("|")) {
//						System.out.println("Found a commenad");
//						
//						switch (s = s.substring(1).split(":")[1]) {
//							case "avgCPU":
//								System.out.println("AvgCPU: " + s);
//						}
//					}
					if (s.contains("receivePort:")) {
			
						// Set apps send port to supercollider's receive port
						String port = s.split(":")[1];
						setSendPort(Integer.valueOf(port));
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
	}

	
	public void stopSCLang() {
		running = false;
		
		receiver.stopListening();
		receiver.close();
		
    	sendMessage("/quit", 1);
    	scProcess.destroy();
	}
	
	public void sendMessage(String address, Object... args) {
		sendMessage(this.sendPort, address, args);
	}
	
	// create test for sending and receiving messages so I always know it works...
	public void sendMessage(int port, String address, Object... args) {
		try {
			
			// Create OSC sending object for localhost (this might be better done for the class)
			//OSCPortOut sender = new OSCPortOut(InetAddress.getLocalHost(), port);
			
			// Create the OSC Message from the arguments
	    	List<Object> arguments = new ArrayList<Object>();
	    	arguments.addAll(Arrays.asList(args));
	    	OSCMessage msg = new OSCMessage(address, arguments);
	    	
	    	// Send the message
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
//		receiver.addListener(address, listener {
		receiver.addListener(address, listener);

	}
	
	public static void log(String log) {
		System.out.println("java[ " + log);
	}
}
