package net.alexgraham.thesis.supercollider.synths;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.Synth.SynthListener;
import net.alexgraham.thesis.supercollider.synths.defs.ChooserDef;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ChoiceParamModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class Chooser extends Instance implements Serializable, Connectable {
	
	private ArrayList<String> choices;
	private String currentChoice;
	private int currentIndex = 0;
	
	private CopyOnWriteArrayList<SynthListener> synthListeners =
			new CopyOnWriteArrayList<Synth.SynthListener>();
	
	public Chooser(Def def) {
		super(def);

		// Create default values
		init();
	}
	
	public void init() {
		
		this.choices = ((ChooserDef) def).getChoices();
		currentChoice = choices.get(0);
		
		startCommand = "/chooser/add";
		paramChangeCommand = "/chooser/paramc";
		closeCommand = "/chooser/remove";
		
		addConnector(ConnectorType.CHOICE_CHANGE_OUT);
	}
	
	@Override
	public Object[] getStartArguments() {
		// Create the arguments list for this Synth
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add(def.getDefName());
    	arguments.add(id.toString());
    	arguments.add(currentIndex);

    	return arguments.toArray();
	}
	
	public ArrayList<String> getChoices() {
		return choices;
	}
		
	public void addSynthListener(SynthListener listener) {
		synthListeners.add(listener);
	}
	
	@Override
	public void start() {
    	App.sc.sendMessage(startCommand, getStartArguments());
	}
	
	@Override
	public void stop() {
		// Stop the synth at ID
    	App.sc.sendMessage(closeCommand, def.getDefName(), id.toString());
	}
	
	@Override
	public void close() {
		// Stop the synth at ID
    	App.sc.sendMessage(closeCommand, def.getDefName(), id.toString());
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public void choose(String choice) {
		choose(choices.indexOf(choice));
	}
	
	public void choose(int index) {
		currentIndex = index;
		App.sc.sendMessage("/chooser/choose", this.getName(), this.getID(), index);
	}
	
	public boolean connectToParameter(ChoiceParamModel param) {
		String returnType = this.getDef().getReturnType();
		String choiceType = param.getChoiceType();
		if (!returnType.equals(choiceType) || choiceType.equals("any")) {
			JOptionPane.showMessageDialog(null, "Could not connect Chooser with" + returnType + " to Param with" + choiceType);
			return false;
		}
		System.out.println("Trying connect " + returnType + " to " + choiceType);
		Instance owner = param.getOwner();
		App.sc.sendMessage("/chooser/connect/param", this.getName(), this.getID(), owner.getName(), owner.getID(), param.getName());
		return true;
	}
	
	public void disconnectFromParameter(ChoiceParamModel param) {
		Instance owner = param.getOwner();
		App.sc.sendMessage("/chooser/disconnect/param", this.getName(), this.getID(), owner.getName(), owner.getID(), param.getName());
	}
	
	@Override
	public boolean connect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();

		// Instrument output into Effect input, valid, check if connectors are correct
		if (connection.isConnectionType(this, ConnectorType.CHOICE_CHANGE_OUT, ConnectorType.CHOICE_CHANGE_IN)) {
			if (target.getClass() == ChoiceParamModel.class) {
				return connectToParameter((ChoiceParamModel) target);
			}	
		}
		
		return false;
	}


	@Override
	public boolean disconnect(Connection connection) {
		
		Connectable target = connection.getTargetConnector(this).getConnectable();

		// Instrument output into Effect input, valid, check if connectors are correct
		if (connection.isConnectionType(this, ConnectorType.CHOICE_CHANGE_OUT, ConnectorType.CHOICE_CHANGE_IN)) {
			if (target.getClass() == ChoiceParamModel.class) {
				disconnectFromParameter((ChoiceParamModel) target);
				return true;
			}	
		}
		
		return false;
	}

}
