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
import java.util.List;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import AlexGraham.TestMaven.supercollider.SCLang;
import AlexGraham.TestMaven.supercollider.SynthDef.Parameter;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class SynthWindow extends JFrame {
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	
	JLabel topLabel;
	
	JButton timeButton;
	JButton stringButton;
	
	JTextField stringField;
	
	JTextArea timeArea;
	
	SCLang sc;
	
	String synthName;
	UUID id;
	
	int lastInt = 0;
		
	public SynthWindow(SynthDef synth, OSCPortOut sender, OSCPortIn receiver) {
		setupWindow();
		synthName = synth.getSynthName();
		this.sender = sender;
		this.receiver = receiver;
		id = UUID.randomUUID();
		
		for (Parameter param : synth.getParameters()) {
			addParameter(param.name, param.min, param.max, param.value);
		}
		
		setTitle(synthName);

		// Set up Synth Starter
    	ArrayList<Object> arguments = new ArrayList<Object>();
    	arguments.add(id.toString());
    	OSCMessage msg = new OSCMessage("/" + synthName + "/start", arguments);
    	
    	// Send the message
    	try {
			sender.send(msg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public SynthWindow(SynthDef synth, SCLang sc) {
		// TODO Auto-generated constructor stub
	}

	public void addParameter(final String name, float min, float max, float value) {

		JSliderD paramSlider = new JSliderD(JSlider.HORIZONTAL, 5, min, max, value);
		
		paramSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
				JSliderD source = (JSliderD)e.getSource();
				
		    	ArrayList<Object> arguments = new ArrayList<Object>();
		    	arguments.add(id.toString());
		    	arguments.add(source.getDoubleValue());
		    	OSCMessage msg = new OSCMessage("/" + synthName + "/" + name, arguments);
		    	
		    	try {
					sender.send(msg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		middlePanel.add(new JLabel(name));
		middlePanel.add(paramSlider);
		middlePanel.revalidate();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		
		// Set up Synth Starter
    	ArrayList<Object> arguments = new ArrayList<Object>();
    	arguments.add(id.toString());
    	OSCMessage msg = new OSCMessage("/" + synthName + "/stop", arguments);
    	
    	// Send the message
    	try {
			sender.send(msg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void setupWindow() {
		setSize(300, 150);
		setLayout(new BorderLayout());
				
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

}
