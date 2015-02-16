package net.alexgraham.thesis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

import net.alexgraham.thesis.supercollider.SCLang.SCConsoleListener;
import net.alexgraham.thesis.supercollider.SCLang.SCLangProperties;
import net.alexgraham.thesis.supercollider.SCLang.SCMessageListener;
import net.alexgraham.thesis.ui.components.ConsoleDialog;
import net.alexgraham.thesis.ui.components.FlashButton;

public class MainWindow extends JFrame implements SCMessageListener {
	JPanel mainPanel;
	JPanel bottomPanel;
	
	MainSplitLayout splitLayout;

	JTextField avgCPUField;
	JTextField peakCPUField;
	ConsoleDialog consoleDialog;
	
	FlashButton inFlasher;
	FlashButton outFlasher;
	
	final int FLASH_LENGTH = 150;
	
	public MainWindow() throws SocketException {
		setSize(1280, 800);
		setLayout(new BorderLayout());
		createConsoleDialog();
		
		splitLayout = new MainSplitLayout();
		add(splitLayout, BorderLayout.CENTER);
		
		setupBottomPanel();
		
		App.sc.addUpdateListener(SCMessageListener.class, this);
		
		App.sc.createListener("/*", new OSCListener() {
			
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				inFlasher.flash();
			}
		});
		//add(bottomPanel, BorderLayout.PAGE_END);
	}
	
	public void setupBottomPanel() {
		JPanel bottomWrapper = new JPanel();
		bottomWrapper.setLayout(new BoxLayout(bottomWrapper, BoxLayout.X_AXIS));
		
		//Bottom Panel//
		bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		// Blinkers //
		inFlasher = new FlashButton(FLASH_LENGTH, Color.GREEN);
		outFlasher = new FlashButton(FLASH_LENGTH, Color.RED);
		
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
		
		bottomButtons.add(inFlasher);
		bottomButtons.add(outFlasher);
		
		JButton rebootButton = new JButton("Reboot");
		rebootButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					App.sc.rebootServer();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}
		});
		
		bottomWrapper.add(rebootButton);
		
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
	
	// SCMessage Listener
	// --------------------

	@Override
	public void oscMessageOut() {
		outFlasher.flash();
	}

	@Override
	public void messageIn(boolean oscMessage) {
		
		if (!oscMessage) {
			inFlasher.flash(inFlasher.blinkColor.darker().darker().darker());
		}
	}

}
