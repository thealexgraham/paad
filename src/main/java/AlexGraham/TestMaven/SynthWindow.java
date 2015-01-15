package AlexGraham.TestMaven;

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

import AlexGraham.TestMaven.SynthDef.Parameter;
import AlexGraham.TestMaven.supercollider.SCLang;

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
	
	public SynthWindow(SynthDef synth, SCLang sc) {
		this.sc = sc;
		this.synthName = synth.getSynthName();
		this.id = UUID.randomUUID();
		
		setupWindow();
	
		for (Parameter param : synth.getParameters()) {
			addParameter(param.name, param.min, param.max, param.value);
		}
		
		sc.sendMessage("/" + synthName + "/start", id.toString());
		
		setTitle(synthName);
	}

	public void addParameter(final String paramName, float min, float max, float value) {

		JSliderD paramSlider = new JSliderD(JSlider.HORIZONTAL, 5, min, max, value);
		
		paramSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
				JSliderD source = (JSliderD)e.getSource();
		    	
				// Send the parameter info to SuperCollider
		    	sc.sendMessage("/" + synthName + "/" + paramName, 
		    			id.toString(), source.getDoubleValue());
		    	
			}
		});
		
		middlePanel.add(new JLabel(paramName));
		middlePanel.add(paramSlider);
		middlePanel.revalidate();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();

		// Stop the synth at ID
    	sc.sendMessage("/" + synthName + "/stop", id.toString());
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
