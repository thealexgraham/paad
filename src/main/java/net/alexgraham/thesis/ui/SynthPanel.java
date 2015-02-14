package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.alexgraham.thesis.supercollider.synths.Parameter;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.ui.components.Dial;
import net.alexgraham.thesis.ui.components.DialD;


public class SynthPanel extends JPanel  {
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	JScrollPane scrollPane;
	
	JLabel topLabel;
	
	JButton timeButton;
	JButton stringButton;
	
	JTextField stringField;
	
	JTextArea timeArea;
	
	Synth synth;
	
	String synthName;

	
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

	
	
	/**
	 * Constructs a SynthPanel, note that this does NOT launch the synth!
	 * @param synth
	 * @param sc
	 */
	public SynthPanel(Synth synth) {

		this.synth = synth;
		
		setupWindow();
		
		// Go through each parameter and add a slider for it
		for (Parameter param : synth.getParameters()) {
			System.out.println("Adding parameter " + param.getName());
			addParameter(param);
		}
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synth.close();
			}
		});
		
		middlePanel.add(closeButton);
		setPreferredSize(getPreferredSize());
		revalidate();
	}
	
	public Synth getSynth() {
		return this.synth;
	}
	
	public void addParameter(final Parameter param) {

//		JSliderD paramSlider = new JSliderD(JSlider.HORIZONTAL, 
//				getSynth().getModelForParameterName(param.getName()));
//		middlePanel.add(new JLabel(param.name));
//		middlePanel.add(paramSlider);
		
		DialD paramDial = new DialD(getSynth().getModelForParameterName(param.getName()));
		paramDial.setName(param.getName());
		if (param.getName() == "pan") {
			paramDial.setBehavior(Dial.Behavior.CENTER);
		}
		
		paramDial.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    public void mouseReleased(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    private void doPop(MouseEvent e){
		        PopUpDemo menu = new PopUpDemo();
		        menu.show(e.getComponent(), e.getX(), e.getY());
		    }

		});
		
		//middlePanel.add(paramDial);
		middlePanel.add(paramDial);
		middlePanel.revalidate();
	
		//sliders.put(param.getName(), paramSlider);
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

}
