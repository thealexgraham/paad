package net.alexgraham.thesis;

import java.io.IOException;
import java.net.SocketException;

import javax.swing.JFrame;

import net.alexgraham.thesis.supercollider.OSC;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.models.DefModel;
import net.alexgraham.thesis.supercollider.models.PlayerModel;
import net.alexgraham.thesis.supercollider.models.SynthModel;


public class App 
{
	static final int SC_PORT = 53120;
	static final int JAVA_PORT = 1294;
	
	public static SCLang sc;
	public static DefModel defModel;
	public static SynthModel synthModel;
	public static PlayerModel playerModel;
	
    public static void main( String[] args ) throws IOException
    {
		OSC.start(SC_PORT, JAVA_PORT);
		
    	sc = new SCLang(SC_PORT, JAVA_PORT);
		sc.startSCLang();
		defModel = new DefModel();
		synthModel = new SynthModel();
		playerModel = new PlayerModel();
		
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					openSelectorWindow();
				} catch (SocketException e) {
					e.printStackTrace();
				}
            }
        });
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     * @throws SocketException 
     */
    private static void openSelectorWindow() throws SocketException {
        //Create and set up the window.
        JFrame frame = new MainWindow() {
        	@Override
        	public void dispose() {
        		super.dispose();
            	App.sc.stopSCLang();
        	}
        };
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
//        JComponent newContentPane = new SynthSelector();
//        newContentPane.setOpaque(true); //content panes must be opaque
//        frame.setContentPane(newContentPane);

        //Display the window.
        //frame.pack();
        frame.setVisible(true);
    }

}
