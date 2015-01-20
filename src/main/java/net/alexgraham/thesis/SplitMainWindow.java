package net.alexgraham.thesis;

import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.xml.stream.events.StartDocument;

public class SplitMainWindow extends JFrame {
	
	JSplitPane splitPane;
	JPanel sidePanel;
	JPanel mainPanel;
	
	JPanel bottomPanel;
	
	JTextField avgCPUField;
	JTextField peakCPUField;
	
	Hashtable<String, JComponent> scLangComponents;
	
	public SplitMainWindow() {
		start();
	}
}
