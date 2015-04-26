package net.alexgraham.thesis.ui.modules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.time.Year;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.grouping.ExportIcon;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamMenuAdapter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ChoiceParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ChoiceParamModel.ChoiceChangeListener;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.IntParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class ModuleFactory implements Serializable {
	
	// Side Connect Panel //
	public static JPanel createSideConnectPanel(ModulePanel module, Connector connector, JPanel insidePanel) {
		return createSideConnectPanel(module, connector, connector, insidePanel);
	}

	
	public static JPanel createSideConnectPanel(ModulePanel module, Connector connector, JComponent component) {
		JPanel insidePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
		insidePanel.add(component);
		return ModuleFactory.createSideConnectPanel(module, connector, insidePanel);
	}
	
	public static ConnectablePanel getConnectablePanel(ModulePanel module, Location location, Connector connector, int width, int height) {
		ConnectablePanel connectablePanel = new ConnectablePanel(location, connector);
		module.addConnectablePanel(connectablePanel);
		connectablePanel.setPreferredSize(new Dimension(width, height));
		return connectablePanel;
	}
	
	public static JPanel createSideConnectPanel(ModulePanel module, Connector leftConnector, Connector rightConnector, JPanel insidePanel) {
		
		insidePanel.setOpaque(false);

		ConnectablePanel leftConnectable = new ConnectablePanel(Location.LEFT, leftConnector);
		module.addConnectablePanel(leftConnectable);
		leftConnectable.setOpaque(false);
		leftConnectable.setPreferredSize(new Dimension(5, 5));
		
		ConnectablePanel rightConnectable = new ConnectablePanel(Location.RIGHT, rightConnector);
		module.addConnectablePanel(rightConnectable);
		rightConnectable.setOpaque(false);
		rightConnectable.setPreferredSize(new Dimension(5, 5));
	
		JPanel pane = new JPanel();
    	pane.setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.BOTH;
    	c.weightx = 0.5f;
    	
    	pane.add(leftConnectable, c);
    	
    	c.gridx = 1;
    	pane.add(insidePanel);
    	
    	c.gridx = 2;
    	pane.add(rightConnectable, c);
		return pane;
	}
	
	// Create Model Param Panels //
	public static JPanel createIntParamPanel(ModulePanel module, IntParamModel model) {

		// Set up the panels
		JPanel togetherPanel = new JPanel();
//		togetherPanel.setLayout(new GridLayout(0, 2, 0, 0));
		togetherPanel.setLayout(new GridBagLayout());

		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		
		// Create connectables
		// -----------------------
		ConnectablePanel leftConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.LEFT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);
		
		// Left Connectable 
		ConnectablePanel rightConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.RIGHT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);

		// Setup components
		// -----------------------
		JLabel paramNameLabel = new JLabel(model.getName());
		JSpinner paramSpinner = new JSpinner(model);
		
	    JComponent field = ((JSpinner.DefaultEditor) paramSpinner.getEditor());
	    Dimension prefSize = field.getPreferredSize();
	    prefSize = new Dimension(30, prefSize.height);
	    field.setPreferredSize(prefSize);
	      
		
		// Add to panels
		// -----------------------
		leftPanel.add(leftConnectable);
		leftPanel.add(paramNameLabel);
//		leftPanel.add(Box.createHorizontalStrut(15));

		rightPanel.add(paramSpinner);
		rightPanel.add(rightConnectable);
		
		
		
		// Put it together
		// -----------------------

    	GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.BOTH;
    	c.weightx = 1f;

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.weightx = 0.5f;
		togetherPanel.add(leftPanel, c);
//		togetherPanel.add(Box.createHorizontalStrut(24));

//		c.gridx = 1;
//		c.weightx = 1f;

		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.weightx = 0.5;
		togetherPanel.add(rightPanel, c);
		return togetherPanel; 
			
	}

	public static JPanel createChoiceParamPanel(ModulePanel module, ChoiceParamModel model) {
		// Set up the panels
		JPanel togetherPanel = new JPanel();
		togetherPanel.setLayout(new BoxLayout(togetherPanel, BoxLayout.LINE_AXIS));
	
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		
		// Create connectables
		// -----------------------
		ConnectablePanel leftConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.LEFT, model.getConnector(ConnectorType.CHOICE_CHANGE_IN), 5, 5);
		
		// Left Connectable 
		ConnectablePanel rightConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.RIGHT, model.getConnector(ConnectorType.CHOICE_CHANGE_IN), 5, 5);

		// Setup components
		// -----------------------
		
		JLabel paramNameLabel = new JLabel(model.getName());
		JLabel paramValueLabel = new JLabel(model.getChoiceName());
		
		model.addChoiceChangeListener(new ChoiceChangeListener() {
			@Override
			public void choiceChanged(String newChoice) {
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						paramValueLabel.setText(newChoice);
						togetherPanel.setToolTipText(model.getObjectValue().toString());
						module.refreshSize();
						App.mainWindow.repaint();
					}
				});
				
			}
		});
		
		// Add to panels
		// -----------------------
		leftPanel.add(leftConnectable);
		leftPanel.add(paramNameLabel);
		leftPanel.add(Box.createHorizontalStrut(5));
		
		rightPanel.add(paramValueLabel);
		rightPanel.add(rightConnectable);
		
		// Put it together
		// -----------------------
		togetherPanel.add(leftPanel);
		togetherPanel.add(Box.createHorizontalGlue());
		togetherPanel.add(rightPanel);
		
		togetherPanel.setToolTipText(model.getObjectValue().toString());
		//togetherPanel.setToolTipText("Connects to: " + model.getChoiceType());
		
		return togetherPanel;
	}
	
	public static JPanel createDoubleParamPanel(ModulePanel module, DoubleParamModel model) {
		
//		 Create Panels
//		// ------------------------
		JPanel togetherPanel = new JPanel();
		togetherPanel.setLayout(new BoxLayout(togetherPanel, BoxLayout.LINE_AXIS));
		//		togetherPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0 ,0));
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		

		// Create connectables
		// -----------------------
		ConnectablePanel leftConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.LEFT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);

		ConnectablePanel rightConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.RIGHT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);
		
		// Create Components
		// ----------------------
		JLabel paramNameLabel = new JLabel(model.getName());
		JLabel paramValueLabel = new JLabel(String.format("%.2f", model.getDoubleValue()));
		model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				paramValueLabel.setText(String.format("%.2f", model.getDoubleValue()));
			}
		});
		
		// Dial Components
		DialD dial = new DialD(model);
		dial.setForcedSize(new Dimension(15, 15));
		dial.setDrawText(false);

		//Add to panels
		// -----------------------
		
		
		// Left Panel
		leftPanel.add(leftConnectable);
		leftPanel.add(new JLabel(new ExportIcon(model)));
		leftPanel.add(Box.createHorizontalStrut(2));
		leftPanel.add(paramNameLabel);
		leftPanel.add(Box.createHorizontalStrut(7));

		// Right Panel
		rightPanel.add(paramValueLabel);
		rightPanel.add(Box.createHorizontalStrut(3));
		rightPanel.add(dial);
		rightPanel.add(rightConnectable);
		
		// Put it together
		// -----------------------
		togetherPanel.add(leftPanel);
		togetherPanel.add(Box.createHorizontalGlue());
		togetherPanel.add(rightPanel);
		
		dial.addMouseListener(new ParamMenuAdapter((ParamModel) model));
		togetherPanel.addMouseListener(new ParamMenuAdapter((ParamModel)model));
		togetherPanel.setBackground(Color.red);
		togetherPanel.setOpaque(true);
		//    	togetherPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		togetherPanel.setPreferredSize(new Dimension(togetherPanel.getPreferredSize().width + 20, togetherPanel.getPreferredSize().height));
		return togetherPanel;
	}

	
	// Create Param Model Panels Together //
	public static void addDoubleParamPanel(ModulePanel module, DoubleParamModel model, JPanel panel, GridBagConstraints c) {
		
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0 ,0));
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		

		// Create connectables
		// -----------------------
		ConnectablePanel leftConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.LEFT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);

		ConnectablePanel rightConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.RIGHT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);
		
		// Create Components
		// ----------------------
		JLabel paramNameLabel = new JLabel(model.getName());
		JLabel paramValueLabel = new JLabel(String.format("%.2f", model.getDoubleValue()));
		model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				paramValueLabel.setText(String.format("%.2f", model.getDoubleValue()));
			}
		});
		
		// Dial Components
		DialD dial = new DialD(model);
		dial.setForcedSize(new Dimension(15, 15));
		dial.setDrawText(false);

		//Add to panels
		// -----------------------
		
		
		// Left Panel
		leftPanel.add(leftConnectable);
		leftPanel.add(new JLabel(new ExportIcon(model)));
		leftPanel.add(Box.createHorizontalStrut(2));
		leftPanel.add(paramNameLabel);
		leftPanel.add(Box.createHorizontalStrut(7));

		// Right Panel
		rightPanel.add(paramValueLabel);
		rightPanel.add(Box.createHorizontalStrut(3));
		rightPanel.add(dial);
		rightPanel.add(rightConnectable);
		rightPanel.add(Box.createHorizontalStrut(1));
		
		// Put it together
		// -----------------------
		fillPanel(leftPanel, rightPanel, panel, c);
	}
	
	public static void addIntParamPanel(ModulePanel module, IntParamModel model, JPanel panel, GridBagConstraints c) {

		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		
		// Create connectables
		// -----------------------
		ConnectablePanel leftConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.LEFT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);
		
		// Left Connectable 
		ConnectablePanel rightConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.RIGHT, model.getConnector(ConnectorType.PARAM_CHANGE_IN), 5, 5);

		// Setup components
		// -----------------------
		JLabel paramNameLabel = new JLabel(model.getName());
		JSpinner paramSpinner = new JSpinner(model);
		
	    JComponent field = ((JSpinner.DefaultEditor) paramSpinner.getEditor());
	    Dimension prefSize = field.getPreferredSize();
	    prefSize = new Dimension(30, prefSize.height);
	    field.setPreferredSize(prefSize);
		
		// Add to panels
		// -----------------------
		leftPanel.add(leftConnectable);
		leftPanel.add(paramNameLabel);
		leftPanel.add(Box.createHorizontalStrut(10));

		rightPanel.add(paramSpinner);
		rightPanel.add(rightConnectable);
		
		fillPanel(leftPanel, rightPanel, panel, c);
			
	}
	
	public static void addChoiceParamPanel(ModulePanel module, ChoiceParamModel model, JPanel panel, GridBagConstraints c) {

		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		
		// Create connectables
		// -----------------------
		ConnectablePanel leftConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.LEFT, model.getConnector(ConnectorType.CHOICE_CHANGE_IN), 5, 5);
		
		// Left Connectable 
		ConnectablePanel rightConnectable = 
				ModuleFactory.getConnectablePanel(module, Location.RIGHT, model.getConnector(ConnectorType.CHOICE_CHANGE_IN), 5, 5);

		// Setup components
		// -----------------------
		
		JLabel paramNameLabel = new JLabel(model.getName());
		JLabel paramValueLabel = new JLabel(model.getChoiceName());
		
		model.addChoiceChangeListener(new ChoiceChangeListener() {
			@Override
			public void choiceChanged(String newChoice) {
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						paramValueLabel.setText(newChoice);
						rightPanel.setToolTipText(model.getObjectValue().toString());
						module.refreshSize();
						App.mainWindow.repaint();
					}
				});
				
			}
		});
		
		// Add to panels
		// -----------------------
		leftPanel.add(leftConnectable);
		leftPanel.add(paramNameLabel);
		leftPanel.add(Box.createHorizontalStrut(5));
		
		rightPanel.add(paramValueLabel);
		rightPanel.add(rightConnectable);
		
		// Put it together
		// -----------------------
		fillPanel(leftPanel, rightPanel, panel, c);
	}
	
	public static void fillPanel(JPanel leftPanel, JPanel rightPanel, JPanel panel, GridBagConstraints c) {
		// Put it together
		// -----------------------
    	c.fill = GridBagConstraints.BOTH;

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
    	c.weightx = 0.5f;
		panel.add(leftPanel, c);

		c.anchor = GridBagConstraints.EAST;
		c.gridx = 2;
		c.weightx = 0.5;
		panel.add(rightPanel, c);
	}
	
	public static void addModelParameters(ArrayList<ParamModel> models, ModulePanel module, JPanel pane) {
		JPanel paramPanel = new JPanel();
//		paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
		paramPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		int numModels = models.size();
		int i = 1;
//		c.gridy = 0;
		for (ParamModel baseModel : models) {

			if (baseModel.getClass() == IntParamModel.class) {
				IntParamModel model = (IntParamModel) baseModel;
				ModuleFactory.addIntParamPanel(module, model, paramPanel, c);
			} else if (baseModel.getClass() == ChoiceParamModel.class) {
				ChoiceParamModel model = (ChoiceParamModel) baseModel;
				ModuleFactory.addChoiceParamPanel(module, model, paramPanel, c);
			} else if (baseModel.getClass() == DoubleParamModel.class) {
				DoubleParamModel model = (DoubleParamModel) baseModel;
				ModuleFactory.addDoubleParamPanel(module, model, paramPanel, c);
			}
			
			if (i < numModels) {
		    	c.insets = new Insets(0, 0, 0, 0);
				c.gridx = 0;
				c.gridwidth = 3;
				c.fill = GridBagConstraints.BOTH;
				paramPanel.add(new JSeparator(), c);
				c.gridwidth = 1;
			}
			i++;
//			c.gridy = y++;
		}
		
		pane.add(paramPanel);
	}
	
	
	public static ImageIcon getScaledIcon(URL url, int width, int height) {
		try {
			
		    BufferedImage sourceImage;
		    
		    sourceImage = ImageIO.read(url);
			
		    if (sourceImage == null) {
		    	return null;
		    }
		    
		    //BufferedImage negative = op.filter(sourceImage, null);
//		    invertImage(sourceImage);
		    Image newImage = sourceImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		    
		    return new ImageIcon(newImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
//	private static BufferedImage invertImage(BufferedImage source) {
//		short[] data = new short[256];
//		for (short i = 0; i < 256; i++) {
//		    data[i] = (short) (255 - i);
//		}
//
//		LookupTable lookupTable = new ShortLookupTable(0, data);
//		LookupOp op = new LookupOp(lookupTable, null);
//		return op.filter(source, null);
//	}


	private static void invertImage(BufferedImage inputFile) {
		
		 for (int x = 0; x < inputFile.getWidth(); x++) {
	            for (int y = 0; y < inputFile.getHeight(); y++) {
	                int rgba = inputFile.getRGB(x, y);
	                Color col = new Color(rgba, true);
	                col = new Color(255 - col.getRed(),
	                                255 - col.getGreen(),
	                                255 - col.getBlue(), 
	                                col.getAlpha());
	                
	                inputFile.setRGB(x, y, col.getRGB());
	            }
	        }

	}

		 
}
