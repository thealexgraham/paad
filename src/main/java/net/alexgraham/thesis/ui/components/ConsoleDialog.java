package net.alexgraham.thesis.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.sun.xml.internal.ws.wsdl.writer.document.OpenAtts;

import net.alexgraham.thesis.supercollider.SCLang.SCConsoleListener;
import net.alexgraham.thesis.supercollider.SCLang.SCUpdateListener;

public class ConsoleDialog extends JDialog implements SCConsoleListener {
	private JTextArea consoleArea;
	private JScrollPane consolePane;
	
	public ConsoleDialog(JFrame frame, String title) {
		// TODO Auto-generated constructor stub
		super(frame, title);
		
        //Add contents to it. It must have a close button,
        //since some L&Fs (notably Java/Metal) don't provide one
        //in the window decorations for dialogs.

    	
		consoleArea = new JTextArea(15, 50);
		consolePane = new JScrollPane();
		consolePane.setViewportView(consoleArea);
		

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
		// TODO Auto-generated method stub
		//consoleArea.append(consoleLine+"\n");
		consoleArea.append("  " + consoleLine+"\n"); // Write to the console window
		consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
	}

}
