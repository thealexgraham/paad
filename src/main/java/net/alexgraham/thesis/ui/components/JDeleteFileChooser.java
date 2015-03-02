package net.alexgraham.thesis.ui.components;

/*
* @see http://stackoverflow.com/a/17622050/2534090
* @author Gowtham Gutha
*/
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class JDeleteFileChooser extends JFrame
{
	JButton jb;
	JFileChooser jf;
	File[] selectedFiles;
    public JDeleteFileChooser()
    {
        // Create and show GUI
        createAndShowGUI();
    }

    private void createAndShowGUI()
    {
        // Set frame properties
        setTitle("Delete through JFileChooser");
        setLayout(new FlowLayout());
        setSize(400,400);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create JFileChooser
        jf=new JFileChooser();

        // Allow multiple selection
        jf.setMultiSelectionEnabled(true);

        // Create JButton
        jb=new JButton("Open JFileChooser");

        // Add ActionListener to it
        jb.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae)
            {
                // Show the file chooser
                showFileChooser();
            }
        });

        // Register the delete action
        registerDelAction();

        // Add JButton jb to JFrame
        add(jb);
    }

    private void showFileChooser()
    {
        // Show the open dialog
        int op=jf.showOpenDialog(this);
    }

    private void registerDelAction()
    {
        // Create AbstractAction
        // It is an implementation of javax.swing.Action
        AbstractAction a=new AbstractAction(){

            // Write the handler
            public void actionPerformed(ActionEvent ae)
            {
                JFileChooser jf=(JFileChooser)ae.getSource();
                try
                {

                // Get the selected files
                selectedFiles=jf.getSelectedFiles();

                    // If some file is selected
                    if(selectedFiles!=null)
                    {
                        // If user confirms to delete
                        if(askConfirm()==JOptionPane.YES_OPTION)
                        {

                        // Call Files.delete(), if any problem occurs
                        // the exception can be printed, it can be
                        // analysed
                        for(File f:selectedFiles)
                        java.nio.file.Files.delete(f.toPath());

                        // Rescan the directory after deletion
                        jf.rescanCurrentDirectory();
                        }
                    }
                }catch(Exception e){
                    System.out.println(e);
                }
            }
        };

        // Get action map and map, "delAction" with a
        jf.getActionMap().put("delAction",a);

        // Get input map when jf is in focused window and put a keystroke DELETE
        // associate the key stroke (DELETE) (here) with "delAction"
        jf.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"),"delAction");
    }

    public int askConfirm()
    {
        // Ask the user whether he/she wants to confirm deleting
        // Return the option chosen by the user either YES/NO
        return JOptionPane.showConfirmDialog(this,"Are you sure want to delete this file?","Confirm",JOptionPane.YES_NO_OPTION);
    }

    public static void main(String args[])
    {
        SwingUtilities.invokeLater(new Runnable(){
            public void run()
            {
                new JDeleteFileChooser();
            }
        });
    }
}
