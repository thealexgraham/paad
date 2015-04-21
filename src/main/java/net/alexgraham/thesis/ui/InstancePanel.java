package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.grouping.ExportIcon;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamMenuAdapter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.IntParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.components.Dial;
import net.alexgraham.thesis.ui.components.DialD;


public class InstancePanel extends JPanel  {
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	JScrollPane scrollPane;
	
	JLabel topLabel;
	
	JButton timeButton;
	JButton stringButton;
	
	JTextField stringField;
	
	JTextArea timeArea;
	
	Instance instance;
	
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
	 * @param instance
	 * @param sc
	 */
	public InstancePanel(Instance instance) {

		this.instance = instance;
		
		setupWindow();
		JPanel dialsPanel = new JPanel();
		dialsPanel.setLayout(new GridLayout(0, 2));
		for (ParamModel model : instance.getParamModels()) {
			if (model.getClass() == DoubleParamModel.class) {
				addDial( dialsPanel, (DoubleParamModel)model );
			} else if (model.getClass() == IntParamModel.class) {
				addSpinner((IntParamModel) model);
			}

		}
		middlePanel.add(dialsPanel);
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				instance.close();
			}
		});
		JTextArea functionArea = new JTextArea();
		functionArea.setText(instance.getDef().getFunctionString());
		functionArea.setTabSize(1);
		middlePanel.add(functionArea);
		middlePanel.add(closeButton);
		setPreferredSize(getPreferredSize());
		revalidate();
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
//		middlePanel.setLayout(new FlowLayout());
//		middlePanel.setLayout(new GridLayout(0, 2));
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
	
	public Instance getInstance() {
		return this.instance;
	}
	
//	public Synth getSynth() {
//		return this.instance;
//	}
	
	public void addDial(JPanel pane, DoubleParamModel model) {
		
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
		pane.add(dialPanel);
		pane.revalidate();
	
		//sliders.put(param.getName(), paramSlider);
	}
	
public void addSpinner(IntParamModel model) {
		
		JPanel spinnerPanel = new JPanel(new GridLayout(1, 0));

		JSpinner paramSpinner = new JSpinner(model);

		//paramDial.addMouseListener(new ParamMenuAdapter((ParamModel) model));
		
		//middlePanel.add(paramDial);
		spinnerPanel.add(paramSpinner);
		
		JPanel labelPanel = new JPanel();
		labelPanel.add(new JLabel(model.getName()));
		labelPanel.add(new JLabel(new ExportIcon(model)));
		labelPanel.addMouseListener(new ParamMenuAdapter((ParamModel) model));
		spinnerPanel.add(labelPanel);
		middlePanel.add(spinnerPanel);
		middlePanel.revalidate();
	
		//sliders.put(param.getName(), paramSlider);
	}




}
