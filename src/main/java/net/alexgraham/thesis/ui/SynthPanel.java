package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.Synth;
import net.alexgraham.thesis.supercollider.Synth.SynthListener;
import net.alexgraham.thesis.supercollider.SynthDef;
import net.alexgraham.thesis.supercollider.SynthDef.Parameter;
import net.alexgraham.thesis.ui.components.JSliderD;


public class SynthPanel extends JPanel implements SynthListener {
	
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
	
	Hashtable<String, JSliderD> sliders;
	
	int lastInt = 0;

	
	
	/**
	 * Constructs a SynthPanel, note that this does NOT launch the synth!
	 * @param synth
	 * @param sc
	 */
	public SynthPanel(Synth synth) {

		this.synth = synth;
		synth.addSynthListener(this);
		sliders = new Hashtable<String, JSliderD>();
		
		setupWindow();
		
		// Go through each parameter and add a slider for it
		for (Parameter param : synth.getParameters()) {
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

		JSliderD paramSlider = new JSliderD(JSlider.HORIZONTAL, 4, param.min, param.max, param.value);
		
		paramSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSliderD source = (JSliderD)e.getSource();
		    	synth.changeParameter(param.name, source.getDoubleValue());
			}
		});
		
		middlePanel.add(new JLabel(param.name));
		middlePanel.add(paramSlider);
		middlePanel.revalidate();
	
		sliders.put(param.getName(), paramSlider);
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
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
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
	public void parameterChanged(String paramName, double value) {
		JSliderD paramSlider = sliders.get(paramName);
		paramSlider.setDoubleValue(value);
	}

	@Override
	public void synthClosed(Synth synth) {
		// TODO Auto-generated method stub
		
	}

}
