package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang.SCServerListener;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.supercollider.synths.ChangeFuncDef;
import net.alexgraham.thesis.supercollider.synths.EffectDef;
import net.alexgraham.thesis.supercollider.synths.InstDef;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.SynthDef;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class TreeLauncherPanel extends JPanel {
	
	public interface SynthLauncherDelegate {
		void launchSynth(SynthDef synthDef);
		void addInstrument(InstDef instdef);
		void addEffect(EffectDef effectDef);
	}
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	
	JLabel topLabel;

	JList<SynthDef> synthList;
	JTree tree;
	
	SynthLauncherDelegate delegate;
	
	JButton launchButton;
		
	int lastInt = 0;
	
	public TreeLauncherPanel() throws SocketException {
		start();
		System.out.println("Starting");
	}
	
	public void start() {
		
		// Set up window
		setSize(300, 150);
		setLayout(new BorderLayout());
		setupLayout();		
		
		middlePanel.add(new JLabel("Available instruments"));	
		
		// SynthList Setup
		// -------------------
		//synthListModel = new DefaultListModel<String>();
		
		tree = new JTree(App.launchTreeModel.getTreeModel());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
        tree.expandPath(new TreePath(App.launchTreeModel.getRoot().getPath()));
//        tree.expandRow(0);
//        tree.setRootVisible(false);
//        tree.setShowsRootHandles(true);
       
        
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if(selRow != -1) {
                    if(e.getClickCount() == 1) {
                      //  mySingleClick(selRow, selPath);
                    }
                    else if(e.getClickCount() == 2) {
                        //myDoubleClick(selRow, selPath);
                    	DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selPath.getLastPathComponent();
                    	
                    	if (selectedNode.getUserObject() instanceof SynthDef) {
                        	SynthDef selected = (SynthDef) selectedNode.getUserObject();
                        	launchSynth(selected);
                    	}

                    }
                }
            }
        };
        tree.addMouseListener(ml);
        
        // Pull out the trees when the server is ready
        App.sc.addUpdateListener(SCServerListener.class, new SCServerListener() {
			
			@Override
			public void serverReady() {
				
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				
				System.out.println("Doing it");
		        for (int i = 0; i < tree.getRowCount(); i++) {
		            tree.expandRow(i);
		        }
			}
		});
        
		
		// Add Srollplane to it
		JScrollPane listScrollPane = new JScrollPane(tree);
		middlePanel.add(listScrollPane);
		
		// Launch Button
		launchButton = new JButton("Launch Synth");
		launchButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				SynthDef currentSelection = synthList.getSelectedValue();
				launchSynth(currentSelection);
			}
		});
		
		JButton resendButton = new JButton("Resend");
		resendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//synthList.updateUI();
				//App.sc.sendMessage("/start/ready", 1);
		        for (int i = 0; i < tree.getRowCount(); i++) {
		            tree.expandRow(i);
		        }
			
			}
		});
		
		JButton playerButton = new JButton("Routine Player");
		playerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				RoutinePlayer player = new RoutinePlayer();
				App.playerModel.addPlayer(player);
			}
		});
		
		middlePanel.add(launchButton);
		middlePanel.add(resendButton);
		middlePanel.add(playerButton);
	}
	
	public void launchSynth(SynthDef synthDef) {
		// Launch the Synth based on the type of Synth
		if (synthDef.getClass() == SynthDef.class) {
			App.synthModel.launchSynth(synthDef);
		} else if (synthDef.getClass() == InstDef.class) {
			App.synthModel.addInstrument( (InstDef)synthDef );
		} else if(synthDef.getClass() == EffectDef.class) {
			App.synthModel.addEffect((EffectDef)synthDef);
		} else if(synthDef.getClass() == ChangeFuncDef.class) {
			App.synthModel.addChangeFunc((ChangeFuncDef)synthDef);
		}
	}
	
	
	private void setupLayout() {
		
		//Top Panel//
		
		topPanel = new JPanel(new FlowLayout());
		topLabel = new JLabel("Instruments");
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		
		middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

		//Bottom Panel//

		bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);

		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

}
