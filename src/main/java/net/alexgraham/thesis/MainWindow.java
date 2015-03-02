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
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.alexgraham.thesis.supercollider.SCLang.SCConsoleListener;
import net.alexgraham.thesis.supercollider.SCLang.SCLangProperties;
import net.alexgraham.thesis.supercollider.SCLang.SCMessageListener;
import net.alexgraham.thesis.supercollider.SaveHelper;
import net.alexgraham.thesis.ui.components.ConsoleDialog;
import net.alexgraham.thesis.ui.components.FlashButton;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame implements SCMessageListener {
	JPanel mainPanel;
		
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
		
		this.setJMenuBar(createMenuBar());
		//add(bottomPanel, BorderLayout.PAGE_END);
	}
	
	public void setupBottomPanel() {
		
		// Bottom Panel
		JPanel bottomWrapper = new JPanel();
		bottomWrapper.setLayout(new BoxLayout(bottomWrapper, BoxLayout.X_AXIS));
		
		//Bottom Split//
		JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		// Blinkers //
		inFlasher = new FlashButton(FLASH_LENGTH, Color.GREEN);
		outFlasher = new FlashButton(FLASH_LENGTH, Color.RED);
		
		// CPU Fields
		avgCPUField = new JTextField(4);
		peakCPUField = new JTextField(4);
		
		
		bottomRight.add(new JLabel("Avg CPU:"));
		bottomRight.add(avgCPUField);
		bottomRight.add(new JLabel("Peak CPU:"));
		bottomRight.add(peakCPUField);
		
		bottomLeft.add(inFlasher);
		bottomLeft.add(outFlasher);
		
		
		bottomWrapper.add(bottomLeft);
		bottomWrapper.add(bottomRight);
		add(bottomWrapper, BorderLayout.PAGE_END);
		
		//App.sc.addCPUUpdateListener(this);
		//App.sc.addUpdateListener(SCCPUListener.class, this);
		createCPUListeners();
	}
	
	// Menu Bar actions 
	// --------------------------
	
	public void newAction() {
		App.data.clearData();
		this.setTitle("Untitled");
	}
	
	public void saveAction() {
		File file = SaveHelper.chooseFile("Save");
		if (file != null) {
			App.data.saveData(file);
			this.setTitle(file.getName());
		}

	}
	
	public void loadAction() {
		File file = SaveHelper.chooseFile("Load");
		if (file != null) {
			App.data.loadData(file);
			this.setTitle(file.getName());
		}
	}
	
	public void rebootAction() {
		try {
			App.sc.rebootServer();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
	}
	
	public void consoleAction() {
		consoleDialog.openDialog();
	}
	
	// Setup SuperCollider Info 
	// ----------------------------------
	
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
	
	// Create menu bar
	// --------------------- 
	
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu file, menu;
		JMenuItem menuItem;
		
		menuBar = new JMenuBar();
		
		// Build File Menu
		file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
        file.getAccessibleContext().setAccessibleDescription(
                "Opens and closes files, etc");
        menuBar.add(file);
        
        // New File
        menuItem = new JMenuItem("New", KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        
        menuItem.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				newAction();
			}
		});
        file.add(menuItem);
        
        // Open File
        menuItem = new JMenuItem("Open", KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        
        menuItem.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				loadAction();
			}
		});
        file.add(menuItem);
        
        // Save File
        menuItem = new JMenuItem("Save", KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        
        menuItem.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction();
			}
		});
        file.add(menuItem);
        
        // Save As
        menuItem = new JMenuItem("Save As", KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        
        menuItem.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction();
			}
		});
        file.add(menuItem);
        
        file.addSeparator();
        
        // Exit
        menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        
        menuItem.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchEvent(new WindowEvent(MainWindow.this, WindowEvent.WINDOW_CLOSING));
			}
		});
        file.add(menuItem);
        
        // SuperCollider Menu
        // -----------------------
        
        menu = new JMenu("SuperCollider");
        menu.setMnemonic(KeyEvent.VK_S);      
        menuBar.add(menu);
        
        // Show Console
        menuItem = new JMenuItem("Show Console", KeyEvent.VK_C);
        menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				consoleAction();
			}
		});
        menu.add(menuItem);
        
        menu.addSeparator();
        
        // Reboot server
        menuItem = new JMenuItem("Reboot Server", KeyEvent.VK_R);
        menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rebootAction();
			}
		});
        menu.add(menuItem);
        
        return menuBar;
	}
	
	public void setupSCButtons(JPanel bottomWrapper) {
		
		JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JButton consoleButton = new JButton("Console");
		bottomButtons.add(consoleButton);
		
		consoleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				consoleAction();
			}
		});
		
		consoleButton.setMargin(new Insets(0, 0, 0, 0));
		
		JButton rebootButton = new JButton("Reboot");
		rebootButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rebootAction();
			}
		});
		
		bottomWrapper.add(rebootButton);
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction();
			}
		});
		
		bottomWrapper.add(saveButton);
		
		
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadAction();
			}
		});
		
		bottomWrapper.add(loadButton);
	}
	


}
