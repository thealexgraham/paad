package net.alexgraham.thesis.ui.modules;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.supercollider.synths.grouping.ParamMenuAdapter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class ModuleFactory {
	public static JPanel createDoubleParamPanel(ModulePanel module, DoubleParamModel model) {
		JLabel paramNameLabel = new JLabel(model.getName());
		JLabel paramValueLabel = new JLabel(String.valueOf(model.getDoubleValue()));
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
		//paramPanel.add(paramValueLabel);
		ConnectablePanel leftConnectable = new ConnectablePanel(Location.LEFT, model.getConnector(ConnectorType.PARAM_CHANGE_IN));

		JPanel dialPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		DialD dial = new DialD(model);
		dial.setForcedSize(new Dimension(15, 15));
		dial.setDrawText(false);
		dialPanel.add(leftConnectable);
		module.addConnectablePanel(leftConnectable);
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
}
