package net.alexgraham.thesis.supercollider.synths.parameters.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamGroup;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParam;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class IntParamModel extends SpinnerNumberModel implements ParamModel, Connectable, Serializable {
	
	private String name;
	private Instance owner;
	
	private ParamGroup exportGroup = null;
	
	public IntParamModel(int value, int min, int max) {
		super(value, min, max, 1);
		addConnector(ConnectorType.PARAM_CHANGE_IN);
	}
	
	public Instance getOwner() { return owner; }
	public void setOwner(Instance owner) { 
		this.owner = owner; 
		App.sc.createListener("/" + owner.getID() + "/" + this.getName() + "/change", new OSCListener() {
			
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				// TODO Auto-generated method stub
				System.out.println("Getting a message");
    			List<Object> arguments = message.getArguments();
    			Number newValue = AGHelper.convertToInt(arguments.get(0));
    			if (newValue != getValue())
    				setValue(newValue);
			}
		});
	}
	

	@Override
	public void updateBounds(Param newParam) {
		IntParam param = (IntParam) newParam;
		setMinimum(param.getMin());
		setMaximum(param.getMax());
	}
	
	public Object getObjectValue() {
		return getValue();
	}
	
	@Override
	public void addInstanceListener(Instance instance) {
		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				instance.changeParameter(getName(), getObjectValue());
			}
		});
	}
	
	// Connectors
	// -------------------
	
	EnumMap<ConnectorType, Connector> connectors = new EnumMap<ConnectorType, Connector>(ConnectorType.class);
	public Connector getConnector(ConnectorType type) {
		return connectors.get(type); //TODO: might not be the best way to do this
	}
	
	public void addConnector(ConnectorType type) {
		connectors.put(type, new Connector(this, type));
	}
	
	public void removeConnectorUIs () {
		for (Connector connector : connectors.values()) {
			connector.removeConnectorUIs();
		}
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean connect(Connection connection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disconnect(Connection connection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParamGroup getExportGroup() {
		return exportGroup;
	}

	@Override
	public void setExportGroup(ParamGroup paramGroup) {
		this.exportGroup = paramGroup;
	}




}
