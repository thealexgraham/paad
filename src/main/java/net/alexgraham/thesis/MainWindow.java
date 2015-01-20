package net.alexgraham.thesis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainWindow extends JFrame {
	JPanel mainPanel;
	JPanel bottomPanel;
	
	MainSplitLayout splitLayout;

	JTextField avgCPUField;
	JTextField peakCPUField;

	
	public MainWindow() throws SocketException {
		setSize(1000, 500);
		setLayout(new BorderLayout());
		
		splitLayout = new MainSplitLayout();
		add(splitLayout, BorderLayout.CENTER);
		
		setupBottomPanel();
		add(bottomPanel, BorderLayout.PAGE_END);
		
	}
	
	public void setupBottomPanel() {
		//Bottom Panel//
		bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		// CPU Fields
		avgCPUField = new JTextField(4);
		peakCPUField = new JTextField(4);
		
		bottomPanel.add(new JLabel("Avg CPU:"));
		bottomPanel.add(avgCPUField);
		bottomPanel.add(new JLabel("Peak CPU:"));
		bottomPanel.add(peakCPUField);
		
		App.sc.setAvgCPUField(avgCPUField);
		App.sc.setPeakCPUField(peakCPUField);
	}
	
}
