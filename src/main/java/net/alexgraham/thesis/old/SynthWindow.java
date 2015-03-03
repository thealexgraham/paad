package net.alexgraham.thesis.ui.old;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.defs.Def.Parameter;
import net.alexgraham.thesis.ui.components.JSliderD;

public class SynthWindow extends JFrame {
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	
	JLabel topLabel;
	
	JButton timeButton;
	JButton stringButton;
	
	JTextField stringField;
	
	JTextArea timeArea;
	
	SCLang sc;
	Synth synth;
	
	String synthName;
	
	int lastInt = 0;
	
	public SynthWindow(Synth synth, SCLang sc) {
		this.sc = sc;
		this.synth = synth;
		
		setupWindow();
		
		// Go through each parameter and add a slider for it
		for (Parameter param : synth.getParameters()) {
			addParameter(param);
		}
		
		synth.start();
		
		setTitle(synth.getSynthName());
	}

	public void addParameter(final Parameter param) {

		JSliderD paramSlider = new JSliderD(JSlider.HORIZONTAL, 5, param.min, param.max, param.value);
		
		paramSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSliderD source = (JSliderD)e.getSource();
		    	synth.changeParameter(param.name, source.getDoubleValue());
			}
		});
		
		middlePanel.add(new JLabel(param.name));
		middlePanel.add(paramSlider);
		middlePanel.revalidate();
	}

	@Override
	public void dispose() {
		super.dispose();
		synth.close();
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
				
		//Bottom Panel//

		bottomPanel = new JPanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);

		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);	
	}

}
