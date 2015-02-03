package net.alexgraham.thesis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.SocketException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.alexgraham.thesis.supercollider.SCLang.SCConsoleListener;
import net.alexgraham.thesis.supercollider.SCLang.SCLangProperties;
import net.alexgraham.thesis.ui.components.ConsoleDialog;

public class MainWindow extends JFrame {
	JPanel mainPanel;
	JPanel bottomPanel;
	
	MainSplitLayout splitLayout;

	JTextField avgCPUField;
	JTextField peakCPUField;
	ConsoleDialog consoleDialog;
	
	public MainWindow() throws SocketException {
		setSize(1000, 500);
		setLayout(new BorderLayout());
		createConsoleDialog();
		
		splitLayout = new MainSplitLayout();
		add(splitLayout, BorderLayout.CENTER);
		
		setupBottomPanel();
		//add(bottomPanel, BorderLayout.PAGE_END);
	}
	
	public void setupBottomPanel() {
		JPanel bottomWrapper = new JPanel();
		bottomWrapper.setLayout(new BoxLayout(bottomWrapper, BoxLayout.X_AXIS));
		
		//Bottom Panel//
		bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		// CPU Fields
		avgCPUField = new JTextField(4);
		peakCPUField = new JTextField(4);
		
		bottomPanel.add(new JLabel("Avg CPU:"));
		bottomPanel.add(avgCPUField);
		bottomPanel.add(new JLabel("Peak CPU:"));
		bottomPanel.add(peakCPUField);

		JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton consoleButton = new JButton("Console");
		
		consoleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				consoleDialog.openDialog();
			}
		});
		
		consoleButton.setMargin(new Insets(0, 0, 0, 0));
		bottomButtons.add(consoleButton);
		
		bottomWrapper.add(bottomButtons);
		bottomWrapper.add(bottomPanel);
				
		add(bottomWrapper, BorderLayout.PAGE_END);
		
		//App.sc.addCPUUpdateListener(this);
		//App.sc.addUpdateListener(SCCPUListener.class, this);
		createCPUListeners();
	}
	
	public void createCPUListeners() {

		App.sc.addPropertyChangeListener(SCLangProperties.avgCPU, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				avgCPUField.setText(String.valueOf(evt.getNewValue()) + "%");
			}
		});
		
		App.sc.addPropertyChangeListener(SCLangProperties.peakCPU, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				peakCPUField.setText(String.valueOf(evt.getNewValue()) + "%");
			}
		});
	}

	public void createConsoleDialog() {
		JFrame mainFrame = (JFrame) SwingUtilities.getRoot(this);
        //Create the dialog.
       consoleDialog = 
        		new ConsoleDialog(mainFrame, "Console Dialog");
        App.sc.addUpdateListener(SCConsoleListener.class, consoleDialog);
        
        //Show it.
        consoleDialog.setSize(new Dimension(600, 300));
        consoleDialog.setLocationRelativeTo(mainFrame);
      
	}

}
