package net.alexgraham.thesis.ui.modules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.webkit.InspectorClient;

import net.alexgraham.thesis.supercollider.synths.grouping.ExportIcon;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamMenuAdapter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class ModuleFactory {
	public static JPanel createSideConnectPanel(ModulePanel module, Connector connector, JPanel insidePanel) {
		
		insidePanel.setOpaque(false);

		ConnectablePanel leftConnectable = new ConnectablePanel(Location.LEFT, connector);
		module.addConnectablePanel(leftConnectable);
		leftConnectable.setOpaque(false);
		leftConnectable.setPreferredSize(new Dimension(5, 5));
		
		ConnectablePanel rightConnectable = new ConnectablePanel(Location.RIGHT, connector);
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
	
	public static JPanel createSideConnectPanel(ModulePanel module, Connector connector, JComponent component) {
		JPanel insidePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
		insidePanel.add(component);
		return ModuleFactory.createSideConnectPanel(module, connector, insidePanel);
	}
	
	public static JPanel createDoubleParamPanel(ModulePanel module, DoubleParamModel model) {
		
		JPanel togetherPanel = new JPanel();
		togetherPanel.setLayout(new BoxLayout(togetherPanel, BoxLayout.LINE_AXIS));
		
		
		
		JPanel paramPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0 ,0));
		
		//paramPanel.add(paramValueLabel);
		ConnectablePanel leftConnectable = new ConnectablePanel(Location.LEFT, model.getConnector(ConnectorType.PARAM_CHANGE_IN));
		module.addConnectablePanel(leftConnectable);
		leftConnectable.setPreferredSize(new Dimension(5, 5));
		
		JLabel paramNameLabel = new JLabel(model.getName());
		JLabel paramValueLabel = new JLabel(String.format("%.2f", model.getDoubleValue()));
		model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				paramValueLabel.setText(String.format("%.2f", model.getDoubleValue()));
			}
		});

		paramPanel.add(leftConnectable);
		paramPanel.add(new JLabel(new ExportIcon(model)));
		paramPanel.add(paramNameLabel);

		paramPanel.add(Box.createHorizontalStrut(7));
		

		
		ConnectablePanel rightConnectable = new ConnectablePanel(Location.RIGHT, model.getConnector(ConnectorType.PARAM_CHANGE_IN));
		paramPanel.add(rightConnectable);
		module.addConnectablePanel(rightConnectable);
		rightConnectable.setPreferredSize(new Dimension(5, 5));
		
		
		JPanel dialPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		DialD dial = new DialD(model);
		dial.setForcedSize(new Dimension(15, 15));
		dial.setDrawText(false);
		
		dialPanel.add(paramValueLabel);
		dialPanel.add(Box.createHorizontalStrut(3));
		dialPanel.add(dial);
		dialPanel.add(rightConnectable);

		//dialPanel.add(paramNameLabel);
		
		dial.addMouseListener(new ParamMenuAdapter((ParamModel) model));
		togetherPanel.addMouseListener(new ParamMenuAdapter((ParamModel)model));
		togetherPanel.add(paramPanel);
		togetherPanel.add(Box.createHorizontalGlue());
		togetherPanel.add(dialPanel);
//    	togetherPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		return togetherPanel;
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
