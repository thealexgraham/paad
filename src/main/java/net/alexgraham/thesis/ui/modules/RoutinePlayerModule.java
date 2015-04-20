package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
import net.alexgraham.thesis.supercollider.synths.grouping.ExportIcon;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamMenuAdapter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class RoutinePlayerModule extends ModulePanel implements PlayerListener {
	
	JPanel topPanel;
	ConnectablePanel bottomPanel;
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
		setInstance(player);

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
		
		JPanel topContentPanel = new JPanel();
		
		topPanel = ModuleFactory.createSideConnectPanel(this, player.getConnector(ConnectorType.ACTION_OUT), topContentPanel);
		
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topContentPanel.add(topLabel);
		
		
		ConnectablePanel topWrapper = new ConnectablePanel();
		topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
		topWrapper.addConnector(Location.TOP, player.getConnector(ConnectorType.PATTERN_IN));
		this.addConnectablePanel(topWrapper);
		topWrapper.add(topPanel);
		
		//Middle Panel//
		middlePanel = new JPanel();
		//middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.setLayout(new GridLayout(0, 2));
		
		

		//scrollPane = new JScrollPane(middlePanel);
		
		
		//Bottom Panel//
		bottomPanel = new ConnectablePanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		// Create connectors //
		bottomPanel.addConnector(Location.BOTTOM, player.getConnector(ConnectorType.INST_PLAY_OUT));
		this.addConnectablePanel(bottomPanel);


		pane.add(topWrapper, BorderLayout.NORTH);
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
		
		middlePanel.setLayout(new GridLayout(0, 1, 5, 5));
		
		middlePanel.add(createButtonPanel(playButton, "play"));
		middlePanel.add(createButtonPanel(closeButton, "stop"));
		middlePanel.add(instLabel);
	
	}
	
	public JPanel createButtonPanel(JButton button, String action) {
		JPanel togetherPanel = new JPanel();
		togetherPanel.setLayout(new BoxLayout(togetherPanel, BoxLayout.LINE_AXIS));
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0 ,0));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		ConnectablePanel leftConnectable = new ConnectablePanel(Location.LEFT, player.getConnector(ConnectorType.ACTION_IN, action.toLowerCase()));
		this.addConnectablePanel(leftConnectable);

		ConnectablePanel rightConnectable = new ConnectablePanel(Location.RIGHT, player.getConnector(ConnectorType.ACTION_IN, action.toLowerCase()));
		this.addConnectablePanel(rightConnectable);

		panel.add(leftConnectable);
		panel.add(button);
		panel.add(rightConnectable);
		
			
//		togetherPanel.add(panel);
		return panel;
		
	}
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		player.close();
	}

	@Override
	public void instrumentConnected(Instrument inst) {
		
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
		
		this.instLabel.setText("Inst: None");
		this.revalidate();
	}

}
