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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.grouping.ExportGroupMenu;
import net.alexgraham.thesis.supercollider.synths.grouping.ExportIcon;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamMenuAdapter;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
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
		
		for (ParamModel model : synth.getParamModels()) {
			if (model.getClass() == DoubleParamModel.class) {
				addDial( (DoubleParamModel)model );
			}

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
	
	public void addDial(DoubleParamModel model) {
		
		JPanel dialPanel = new JPanel(new GridLayout(0, 1));
		
		DialD paramDial = new DialD(model);
		//paramDial.setName(model.getName());
		if (model.getName() == "pan") {
			paramDial.setBehavior(Dial.Behavior.CENTER);
		}
		
		paramDial.addMouseListener(new ParamMenuAdapter((ParamModel) model));
		
		//middlePanel.add(paramDial);
		dialPanel.add(paramDial);
		
		JPanel labelPanel = new JPanel();
		labelPanel.add(new JLabel(model.getName()));
		labelPanel.add(new JLabel(new ExportIcon(model)));
		labelPanel.addMouseListener(new ParamMenuAdapter((ParamModel) model));
		dialPanel.add(labelPanel);
		middlePanel.add(dialPanel);
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
