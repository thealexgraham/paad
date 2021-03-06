package net.alexgraham.thesis.supercollider;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.EventListenerList;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.ChangeSender;
import net.alexgraham.thesis.supercollider.players.PatternPlayer;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.ui.connectors.Connection;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class SCLang extends ChangeSender {
	
	public interface SCUpdateListener extends java.util.EventListener {

	}
	
	public interface SCConsoleListener extends SCUpdateListener {
		public void consoleUpdate(String consoleLine);
	}
	
	public interface SCMessageListener extends SCUpdateListener {
		public void oscMessageOut();
		public void messageIn(boolean oscMessage);
	}
	
	public interface SCServerListener extends SCUpdateListener {
		public void serverReady();
	}
	
	// TODO: Use Adapter instead of multiple interfaces
	
	public class SCLangProperties {
		public final static String avgCPU = "avgCPU";
		public final static String peakCPU = "peakCPU";
	}

	final static boolean logging = true;

	private int sendPort;
	private int receivePort;
	OSCPortOut sender;
	OSCPortIn receiver;
	
	private Process scProcess;
	private BufferedWriter writer;

	private boolean running;

	private double avgCPU;
	private double peakCPU;
	
	private EventListenerList listenerList = new EventListenerList();

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private CopyOnWriteArrayList<SCConsoleListener> consoleListeners = 
			new CopyOnWriteArrayList<SCLang.SCConsoleListener>();
	
	private String scDir = "";
	
	private BufferedWriter exportWriter = null;
	
	private String lastCommand = "";
	
	private boolean rebooted = false;
	
	
	public boolean isRebooted() {
		return rebooted;
	}

	public int getSendPort() {
		return sendPort;
	}

	/**
	 * While an export writer is set, OSC will write net send messages
	 * to the output stream instead of sending OSC message. Used to
	 * create an exported environment. Set to null to go back to normal
	 * @param writer
	 */
	public void setExportWriter(BufferedWriter writer) {
		this.exportWriter = writer;
	}
	
	public void stopExportWriting() {
		this.exportWriter = null;
	}
	
	public String getScDir() {
		return scDir;
	}
	
	public String getScIde() {
		String system = System.getProperty("os.name");
		if (system.equals("Mac OS X")) {
			return "/Applications/SuperCollider.app/Contents/MacOS/SuperCollider";
		} else {
			return "C:/Users/Alex/supercollider/scide.exe";
		}
	}
	

	public SCLang(int sendPort, int receivePort) throws SocketException,
			UnknownHostException {
		running = false;
		this.sendPort = sendPort;
		this.receivePort = receivePort;
	}

	public void setSendPort(int port) throws SocketException,
			UnknownHostException {
		// Need this so we can recreate the sender
		OSC.setSendPort(port);
	}
	
	/**
	 * Starts sclang.exe using default options and no run command
	 * 
	 * @throws IOException
	 */
	public void startSCLang() throws IOException {
		startSCLang("");
	}

	/**
	 * Starts sclang.exe using default options
	 * 
	 * @throws IOException
	 */
	public void startSCLang(String onRun) throws IOException {
		// FIXME Get rid of personal paths
		
		String system = System.getProperty("os.name");
		String pwd = System.getProperty("user.dir");
		String scFile;
		
		System.out.println(pwd);
		try {
			
	        URL path = App.class.getResource("App.class");

	        if(path.toString().startsWith("jar:")) {
				pwd = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString();
	        } else {
	            startWithDefaults(onRun);
	            return;
	        }

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pwd = pwd.replace("\\", "/");
		if (system.equals("Mac OS X")) {
			scFile = pwd + "/sc/run.scd";
			startSCLang(pwd + "/supercollider/mac/SuperCollider.app/Contents/Resources/", "sclang", sendPort, scFile, pwd + "/sc/sclang_conf.yaml", onRun);
		} else {
			scFile = pwd + "/sc/run.scd";
			startSCLang(pwd + "/supercollider/pc/", "sclang.exe", sendPort, scFile, pwd + "/sc/sclang_conf.yaml", onRun);
		}
	}
	
	private void startWithDefaults(String onRun) throws IOException {
		String system = System.getProperty("os.name");
		String pwd = System.getProperty("user.dir");
		String scFile;
		
		if (system.equals("Mac OS X")) {
			scFile = pwd + "/src/main/sc/run.scd";
			startSCLang("/Applications/SuperCollider.app/Contents/Resources/", "sclang", sendPort, scFile, " ", onRun);
		} else {
			scFile = "C:/Users/Alex/Dropbox/Thesis/thesis-code/workspace/agthesis-java/src/main/sc/run.scd";
			startSCLang("C:/Users/Alex/supercollider/", "sclang.exe", sendPort, scFile, " ", onRun);
		}
	}

	/**
	 * Starts up an instance of sclang.exe using the port specified, and runs a
	 * file. Sets up output and input streams as well.
	 * 
	 * @param scDir
	 * @param scPort
	 * @param runFile
	 * @throws IOException
	 */
	public void startSCLang(String scDir, String scExec, int scPort, String runFile, String confFile, String onRun)
			throws IOException {
		running = true;
		this.scDir = scDir;
		
		// Create Process to run sclang
		ProcessBuilder pb = new ProcessBuilder(scDir + scExec, "-u",
				String.valueOf(scPort), "-l", confFile);// , runFile);
		pb.directory(new File(scDir));
		pb.redirectErrorStream(true);

		scProcess = pb.start();

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

		writer = new BufferedWriter(new OutputStreamWriter(
				scProcess.getOutputStream()));
		
		sendCommand(onRun);
		sendCommand("\"" + runFile + "\"" + ".load.postln");
	}

	/**
	 * Sends a command to the sclang.exe directly
	 * 
	 * @param command
	 *            - the command
	 */
	public void sendCommand(String command) {
		try {
			lastCommand = command;
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

				while ((s = inStreamReader.readLine()) != null) {
//					System.out.println(s);
					if (!command) {
						s = s.replace("sc3> ", "");
						if (s.equals("|")) {
							command = true;
						} else {
							if (!s.startsWith("<-")) {
								System.out.println("sc[ " + s);
								if (s.equals(lastCommand)) {
									s = ">> " + s;
									lastCommand = null;
								}
								fireConsoleUpdate(s);
								fireInMessageUpdate(false);
							} else {
								fireInMessageUpdate(false);
							}

						}
						
					} else {
						command = false;
						switch ((splitString = s.split(":"))[0]) {

							case "avgCPU":
								setAvgCPU(round(Double.valueOf(splitString[1])));
								break;

							case "peakCPU":
								setPeakCPU(round(Double.valueOf(splitString[1])));
								break;
								
							case "receivePort":
								String port = splitString[1];
								OSC.setSendPort(Integer.valueOf(port));
								log("set send port to " + sendPort);
								break;

							case "setListener":
								// Send the Java listening port to supercollider
								// so it knows where to send
								OSC.sendMessage("/start/port", receivePort);
								break;
							case "ready":
								log("Server is ready.");
								fireServerReadyUpdate();
								break;
							case "test":
								log("Test is dumb");
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
		// sendCommand("Server.local.quit");
		OSC.sendMessage("/quit", 1);
		// Sometimes the process gets destroyed before we have time to quit
		// scsynth
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		scProcess.destroy();
	}
	
    public void rebootServer() throws IOException {
    	
    	App.sc.stopSCLang();
    	
    	rebooted = true;
    	
    	// Do this since the server will send new versions
    	App.defModel.clearDefModel();
    	App.launchTreeModel.clearLaunchTreeModel();
    	
    	App.sc.startSCLang();
    	
    	
    	
    	App.sc.addUpdateListener(SCServerListener.class, new SCServerListener() {
			
			@Override
			public void serverReady() {
				
		    	ArrayList<PatternPlayer> players = App.synthModel.getPlayers();
		    	for (PatternPlayer player : players) {
		    		player.reset();
		    	}
				
		    	ArrayList<Instance> instances= App.synthModel.getInstances();
		    	for (Instance instance : instances) {
		    		instance.start();
				}
		    	
		    	ArrayList<Connection> connections = App.connectionModel.getConnections();
		    	for (Connection connection : connections) {
					if (connection.connectModules()) {
						System.out.println("Java{ Could not connect modules");
					}
				}
		    	
		    	App.sc.removeUpdateListener(SCServerListener.class, this);
			}
		});

    }
    
    public void saveState() throws IOException {
    	// Write to disk with FileOutputStream
    	String filename = "testobject.data";
    	File out = new File(System.getProperty("user.home") + "\\test\\" + filename);
    	
    	FileOutputStream f_out = new 
    		FileOutputStream(out);

    	// Write object with ObjectOutputStream
    	ObjectOutputStream obj_out = new
    		ObjectOutputStream (f_out);

    	// Write object out to disk
    	obj_out.writeObject ( App.connectionModel );
    }

	public void sendMessage(String address, Object... args) {
		if (exportWriter == null) {
			// Send message as normal
			OSC.sendMessage(this.sendPort, address, args);			
		} else {
			// Write the messages to file
			try {
				exportWriter.write("net.sendMsg('" + address + "', ");
				exportWriter.write(createArgumentsListString(args) + ");");
				exportWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void sendSilentMessage(String address, Object... args) {
		if (exportWriter == null) {
			// Send message as normal
			OSC.sendSilentMessage(this.sendPort, address, args);			
		} else {
			// Write the messages to file
			try {
				exportWriter.write("net.sendMsg('" + address + "', ");
				exportWriter.write(createArgumentsListString(args) + ");");
				exportWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String createArgumentsListString(Object[] arguments) {
		String listString = "";
		for (Object object : arguments) {
			String argString = object.toString();
			if (object instanceof String)
				argString = "\"" + argString + "\"";
			listString = listString + argString + ", ";
		}
		listString = listString.substring(0, listString.length() - 2); // Remove trailing ,		
		return listString;
	}

	public void createListener(String address, OSCListener listener) {
		OSC.createListener(address, listener);
//		OSC.createListener(address, new OSCListener() {
//			
//			@Override
//			public void acceptMessage(Date time, OSCMessage message) {
//				// FIXME: Only do this when debugging
//				System.out.println("java[ msg in: " + message.getAddress() + " " + message.getArguments().toString());
//			}
//		});

	}
	
	// Getters / Setters
	// -----------------------------
	
	public double getAvgCPU() { return avgCPU; }
	public void setAvgCPU(double avgCPU) {
		double oldValue = this.avgCPU;
		this.avgCPU = avgCPU;
		this.pcs.firePropertyChange(SCLangProperties.avgCPU, oldValue, this.avgCPU);
	}

	public double getPeakCPU() { return peakCPU; }
	public void setPeakCPU(double peakCPU) {
		this.pcs.firePropertyChange(SCLangProperties.peakCPU, this.peakCPU, peakCPU);
		this.peakCPU = peakCPU;
	}
	
	// Property Changes
	// -----------------------------

	// Property Change Listener
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(propertyName, listener);
	}
	
	// Update Listeners
	// -----------------------------
	public <T extends SCUpdateListener> void addUpdateListener(Class <T> type, T listener) {
		listenerList.add(type, listener);
	}

	public <T extends SCUpdateListener> void removeUpdateListener(Class <T> type, T listener) {
		listenerList.remove(type, listener);
	}
	
	protected void fireConsoleUpdate(String line) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SCConsoleListener.class) {
				((SCConsoleListener) listeners[i + 1]).consoleUpdate(line);
			}
		}
	}
	
	protected void fireServerReadyUpdate() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SCServerListener.class) {
				((SCServerListener) listeners[i + 1]).serverReady();
			}
		}
	}
	
	protected void fireInMessageUpdate(boolean oscMessage) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SCMessageListener.class) {
				((SCMessageListener) listeners[i + 1]).messageIn(oscMessage);
			}
		}
	}
	
	protected void fireOutMessageUpdate() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SCMessageListener.class) {
				((SCMessageListener) listeners[i + 1]).oscMessageOut();
			}
		}
	}
	
	// Tools 
	// ---------------------

	public static double round(double val) {
		return Math.round(val * 100.0) / 100.0;
	}

	public static void log(String log) {
		if (logging)
			System.out.println("java[ " + log);

	}

}
