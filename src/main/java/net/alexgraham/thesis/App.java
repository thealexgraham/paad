package net.alexgraham.thesis;

import java.io.IOException;
import java.net.SocketException;
import javax.swing.JFrame;

import net.alexgraham.thesis.supercollider.OSC;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.ui.SynthSelector;


public class App 
{
	static final int SC_PORT = 53120;
	static final int JAVA_PORT = 1294;
	
	static SCLang sc;

    public static void main( String[] args ) throws IOException
    {
		OSC.start(SC_PORT, JAVA_PORT);
		
    	sc = new SCLang(SC_PORT, JAVA_PORT);
		sc.startSCLang();
		
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
        JFrame frame = new SynthSelector(sc) {
        	@Override
        	public void dispose() {
        		super.dispose();
        		System.out.println("Disposing");
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
