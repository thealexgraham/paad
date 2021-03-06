package net.alexgraham.thesis.supercollider.synths.parameters.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.sun.org.apache.bcel.internal.generic.NEW;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamGroup;
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParam;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class DoubleParamModel extends DoubleBoundedRangeModel implements ParamModel, Connectable, Serializable {
	
	private String name;
	private Instance owner;
	private ParamGroup exportGroup = null;
	
	public DoubleParamModel(int decimals, double min, double max, double value) {
		super(decimals, min, max, value);
		addConnector(ConnectorType.PARAM_CHANGE_IN);
		// TODO Auto-generated constructor stub
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
    			final float newValue = AGHelper.convertToFloat(arguments.get(0));
    			if (newValue != getDoubleValue())
    				setDoubleValue(newValue);
			}
		});
	}
	
	public Object getObjectValue() {
		return getDoubleValue();
	}
	
	@Override
	public void updateBounds(Param newParam) {
		DoubleParam param = (DoubleParam) newParam;
		setDoubleMinimum(param.getMin());
		setDoubleMaximum(param.getMax());
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
	public ParamGroup getExportGroup() {
		return exportGroup;
	}

	@Override
	public void setExportGroup(ParamGroup paramGroup) {
		this.exportGroup = paramGroup;
	}
	
	
	// Connectable
	// --------------
	
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
	
}
