package net.alexgraham.thesis.ui.modules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
		
		ConnectablePanel rightConnectable = new ConnectablePanel(Location.RIGHT, connector);
		module.addConnectablePanel(rightConnectable);
		rightConnectable.setOpaque(false);
	
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
	
	public static JPanel createDoubleParamPanel(ModulePanel module, DoubleParamModel model) {
		JLabel paramNameLabel = new JLabel(model.getName());
		JLabel paramValueLabel = new JLabel(String.format("%.2f", model.getDoubleValue()));
		model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				paramValueLabel.setText(String.format("%.2f", model.getDoubleValue()));
			}
		});
		
//			JPanel togetherPanel = new JPanel(new GridLayout(1, 0, 0, 0));
		JPanel togetherPanel = new JPanel();
		togetherPanel.setLayout(new BoxLayout(togetherPanel, BoxLayout.LINE_AXIS));
		
		JPanel paramPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0 ,0));

		paramPanel.add(paramNameLabel);
		paramPanel.add(new JLabel(new ExportIcon(model)));
		
		//paramPanel.add(paramValueLabel);
		ConnectablePanel leftConnectable = new ConnectablePanel(Location.LEFT, model.getConnector(ConnectorType.PARAM_CHANGE_IN));
		module.addConnectablePanel(leftConnectable);
		
		JPanel dialPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		DialD dial = new DialD(model);
		dial.setForcedSize(new Dimension(15, 15));
		dial.setDrawText(false);
		dialPanel.add(leftConnectable);

		dialPanel.add(dial);
		dialPanel.add(paramValueLabel);
		//dialPanel.add(paramNameLabel);
		
		ConnectablePanel connectablePanel = new ConnectablePanel(Location.RIGHT, model.getConnector(ConnectorType.PARAM_CHANGE_IN));
		paramPanel.add(connectablePanel);
		module.addConnectablePanel(connectablePanel);
		
		dialPanel.add(Box.createHorizontalStrut(15));
		
		dial.addMouseListener(new ParamMenuAdapter((ParamModel) model));
		togetherPanel.addMouseListener(new ParamMenuAdapter((ParamModel)model));
		togetherPanel.add(dialPanel);
		togetherPanel.add(Box.createHorizontalGlue());
		togetherPanel.add(paramPanel);
		
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
