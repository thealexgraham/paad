package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.InstDef;
import net.alexgraham.thesis.supercollider.Synth;
import net.alexgraham.thesis.supercollider.SynthDef;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class SynthLauncherPanel extends JPanel {
	
	public interface SynthLauncherDelegate {
		void launchSynth(SynthDef synthDef);
		void addInstrument(InstDef instdef);
	}
		
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	
	JLabel topLabel;

	JList<SynthDef> synthList;
	//DefaultListModel<String> synthListModel;
	DefaultListModel<SynthDef> synthListModel;
	Hashtable<String, SynthDef> synthdefs;
	
	SynthLauncherDelegate delegate;
	
	JButton launchButton;
		
	int lastInt = 0;
	
	public SynthLauncherPanel(SynthLauncherDelegate delegate) throws SocketException {
		this.delegate = delegate;
		start();
		createListeners();
		System.out.println("Starting");
	}
	
	public void start() {
		
		// Set up window
		setSize(300, 150);
		setLayout(new BorderLayout());
		setupLayout();		
		
		middlePanel.add(new JLabel("Available instruments"));
		
		synthdefs = new Hashtable<String, SynthDef>();
		
		
		// SynthList Setup
		// -------------------
		//synthListModel = new DefaultListModel<String>();
		synthListModel = new DefaultListModel<SynthDef>();
		//synthList = new JList<String>(synthListModel);
		synthList = new JList<SynthDef>(synthListModel);
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
						SynthDef selected = synthList.getSelectedValue();
						launchSynth(selected);
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
				SynthDef currentSelection = synthList.getSelectedValue();
				launchSynth(currentSelection);
			}
		});
		
		middlePanel.add(launchButton);

	}
	
	public void createListeners() throws SocketException {
		
    	App.sc.createListener("/addsynth", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			SynthDef synth = new SynthDef(synthName, App.sc);
    			synthdefs.put(synthName, synth);
    			//synthListModel.addElement(synthName);
    			synthListModel.addElement(synth);
    			// Also Add To The List
    		}
    	});
    	
    	App.sc.createListener("/instdef/add", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			//SynthDef synth = new SynthDef(synthName, App.sc);
    			InstDef synth = new InstDef(synthName, App.sc);
    			
    			synthdefs.put(synthName, synth);
    			synthListModel.addElement(synth);

    			//synthListModel.addElement(synthName + "(inst)");
    			
    			// Also Add To The List
    		}
    	});
    	
    	OSCListener paramlistener = new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			final String paramName = (String) arguments.get(1);
    			
    			final float min = AGHelper.convertToFloat(arguments.get(2));
    			final float max = AGHelper.convertToFloat(arguments.get(3));
    			final float value = AGHelper.convertToFloat(arguments.get(4));
       			
    			SynthDef synth = synthdefs.get(synthName);
    			
    			synth.addParameter(paramName, min, max, value);
    			
    		}
    	};
    	
    	App.sc.createListener("/addparam", paramlistener);
    	App.sc.createListener("/instdef/param", paramlistener);
	}
	
	public void launchSynth(SynthDef synthDef) {

		if (synthDef.getClass() == SynthDef.class) {
			delegate.launchSynth(synthDef);			
		} else if (synthDef.getClass() == InstDef.class) {
			delegate.addInstrument( (InstDef)synthDef );
		}

	}
	
	public void launchSynthWindow(String synthName) {
		SynthDef synthDef = synthdefs.get(synthName);
		Synth synth = new Synth(synthDef, App.sc);
		
		// JFrame Test
		JFrame frame = new JFrame() {
			public void dispose() {
				super.dispose();
				synth.close();
			}
		};
		
		frame.add(new SynthPanel(synth));
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

}
