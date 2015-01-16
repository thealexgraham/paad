package net.alexgraham.thesis.main;

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

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.ui.components.JSliderD;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class TestApplet extends JApplet implements ActionListener, FocusListener {
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	
	JLabel topLabel;
	
	JButton timeButton;
	JButton stringButton;
	
	JTextField stringField;
	
	JTextArea timeArea;
	
	OSCPortIn receiver;
	OSCPortOut sender;
	
	int lastInt = 0;
	
	public void start() {
		
		System.out.println("Starting");
		try {
			createListeners();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		try {
			sender = new OSCPortOut(InetAddress.getLocalHost(), 57120);
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
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
	
		JSliderD testSlider = new JSliderD(JSlider.HORIZONTAL, 5, 0.0, 1.0, 0.5);
		testSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSliderD source = (JSliderD)e.getSource();
				System.out.println(source.getValue());
				System.out.println(source.getDoubleValue());
			}
		});
		
		middlePanel.add(new JLabel("test slider"));
		middlePanel.add(testSlider);
				
		//Bottom Panel//

		bottomPanel = new JPanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);

		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		
		
		testSlider = new JSliderD(JSlider.HORIZONTAL, 5, 0.0, 1.0, 0.5);
		testSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSliderD source = (JSliderD)e.getSource();
				System.out.println(source.getValue());
				System.out.println(source.getDoubleValue());
			}
		});
		
		middlePanel.add(new JLabel("invisible label"));
		
	}
	
	public void createListeners() throws SocketException {

		receiver = new OSCPortIn(1253);
		final JApplet thisApplet = this;
		
    	OSCListener addParamListener = new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			JSlider paramSlider;
    			
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			final String paramName = (String) arguments.get(1); 
       			
    			if (arguments.get(2).getClass() == Float.class) {
        			float min = (Float) arguments.get(2);
        			float max = (Float) arguments.get(3);
        			float value = (Float) arguments.get(4);
        			
        			paramSlider = new JSliderD(JSlider.HORIZONTAL, 5, min, max, value);
        			
        			paramSlider.addChangeListener(new ChangeListener() {
        				public void stateChanged(ChangeEvent e) {
        					
        					JSliderD source = (JSliderD)e.getSource();
        					
        			    	ArrayList<Object> arguments = new ArrayList<Object>();
        			    	arguments.add(source.getDoubleValue());
        			    	OSCMessage msg = new OSCMessage("/" + synthName + "/" + paramName, arguments);
        			    	
        			    	try {
								sender.send(msg);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
        				}
        			});
    			} else {
        			int min = (Integer) arguments.get(2);
        			int max = (Integer) arguments.get(3);
        			int value = (Integer) arguments.get(4);
        			
        			paramSlider = new JSlider(JSlider.HORIZONTAL, min, max, value);
        			
        			paramSlider.addChangeListener(new ChangeListener() {
        				public void stateChanged(ChangeEvent e) {

        					JSlider source = (JSlider)e.getSource();
        					
        			    	ArrayList<Object> arguments = new ArrayList<Object>();
        			    	arguments.add(source.getValue());
        			    	OSCMessage msg = new OSCMessage("/" + synthName + "/" + paramName, arguments);
        			    	
        			    	try {
								sender.send(msg);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
        				}
        			});
    			}

    			
    			middlePanel.add(new JLabel(paramName));
    			middlePanel.add(paramSlider);
    			middlePanel.revalidate();
    		}
    	};

    	receiver.addListener("/addparam", addParamListener);
    	receiver.startListening();
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
	public void stop() {
    	receiver.close();
	}

	public void focusGained(FocusEvent fv) {
		if (fv.getSource() == stringField) {
			stringField.setText("");
		}
		
	}

	public void focusLost(FocusEvent e) {
		
	}

}
