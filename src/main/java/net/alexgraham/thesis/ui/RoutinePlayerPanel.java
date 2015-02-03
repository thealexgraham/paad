package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import net.alexgraham.thesis.supercollider.Instrument;
import net.alexgraham.thesis.supercollider.RoutinePlayer;
import net.alexgraham.thesis.supercollider.RoutinePlayer.PlayState;
import net.alexgraham.thesis.supercollider.RoutinePlayer.PlayerListener;

public class RoutinePlayerPanel extends JPanel implements PlayerListener {
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	JScrollPane scrollPane;
	
	JLabel topLabel;
	
	RoutinePlayer player;
	
	String synthName;
	
	JButton playButton;
	
	int lastInt = 0;
	
	class PopUpDemo extends JPopupMenu {
	    JMenuItem anItem;
	    public PopUpDemo(){
	        anItem = new JMenuItem("Click Me!");
	        anItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("It was done");
				}
			});
	        add(anItem);
	    }
	}

	
	
	public RoutinePlayerPanel(RoutinePlayer player) {

		this.player = player;

		player.addListener(this);
		setupWindow();
		
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
				player.close();
			}
		});
		
		middlePanel.add(playButton);
		middlePanel.add(closeButton);
		setPreferredSize(getPreferredSize());
		//revalidate();
	}
	
	

	public void setupWindow() {
		setSize(300, 150);
		setLayout(new BorderLayout());
				
		//Top Panel//
		
		topPanel = new JPanel(new FlowLayout());
		topLabel = new JLabel("Instruments");
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel = new JPanel();
		//middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.setLayout(new GridLayout(0, 2));
		scrollPane = new JScrollPane(middlePanel);
		
				
		//Bottom Panel//

		bottomPanel = new JPanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);

		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);	
	}

	@Override
	public void instrumentConnected(Instrument inst) {
		// TODO Auto-generated method stub
		
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

}
