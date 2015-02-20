package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.defs.ChangeFuncDef;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.defs.EffectDef;
import net.alexgraham.thesis.supercollider.synths.defs.InstDef;
import net.alexgraham.thesis.supercollider.synths.defs.SynthDef;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class SynthLauncherPanel extends JPanel {
	
	public interface SynthLauncherDelegate {
		void launchSynth(Def def);
		void addInstrument(InstDef instdef);
		void addEffect(EffectDef effectDef);
	}
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	
	JLabel topLabel;

	JList<Def> synthList;
	JTree tree;
	
	SynthLauncherDelegate delegate;
	
	JButton launchButton;
		
	int lastInt = 0;
	
	public SynthLauncherPanel(SynthLauncherDelegate delegate) throws SocketException {
		this.delegate = delegate;
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
		
		synthList = new JList<Def>(App.defModel.getSynthDefListModel());
		synthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		synthList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				JList theList = (JList)e.getSource();
				if (theList.getSelectedIndex() == -1) {
					// No selection, don't have launch button
					launchButton.setEnabled(false);
				} else {
					launchButton.setEnabled(true);
				}
			}
		});
		
		synthList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList)evt.getSource();
				if (evt.getClickCount() == 2) {
					Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex()); 
					if (r != null && r.contains(evt.getPoint()))
					{ 
						Def selected = synthList.getSelectedValue();
						launchSynth(selected);
					}

				}
			}
		});
		
		// Add Srollplane to it
		JScrollPane listScrollPane = new JScrollPane(synthList);
		middlePanel.add(listScrollPane);
		
		// Launch Button
		launchButton = new JButton("Launch Synth");
		launchButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Def currentSelection = synthList.getSelectedValue();
				launchSynth(currentSelection);
			}
		});
		
		JButton resendButton = new JButton("Resend");
		resendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				synthList.updateUI();
				//App.sc.sendMessage("/start/ready", 1);
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
	
	public void launchSynth(Def def) {
		// Launch the Synth based on the type of Synth
		if (def.getClass() == SynthDef.class) {
			App.synthModel.launchSynth(def);
		} else if (def.getClass() == InstDef.class) {
			App.synthModel.addInstrument( (InstDef)def );
		} else if(def.getClass() == EffectDef.class) {
			App.synthModel.addEffect((EffectDef)def);
		} else if(def.getClass() == ChangeFuncDef.class) {
			App.synthModel.addChangeFunc((ChangeFuncDef)def);
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
