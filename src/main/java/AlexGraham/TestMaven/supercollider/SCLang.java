package AlexGraham.TestMaven.supercollider;

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

import javax.sound.sampled.Port;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
	private Hashtable<String, JComponent> components;
	
	JTextArea consoleArea;
	JTextField avgCPUField;
	JTextField peakCPUField;
	
	private BufferedWriter writer;
	
	
	private boolean running;
	
	public SCLang (int sendPort, int receivePort) throws SocketException, UnknownHostException {
		running = false;
		this.sendPort = sendPort;
		this.receivePort = receivePort;
	}
	
	public void setConsoleArea(JTextArea area) {
		consoleText = area;
	}
	
	public void setComponents(Hashtable<String, JComponent> comps) {
		//TODO Merge instead of just reuse comps, is this really faster?
		components = comps;
		consoleArea = (JTextArea) components.get("consoleArea");
		avgCPUField = (JTextField) components.get("avgCPUField");
		peakCPUField = (JTextField) components.get("peakCPUField");
	}
	
	public void setSendPort(int port) throws SocketException, UnknownHostException {
		// Need this so we can recreate the sender
		OSC.setSendPort(port);
	}
	
	public void startSCLang() throws IOException {
		//String pwd = System.getProperty("user.dir");
		String scFile = "C:/Users/Alex/Dropbox/Thesis/thesis-code/workspace/agthesis-java/src-sc/run.scd";
		startSCLang("C:/Users/Alex/supercollider", sendPort, scFile);
	}
	
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
	
	public void sendCommand(String command) {
		try {
			writer.write(command + "\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
						s = s.replace("sc3> ", "");
						if (s.equals("|")) {
							command = true;
						} else {
							System.out.println("sc[ " + s);
							
							if (consoleText != null) {
								//((JTextArea)components.get("consoleArea")).append(s+"\n");
								consoleArea.append(s+"\n"); // Write to the console window
								consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
							}
						}
					} else {	
						command = false;
						switch ((splitString = s.split(":"))[0]) {
						
							case "avgCPU":
								avgCPUField.setText(Double.toString(
										round(Double.valueOf(splitString[1])))
										+ "%");
								break;
								
							case "peakCPU":
								peakCPUField.setText(Double.toString(
										round(Double.valueOf(splitString[1])))
										+ "%");
								break;
								
							case "receivePort":
								String port = splitString[1];
								OSC.setSendPort(Integer.valueOf(port));
								log("set send port to " + sendPort);
								break;
								
							case "ready":
								log("Server is ready.");
								break;
								
							default:
								log("Unknown command: " + s);
						}
					}
				
					if (s.equals("listener:/start/port")) {
						OSC.sendMessage("/start/port", receivePort);
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
    	sendCommand("Server.local.quit");
//    	OSC.sendMessage("/quit", 1);
    	// Sometimes the process gets destroyed before we have time to quit scsynth
    	try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
		System.out.println("java[ " + log);
	}
}
