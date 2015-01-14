package net.alexgraham.homework1;

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

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainApplet extends JApplet implements ActionListener, FocusListener {
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	
	JLabel topLabel;
	
	JButton timeButton;
	JButton stringButton;
	
	JTextField stringField;
	
	JTextArea timeArea;
	
	int lastInt = 0;
	
	public void start() {
		
		setSize(300, 150);
		setLayout(new BorderLayout());
				
		//Top Panel//
		
		topPanel = new JPanel(new FlowLayout());
		topLabel = new JLabel("Hello I am Alex Graham's first Applet");
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		
		middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		
		//middlePanel.setComponentOrientation(ComponentOrientation.);
		
		timeButton = new JButton("Show Time");
		timeArea = new JTextArea("This is a text area");

		timeButton.addActionListener(this);
		//timeField.setMinimumSize(new Dimension(timeField.getSize().height, 600));

		middlePanel.add(timeArea);
		middlePanel.add(timeButton);
				
		//Bottom Panel//

		bottomPanel = new JPanel(new FlowLayout());
		
		stringField = new JTextField("Enter number here");
		stringButton = new JButton("Multiply by 2");
		
		stringField.setPreferredSize(new Dimension(150, 30));
		
		stringField.addFocusListener(this);
		
		stringButton.addActionListener(this);
		
		bottomPanel.add(stringButton);
		bottomPanel.add(stringField);
	
		topPanel.setBackground(Color.DARK_GRAY);
		middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);

		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		
	}

	@Override
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

	@Override
	public void focusGained(FocusEvent fv) {
		// TODO Auto-generated method stub
		if (fv.getSource() == stringField) {
			stringField.setText("");
		}
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

}
