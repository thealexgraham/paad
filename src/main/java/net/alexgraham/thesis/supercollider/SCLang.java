package net.alexgraham.thesis.supercollider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;

import javax.sound.sampled.Port;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import net.alexgraham.thesis.ChangeSender;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class SCLang extends ChangeSender {
	
	public interface SCUpdateListener extends java.util.EventListener {
		public void avgUpdate(double avgCPU);
		public void peakUpdate(double peakCPU);
		public void consoleUpdate(String consoleLine);
	}
	
	final static boolean logging = false;
	
	private int sendPort;
	private int receivePort;
	
	OSCPortOut sender;
	OSCPortIn receiver;
	private Process scProcess;
	
	private ArrayList<SCUpdateListener> listeners;
		
	private BufferedWriter writer;

	private boolean running;
	
	 EventListenerList listenerList = new EventListenerList();

	 public void addUpdateListener(SCUpdateListener l) {
	     listenerList.add(SCUpdateListener.class, l);
	 }

	 public void removeUpdateListener(SCUpdateListener l) {
	     listenerList.remove(SCUpdateListener.class, l);
	 }

	 protected void fireUpdate(double avgCPU) {
	     // Guaranteed to return a non-null array
	     Object[] listeners = listenerList.getListenerList();
	     
	     for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==SCUpdateListener.class) {
	             ((SCUpdateListener)listeners[i+1]).avgUpdate(avgCPU);
	         }
	     }
	 }

	public SCLang (int sendPort, int receivePort) throws SocketException, UnknownHostException {
		running = false;
		this.sendPort = sendPort;
		this.receivePort = receivePort;
		listeners = new ArrayList<SCLang.SCUpdateListener>();
	}
	
	public void addListener(SCUpdateListener listener) {
		listeners.add(listener);
	}
	

	public void setSendPort(int port) throws SocketException, UnknownHostException {
		// Need this so we can recreate the sender
		OSC.setSendPort(port);
	}
	
	/**
	 * Starts sclang.exe using default options
	 * @throws IOException
	 */
	public void startSCLang() throws IOException {
		//String pwd = System.getProperty("user.dir");
		String scFile = "C:/Users/Alex/Dropbox/Thesis/thesis-code/workspace/agthesis-java/src/main/sc/run.scd";
		startSCLang("C:/Users/Alex/supercollider", sendPort, scFile);
	}
	
	/**
	 * Starts up an instance of sclang.exe using the port specified,
	 * and runs a file. Sets up output and input streams as well.
	 * @param scDir
	 * @param scPort
	 * @param runFile
	 * @throws IOException
	 */
	public void startSCLang(String scDir, int scPort, String runFile ) throws IOException {
		running = true;
		
		// Create Process to run sclang 
		ProcessBuilder pb=new ProcessBuilder(scDir + "/sclang.exe", "-u", String.valueOf(scPort));//, runFile);
		pb.directory(new File(scDir));
		pb.redirectErrorStream(true);
		
		scProcess=pb.start();

		// Shut down the supercollider process when we stop
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	        	if (running) {
	        		stopSCLang();
	            	OSC.stop();
	        	}
	        }
	    });
	    
	    // Output Stream Process
	    new Thread(new PostRunnable()).start();
	    
	    writer = new BufferedWriter
	    		(new OutputStreamWriter(scProcess.getOutputStream()));
	    sendCommand("\"" + runFile + "\"" + ".load.postln");
	}
	
	/** 
	 * Sends a command to the sclang.exe directly
	 * @param command - the command 
	 */
	public void sendCommand(String command) {
		try {
			writer.write(command + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	class PostRunnable implements Runnable {
		public void run() {
			
			// Get the input stream so we can output it
			BufferedReader inStreamReader = new BufferedReader(
				    new InputStreamReader(scProcess.getInputStream())); 

			String s;

			try {
				boolean command = false;
				String[] splitString;
				
				while((s = inStreamReader.readLine()) != null){
					
					if (!command) {
						s = s.replace("sc3> ", "");
						if (s.equals("|")) {
							command = true;
						} else {
							System.out.println("sc[ " + s);
							//((JTextArea)components.get("consoleArea")).append(s+"\n");
//								consoleArea.append("  " + s+"\n"); // Write to the console window
//								consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
//							for (SCUpdateListener listener : listeners) {
//								listener.consoleUpdate(s);
//							}
						}
					} else {	
						command = false;
						switch ((splitString = s.split(":"))[0]) {
						
							case "avgCPU":
//								for (SCUpdateListener listener : listeners) {
//									listener.avgUpdate(round(Double.valueOf(splitString[1])));
//								}
								fireAvgUpdate(round(Double.valueOf(splitString[1])));
								firePropertyChange("avgCPU", 0, round(Double.valueOf(splitString[1])));
								break;
								
							case "peakCPU":
//								for (SCUpdateListener listener : listeners) {
//									listener.peakUpdate(round(Double.valueOf(splitString[1])));
//								}
								break;
							case "receivePort":
								String port = splitString[1];
								OSC.setSendPort(Integer.valueOf(port));
								log("set send port to " + sendPort);
								break;
								
							case "setListener":
								// Send the Java listening port to supercollider so it knows where to send
								OSC.sendMessage("/start/port", receivePort);
								break;
								
							case "ready":
								log("Server is ready.");
								break;
								
							default:
								log("Unknown command: " + s);
						}
					}
				}

			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void stopSCLang() {
		running = false;
    	//sendCommand("Server.local.quit");
    	OSC.sendMessage("/quit", 1);
    	// Sometimes the process gets destroyed before we have time to quit scsynth
    	try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

    	scProcess.destroy();
	}
	
	public void sendMessage(String address, Object... args) {
		OSC.sendMessage(this.sendPort, address, args);
	}

	public void createListener(String address, OSCListener listener) {
		OSC.createListener(address, listener);

	}
	
	public static double round(double val) {
		return Math.round(val * 100.0) / 100.0;
	}
	
	public static void log(String log) {
		if (logging)
			System.out.println("java[ " + log);

	}
}
