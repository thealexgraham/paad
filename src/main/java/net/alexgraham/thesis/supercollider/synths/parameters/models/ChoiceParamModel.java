package net.alexgraham.thesis.supercollider.synths.parameters.models;

import java.io.Serializable;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeListener;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamGroup;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class ChoiceParamModel implements Serializable, ParamModel, Connectable {

	public interface ChoiceChangeListener {
		public void choiceChanged(String newChoice);
	}
	private Instance owner;
	private String name;
	
	public String choiceName;
	public Object[] choiceArray;
	public Object choiceValue;
	
	public String choiceType = "";
	
	private ParamGroup exportGroup = null;
	
	private CopyOnWriteArrayList<ChoiceChangeListener> listeners = new CopyOnWriteArrayList<ChoiceParamModel.ChoiceChangeListener>();

	public ChoiceParamModel(String choiceName, Object choiceValue) {
		this.choiceName = choiceName;
		this.choiceValue = choiceValue;
		addConnector(ConnectorType.CHOICE_CHANGE_IN);
	}
	
	public ChoiceParamModel(String choiceName, Object choiceValue, String choiceType) {
		this.choiceName = choiceName;
		this.choiceValue = choiceValue;
		this.choiceType = choiceType;
		addConnector(ConnectorType.CHOICE_CHANGE_IN);
	}
	
	public ChoiceParamModel(String choiceName, Object[] choiceArray) {
		this.choiceName = choiceName;
		this.choiceArray = Arrays.copyOf(choiceArray, choiceArray.length);
		addConnector(ConnectorType.CHOICE_CHANGE_IN);
	}
	
	public String getChoiceType() { return choiceType; }
	public void setChoiceType(String choiceType) { this.choiceType = choiceType; }
	

	@Override
	public Object getObjectValue() {
		return choiceValue;
		//return choiceArray[0];
	}
	

	@Override
	public void updateBounds(Param newParam) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addInstanceListener(Instance instance) {
		//
	}
	
	public void addChoiceChangeListener(ChoiceChangeListener listener) {
		listeners.add(listener);
	}
	
	public void fireChoiceChangeUpdate() {
		for (ChoiceChangeListener listener : listeners) {
			listener.choiceChanged(this.choiceName);
		}
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
	
	public String getChoiceName() {
		return choiceName;
	}
	
	public void setChoiceName(String choiceName) {
		this.choiceName = choiceName;
		fireChoiceChangeUpdate();
	}
	
	public Instance getOwner() {
		return this.owner;
	}
	
	public void setOwner(Instance owner) {
		this.owner = owner;
		
		App.sc.createListener("/" + owner.getID() + "/" + this.getName() + "/change", new OSCListener() {
			
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				// TODO Auto-generated method stub
				System.out.println("Getting a message");
    			List<Object> arguments = message.getArguments();
    			final String newValue = (String) arguments.get(0);
   				setChoiceName(newValue);;
			}
		});
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean connect(Connection connection) {
		return false;
	}

	@Override
	public boolean disconnect(Connection connection) {
		return false;
	}


	@Override
	public ParamGroup getExportGroup() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setExportGroup(ParamGroup paramGroup) {
		// TODO Auto-generated method stub
		
	}



}
