package AlexGraham.TestMaven.old;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.sound.sampled.Mixer;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import AlexGraham.TestMaven.supercollider.SCLang;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class SynthSelector extends JApplet implements ActionListener, FocusListener {
	
	final int IN_PORT = 1295;
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	
	JLabel topLabel;
	
	JButton timeButton;
	JButton stringButton;
	
	JTextField stringField;
	
	JTextArea timeArea;
	
	JList<String> synthList;
	DefaultListModel<String> synthListModel;
	
	Hashtable<String, SynthDef> synthdefs;
	
	JButton launchButton;
	
	SCLang sc;
	
	OSCPortIn receiver;
	OSCPortOut sender;
	
	int lastInt = 0;
	
	public void start() {
		
		System.out.println("Starting");
		
		try {
			sc = new SCLang(27320, IN_PORT);
			sc.startSCLang();
			createListeners();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Create network sockets
		
		// Set up window
		setSize(300, 150);
		setLayout(new BorderLayout());
		setupLayout();		
		
		middlePanel.add(new JLabel("Available instruments"));
		
		synthdefs = new Hashtable<String, SynthDef>();
		
		synthListModel = new DefaultListModel<String>();
		synthList = new JList<String>(synthListModel);
		synthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		synthList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				JList theList = (JList)e.getSource();
				if (theList.getSelectedIndex() == -1) {
					// No selection, don't have launch button
					launchButton.setEnabled(false);
				} else {
					launchButton.setEnabled(true);
				}
			}
		});
		
		JScrollPane listScrollPane = new JScrollPane(synthList);
		
		middlePanel.add(listScrollPane);
		
		launchButton = new JButton("Launch Synth");
		launchButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String currentSelection = synthList.getSelectedValue();
				launchSynth(currentSelection);
			}
		});
		
		synthListModel.addElement("Testing");
		
		middlePanel.add(launchButton);
		

		
	}
	
	public void initializeSockets() {

		try {
			
			receiver = new OSCPortIn(1260);
			sender = new OSCPortOut(InetAddress.getLocalHost(), 57120);
			
			createListeners();
			
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void createListeners() throws SocketException {
		
    	sc.createListener("/addsynth", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			SynthDef synth = new SynthDef(synthName);
    			synthdefs.put(synthName, synth);
    			synthListModel.addElement(synthName);
    			System.out.println("Adding synth");
    			// Also Add To The List
    		}
    	});

    	sc.createListener("/addparam", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			final String paramName = (String) arguments.get(1);
    			
    			final float min = convertToFloat(arguments.get(2));
    			final float max = convertToFloat(arguments.get(3));
    			final float value = convertToFloat(arguments.get(4));
       			
    			SynthDef synth = synthdefs.get(synthName);
    			
    			synth.addParameter(paramName, min, max, value);
    			System.out.println("Adding Parameter");
    		}
    		
    		private Float convertToFloat(Object number) {
    			Float toReturn = 0.0f;
    			
    			if (number.getClass() == Float.class) {
    				toReturn = (Float) number;
    			} else if (number.getClass() == Integer.class) {
    				
    				toReturn = (Float) ((Integer) number).floatValue();
    			}
    			return toReturn;
    		}
    	});
	}
	
	public void launchSynth(String synthName) {
		SynthDef synth = synthdefs.get(synthName);
		// JFrame Test
		SynthWindow frame = new SynthWindow(synth, sender, receiver);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	private void setupLayout() {
		
		//Top Panel//
		
		topPanel = new JPanel(new FlowLayout());
		topLabel = new JLabel("Instruments");
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		
		middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

		//Bottom Panel//

		bottomPanel = new JPanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);

		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent ev) {
		// TODO Auto-generated method stub
		
		if(ev.getSource() == timeButton) {
			timeArea.setText("Time is " + System.currentTimeMillis());
		} else if (ev.getSource() == stringButton) {
			
			try {
				int numToMultiply = Integer.parseInt(stringField.getText());
				lastInt = numToMultiply;
				System.out.println(numToMultiply);
				timeArea.setText(lastInt + " * 2 =" + String.valueOf(numToMultiply * 2));
			} catch (NumberFormatException e) {
				timeArea.setText("Input must be an integer!");
				stringField.setText(String.valueOf(lastInt));
			}
		}
	}
	public void stop() {
    	receiver.close();
	}

	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}
