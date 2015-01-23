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
import java.util.EventListener;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;

import javax.sound.sampled.Port;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import sun.misc.JavaAWTAccess;
import sun.nio.ch.SelChImpl;
import net.alexgraham.thesis.ChangeSender;
import net.alexgraham.thesis.supercollider.SCLang.SCCPUListener;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;
import com.sun.xml.internal.ws.api.ha.StickyFeature;

public class SCLang extends ChangeSender {
	
	public interface SCUpdateListener extends java.util.EventListener {

	}

	public interface SCCPUListener extends SCUpdateListener {
		public void peakUpdate(double peakCPU);
		public void avgUpdate(double avgCPU);
	}

	public interface SCConsoleListener extends SCUpdateListener {
		public void consoleUpdate(String consoleLine);
	}
	
	public class SCLangUpdates {
		final static String CONSOLE = "console";
		final static String avgCPU = "avgCPU";
		final static String peakCPU = "peakCPU";
	}


	final static boolean logging = false;

	private int sendPort;
	private int receivePort;

	OSCPortOut sender;
	OSCPortIn receiver;
	private Process scProcess;

	private BufferedWriter writer;

	private boolean running;

	EventListenerList listenerList = new EventListenerList();
	
	public <T extends SCUpdateListener> void addUpdateListener(Class <T> type, T listener) {
		listenerList.add(type, listener);
	}

	public <T extends SCUpdateListener> void removeUpdateListener(Class <T> type, T listener) {
		listenerList.remove(type, listener);
	}
	
	public void removeCPUUpdateListener(SCCPUListener l) {
		listenerList.remove(SCCPUListener.class, l);
	}

	protected void fireAvgCPUUpdate(double avgCPU) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SCCPUListener.class) {
				((SCCPUListener) listeners[i + 1]).avgUpdate(avgCPU);
			}
		}
	}

	protected void firePeakCPUUpdate(double peakCPU) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SCCPUListener.class) {
				((SCCPUListener) listeners[i + 1]).peakUpdate(peakCPU);
			}
		}
	}
	
	protected void fireConsoleUpdate(String line) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SCConsoleListener.class) {
				((SCConsoleListener) listeners[i + 1]).consoleUpdate(line);
			}
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
	 * Starts sclang.exe using default options
	 * 
	 * @throws IOException
	 */
	public void startSCLang() throws IOException {
		// String pwd = System.getProperty("user.dir");
		String scFile = "C:/Users/Alex/Dropbox/Thesis/thesis-code/workspace/agthesis-java/src/main/sc/run.scd";
		startSCLang("C:/Users/Alex/supercollider", sendPort, scFile);
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
	public void startSCLang(String scDir, int scPort, String runFile)
			throws IOException {
		running = true;

		// Create Process to run sclang
		ProcessBuilder pb = new ProcessBuilder(scDir + "/sclang.exe", "-u",
				String.valueOf(scPort));// , runFile);
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

					if (!command) {
						s = s.replace("sc3> ", "");
						if (s.equals("|")) {
							command = true;
						} else {
							System.out.println("sc[ " + s);
							fireConsoleUpdate(s);
						}
					} else {
						command = false;
						switch ((splitString = s.split(":"))[0]) {

							case "avgCPU":
								fireAvgCPUUpdate(
										round(Double.valueOf(splitString[1])));
								firePropertyChange(SCLangUpdates.avgCPU, 0, receivePort);
								
								break;

							case "peakCPU":
								firePeakCPUUpdate(
										round(Double.valueOf(splitString[1])));
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
