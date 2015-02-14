package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer.PlayState;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer.PlayerListener;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.ui.components.MovablePanel;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;
import net.alexgraham.thesis.ui.connectors.Connector.Location;

public class RoutinePlayerModule extends MovablePanel implements PlayerListener {
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	JScrollPane scrollPane;
	
	JLabel topLabel;
	
	RoutinePlayer player;
	
	String synthName;
	
	JButton playButton;
	JLabel instLabel;
	
	int lastInt = 0;
	
	class InstDefPopup extends JPopupMenu {

	    public InstDefPopup(){
	    	ArrayList<Instrument> instruments = App.synthModel.getInstruments();
	    	for (Instrument instrument : instruments) {
	    	    JMenuItem instItem;
		        instItem = new JMenuItem(instrument.getSynthName());
		        instItem.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						//player.connectInstrument(instDef);
						RoutinePlayerModule.this.instLabel.setText("Inst: " + instrument.getSynthName());
						RoutinePlayerModule.this.player.connectInstrument(instrument);
						RoutinePlayerModule.this.revalidate();
					}
				});
		        add(instItem);
			}
	    }
	}

	
	
	public RoutinePlayerModule(RoutinePlayer player) {
		super();
		
		this.player = player;

		player.addListener(this);
		setupWindow(this.getInterior());
		setSize(getPreferredSize());
	
		//setPreferredSize(getPreferredSize());
		//revalidate();
	}
	
	

	public void setupWindow(Container pane) {
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());
				
		//Top Panel//
		
		topPanel = new JPanel(new FlowLayout());
		topLabel = new JLabel("Routine Player");
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel = new JPanel();
		//middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.setLayout(new GridLayout(0, 2));
		//scrollPane = new JScrollPane(middlePanel);
		
				
		//Bottom Panel//

		bottomPanel = new JPanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		// Create connectors //
		ConnectablePanel connectablePanel = new ConnectablePanel(Location.BOTTOM, player, "Instrument");
		bottomPanel.add(connectablePanel);
		this.addConnectablePanel(connectablePanel);

		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);	
		
		createButtons();
	}
	
	public void createButtons() {
		playButton = new JButton("Play");
		playButton.setEnabled(false);
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				player.playOrPause();
			}
		});
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				player.close();
//				LineConnectPanel connectPanel = (LineConnectPanel) RoutinePlayerModule.this.getParent();
//				connectPanel.removeModule(RoutinePlayerModule.this);
				removeSelf();
			}
			
		});
		
		instLabel = new JLabel("Inst: None");
		instLabel.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    public void mouseReleased(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    private void doPop(MouseEvent e){
		        InstDefPopup menu = new InstDefPopup();
		        menu.show(e.getComponent(), e.getX(), e.getY());
		    }

		});
		
		middlePanel.add(playButton);
		middlePanel.add(closeButton);
		middlePanel.add(instLabel);
	}
	@Override
	public void removeSelf() {
		// TODO Auto-generated method stub
		super.removeSelf();
		player.close();
	}

	@Override
	public void instrumentConnected(Instrument inst) {
		// TODO Auto-generated method stub
		this.instLabel.setText("Inst: " + inst.getSynthName());
		this.revalidate();
	}

	@Override
	public void playStateChanged(PlayState state) {
		System.out.println("Got play state changed");
		playButton.setText("L");
		playButton.paintImmediately(playButton.getVisibleRect());
		switch (state) {
			case READY:
				System.out.println("Setting to play");
				playButton.setEnabled(true);
				playButton.setText("Play");
				break;
			case PLAYING:
				System.out.println("Setting to stop");
				playButton.setEnabled(true);
				playButton.setText("Stop");
				break;
			case DISABLED:
				playButton.setEnabled(false);
				playButton.setText("Play");
				break;
			default:
				break;
		}
	}



	@Override
	public void instrumentDisconnected(Instrument inst) {
		// TODO Auto-generated method stub
		this.instLabel.setText("Inst: None");
		this.revalidate();
	}

}
