package net.alexgraham.thesis.ui.modules;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.players.PatternPlayer;
import net.alexgraham.thesis.supercollider.players.PatternPlayer.PlayState;
import net.alexgraham.thesis.supercollider.players.PatternPlayer.PlayerListener;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class PatternPlayerModule extends ModulePanel {
	
	JScrollPane scrollPane;
	
	JLabel topLabel;
	
	PatternPlayer player;
	
	String synthName;
	
	JButton playButton;
	JLabel instLabel = new JLabel();
	JLabel patternLabel = new JLabel();
	
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
						PatternPlayerModule.this.instLabel.setText("Inst: " + instrument.getSynthName());
						PatternPlayerModule.this.player.connectInstrument(instrument);
						PatternPlayerModule.this.revalidate();
					}
				});
		        add(instItem);
			}
	    }
	}

	
	
	public PatternPlayerModule(PatternPlayer player) {
		super();
		
		this.player = player;
		setInstance(player);

		//player.addListener(this);
		setupWindow(this.getInterior());
		setSize(getPreferredSize());
	
	}
	
	@Override
	public void setupPanels(ConnectablePanel topPanel,
			ConnectablePanel middlePanel,
			ConnectablePanel bottomPanel) {
		// TODO Auto-generated method stub
				
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		
		middlePanel.add(ModuleFactory.createSideConnectPanel(this, player.getConnector(ConnectorType.ACTION_OUT), new JLabel("Played Action")));
		
		middlePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		createButtons(middlePanel);

		middlePanel.add(ModuleFactory.createSideConnectPanel(this, player.getConnector(ConnectorType.ACTION_IN, "play"), new JLabel("play")));
		middlePanel.add(ModuleFactory.createSideConnectPanel(this, player.getConnector(ConnectorType.ACTION_IN, "stop"), new JLabel("stop")));
		
		ModuleFactory.addModelParameters(player.getParamModels(), this, middlePanel);
		
//		patternLabel = new JLabel("Pattern: None");
//		middlePanel.add(ModuleFactory.createSideConnectPanel(this, player.getConnector(ConnectorType.PATTERN_IN), patternLabel));
//		
////		middlePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
//
//		
//		instLabel = new JLabel("Inst: None");
//		middlePanel.add(ModuleFactory.createSideConnectPanel(this, player.getConnector(ConnectorType.INST_PLAY_OUT), instLabel));
		
		bottomPanel.addConnector(Location.BOTTOM, player.getConnector(ConnectorType.INST_PLAY_OUT), this);

		//scrollPane = new JScrollPane(middlePanel);
	}
	

	public void createButtons(JPanel panel) {
		JButton playButton;
		playButton = new JButton("Play");
		playButton.setEnabled(false);
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				player.playOrPause();
			}
		});
		
		player.addListener(new PlayerListener() {
			
			@Override
			public void playStateChanged(PlayState state) {
				System.out.println("Module got play state changed" + state);
				playButton.setText("L");
				playButton.paintImmediately(playButton.getVisibleRect());
				switch (state) {
					case READY:
						playButton.setEnabled(true);
						playButton.setText("Play");
						break;
					case PLAYING:
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
		});
		
		player.playStateChange();
		
		panel.add(ModuleFactory.createSideConnectPanel(this, player.getConnector(ConnectorType.ACTION_IN, "playbutton"), playButton));	
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		player.close();
	}	

}
