package net.alexgraham.thesis.ui.old;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.tests.demos.ListDemo;
import net.alexgraham.thesis.ui.SynthPanel;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class SynthSelector extends JFrame implements ActionListener {
	
	final int IN_PORT = 1295;
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	
	JLabel topLabel;
	
	JButton timeButton;
	JButton stringButton;
	
	JTextField stringField;
	
	JTextField avgCPUField;
	JTextField peakCPUField;
	JTextArea consoleArea;
	JScrollPane consolePane;
	
	Hashtable<String, JComponent> scLangComponents;
	JTextArea timeArea;
	
	JList<String> synthList;
	DefaultListModel<String> synthListModel;
	
	Hashtable<String, Def> synthdefs;
	
	JButton launchButton;
		
	int lastInt = 0;
	
	public SynthSelector() throws SocketException {
		start();
		createListeners();
		System.out.println("Starting");
	}
	
	public void start() {
				
		// Create network sockets
		JTextArea tester = new JTextArea();
		tester.
		
		// Set up window
		setSize(300, 150);
		setLayout(new BorderLayout());
		setupLayout();		
		
		middlePanel.add(new JLabel("Available instruments"));
		
		synthdefs = new Hashtable<String, Def>();
		
		
		// SynthList Setup
		// -------------------
		synthListModel = new DefaultListModel<String>();
		synthList = new JList<String>(synthListModel);
		synthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		synthList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				JList theList = (JList)e.getSource();
				if (theList.getSelectedIndex() == -1) {
					// No selection, don't have launch button
					launchButton.setEnabled(false);
				} else {
					launchButton.setEnabled(true);
				}
			}
		});
		
		synthList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList)evt.getSource();
				if (evt.getClickCount() == 2) {
					Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex()); 
					if (r != null && r.contains(evt.getPoint()))
					{ 
						int index = list.locationToIndex(evt.getPoint());
						launchSynth(synthList.getSelectedValue());
					}

				}
			}
		});
		
		// Add Srollplane to it
		JScrollPane listScrollPane = new JScrollPane(synthList);
		middlePanel.add(listScrollPane);
		
		// Launch Button
		launchButton = new JButton("Launch Synth");
		launchButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String currentSelection = synthList.getSelectedValue();
				launchSynth(currentSelection);
			}
		});
		
		// Test Button
		JButton messageButton = new JButton("Send Message");
		messageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
		        String command = JOptionPane.showInputDialog("Enter command");
				App.sc.sendCommand(command);	
			}
		});
		
		middlePanel.add(messageButton);

		// Console Setup
		// -------------------
		consoleArea = new JTextArea(15, 50);
		consolePane = new JScrollPane();
		consolePane.setViewportView(consoleArea);
		App.sc.setConsoleArea(consoleArea);
		
		// CPU Fields
		avgCPUField = new JTextField(4);
		peakCPUField = new JTextField(4);
		bottomPanel.add(new JLabel("Avg CPU:"));
		bottomPanel.add(avgCPUField);
		bottomPanel.add(new JLabel("Peak CPU:"));
		bottomPanel.add(peakCPUField);
		
		middlePanel.add(launchButton);
		middlePanel.add(consolePane);
		
		scLangComponents = new Hashtable<String, JComponent>();
		scLangComponents.put("avgCPUField", avgCPUField);
		scLangComponents.put("consoleArea", consoleArea);
		scLangComponents.put("peakCPUField", peakCPUField);
		App.sc.setComponents(scLangComponents);
		
//		scLangComponents = new Hashtable<String, Object>(); {
//			{
//				scLangComponents.put("avgCPUField", avgCPUField);
//				scLangComponents.put("peakCPUField", peakCPUField);
//				scLangComponents.put("consoleArea", consoleArea);
//			}
//		};

	}
	
	public void createListeners() throws SocketException {
		
    	App.sc.createListener("/addsynth", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			Def synth = new Def(synthName, App.sc);
    			synthdefs.put(synthName, synth);
    			synthListModel.addElement(synthName);
    			
    			pack();
    			// Also Add To The List
    		}
    	});

    	App.sc.createListener("/addparam", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			final String paramName = (String) arguments.get(1);
    			
    			final float min = convertToFloat(arguments.get(2));
    			final float max = convertToFloat(arguments.get(3));
    			final float value = convertToFloat(arguments.get(4));
       			
    			Def synth = synthdefs.get(synthName);
    			
    			synth.addParameter(paramName, min, max, value);
    			
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
		Def def = synthdefs.get(synthName);
		Synth synth = new Synth(def, App.sc);
		
		// JFrame Test
		JFrame frame = new JFrame() {
			public void dispose() {
				super.dispose();
				synth.close();
			}
		};
		frame.add(new SynthPanel(synth, App.sc));
		frame.setTitle(synth.getSynthName());
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

		bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);

		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent ev) {
		
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
	
	public void dispose() {
		System.out.println("Disposing");
    	App.sc.stopSCLang();
	}

}
