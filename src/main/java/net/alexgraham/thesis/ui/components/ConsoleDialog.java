package net.alexgraham.thesis.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang.SCConsoleListener;

public class ConsoleDialog extends JDialog implements SCConsoleListener {
	private JTextArea consoleArea;
	private JScrollPane consolePane;
	private String lastCommand;
	
	public ConsoleDialog(JFrame frame, String title) {
		
		super(frame, title);
		
        //Add contents to it. It must have a close button,
        //since some L&Fs (notably Java/Metal) don't provide one
        //in the window decorations for dialogs.

		consoleArea = new JTextArea(25, 75);
		consolePane = new JScrollPane();
		consolePane.setViewportView(consoleArea);
		consoleArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		

    	consoleArea.setForeground(Color.white);
    	consoleArea.setBackground(Color.BLACK);
		
		JTextField commandLine = new JTextField(1000);
		commandLine.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
            	lastCommand = commandLine.getText();
                    App.sc.sendCommand(commandLine.getText());
                    commandLine.setText("");

            }});
		commandLine.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					commandLine.setText(lastCommand);
				}
			}
		});
		

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConsoleDialog.this.setVisible(false);
                //ConsoleDialog.this.dispose();
            }
        });
        
        JPanel closePanel = new JPanel();
        closePanel.setLayout(new BoxLayout(closePanel,
                                           BoxLayout.LINE_AXIS));
        closePanel.add(commandLine);
        closePanel.add(Box.createHorizontalGlue());
        closePanel.add(closeButton);
        closePanel.setBorder(BorderFactory.
            createEmptyBorder(0,0,5,5));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(consolePane, BorderLayout.CENTER);
        
        contentPane.add(closePanel, BorderLayout.PAGE_END);
        contentPane.setOpaque(true);
        this.setContentPane(contentPane);
	}
	
	public void openDialog() {
		this.setVisible(true);
		consoleArea.append(" ");
		consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
	}

	@Override
	public void consoleUpdate(String consoleLine) {
		
		//consoleArea.append(consoleLine+"\n");
		consoleArea.append("  " + consoleLine+"\n"); // Write to the console window
		consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
	}

}
