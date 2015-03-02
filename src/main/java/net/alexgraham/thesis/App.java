package net.alexgraham.thesis;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.JFrame;

import net.alexgraham.thesis.supercollider.OSC;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.SCLang.SCServerListener;
import net.alexgraham.thesis.supercollider.models.ConnectionModel;
import net.alexgraham.thesis.supercollider.models.DataModel;
import net.alexgraham.thesis.supercollider.models.DefModel;
import net.alexgraham.thesis.supercollider.models.LaunchTreeModel;
import net.alexgraham.thesis.supercollider.models.PlayerModel;
import net.alexgraham.thesis.supercollider.models.SynthModel;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.ui.connectors.Connection;


public class App 
{
	static final int SC_PORT = 53120;
	static final int JAVA_PORT = 1295;
	
	public static SCLang sc;
	public static LaunchTreeModel launchTreeModel;
	public static DefModel defModel;
	public static SynthModel synthModel;
	public static PlayerModel playerModel;
	public static ConnectionModel connectionModel;
	
	public static DataModel data;
	
    public static void main( String[] args ) throws IOException
    {
		OSC.start(SC_PORT, JAVA_PORT);
		
    	sc = new SCLang(SC_PORT, JAVA_PORT);
		sc.startSCLang();
		
		data = new DataModel();
		
		defModel = data.getDefModel();
		synthModel = data.getSynthModel();
		playerModel = data.getPlayerModel();
		launchTreeModel = data.getLaunchTreeModel();
		connectionModel = data.getConnectionModel();
		
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
