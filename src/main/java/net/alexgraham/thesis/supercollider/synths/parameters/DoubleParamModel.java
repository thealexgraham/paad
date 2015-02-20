package net.alexgraham.thesis.supercollider.synths.parameters;

import java.util.Date;
import java.util.List;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;

public class DoubleParamModel extends DoubleBoundedRangeModel implements Connectable {
	
	private String name;
	private Synth owner;

	public DoubleParamModel(int decimals, double min, double max, double value) {
		super(decimals, min, max, value);
		
		// TODO Auto-generated constructor stub
	}
	
	public Synth getOwner() { return owner; }
	public void setOwner(Synth owner) { 
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

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	// Old
	@Override
	public boolean disconnect(Connector thisConnector, Connector targetConnector) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean connect(Connector thisConnector, Connector targetConnector) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean connectWith(Connectable otherConnectable) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeConnectionWith(Connectable otherConnectable) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
