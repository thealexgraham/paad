package net.alexgraham.thesis;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.alexgraham.thesis.ui.SynthCardsPanel;
import net.alexgraham.thesis.ui.TreeLauncherPanel;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;

public class MainSplitLayout extends JPanel {
	
	JSplitPane mainSplitPane;
	JSplitPane sideSplitPane;
	
	TreeLauncherPanel synthSelector;
	
	JPanel mainCardPanel;

	LineConnectPanel lineConnect;
	

	public MainSplitLayout() throws SocketException {
		setSize(1024, 800);
		setupLayout();
		
		lineConnect = new LineConnectPanel();
		synthSelector = new TreeLauncherPanel();
		
		App.data.setLineConnectPanel(lineConnect);
		
		JScrollPane scroller = new JScrollPane(lineConnect);
		
		sideSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				synthSelector, scroller);

		
		sideSplitPane.setResizeWeight(0.8);
		sideSplitPane.setDividerLocation(250);
		
		sideSplitPane.setOneTouchExpandable(true);
		
		
		SynthCardsPanel cardsPanel = new SynthCardsPanel();
		cardsPanel.setMaximumSize(new Dimension(160, 100));
		lineConnect.addSynthSelectListener(cardsPanel);
		
		JSplitPane wholeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideSplitPane, cardsPanel);
		wholeSplitPane.setDividerLocation(1000);
		//add(sideSplitPane);
		add(wholeSplitPane);
		
		lineConnect.setFocusable(true);
		lineConnect.requestFocusInWindow();
	}

	public void saveLineConnect() throws IOException {
    	// Write to disk with FileOutputStream
		lineConnect.saveModules();
	}
	
	public void loadLineConnect() throws IOException, ClassNotFoundException {
    	// Write to disk with FileOutputStream
		lineConnect.loadModules();
	}
	
	private void setupLayout() {
		setLayout(new GridLayout());
	}

}
