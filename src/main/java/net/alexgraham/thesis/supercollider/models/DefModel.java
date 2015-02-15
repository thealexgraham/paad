package net.alexgraham.thesis.supercollider.models;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.EffectDef;
import net.alexgraham.thesis.supercollider.synths.InstDef;
import net.alexgraham.thesis.supercollider.synths.SynthDef;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class DefModel {
	
	private DefaultListModel<SynthDef> synthListModel 
		= new DefaultListModel<SynthDef>();
	
	private Hashtable<String, SynthDef> synthdefs = new Hashtable<String, SynthDef>();

	
	public DefModel() throws SocketException {
		createListeners();
	}

	public DefaultListModel<SynthDef> getSynthDefListModel() {
		return synthListModel;
	}
	
	public ArrayList<InstDef> getInstDefs() {
		ArrayList<InstDef> instDefs = new ArrayList<InstDef>();
		
		for (Enumeration<SynthDef> e = synthListModel.elements(); e.hasMoreElements();)  {
			SynthDef synthDef = e.nextElement();
			if (synthDef.getClass() == InstDef.class) {
				instDefs.add((InstDef) synthDef);
			}
		}
		
		return instDefs;
	}
	
	public void createListeners() throws SocketException {
		
    	App.sc.createListener("/synthdef/add", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			SynthDef synth = new SynthDef(synthName, App.sc);
    			synthdefs.put(synthName, synth);
    			synthListModel.addElement(synth);
    			// Also Add To The List
    		}
    	});
    	
    	App.sc.createListener("/instdef/add", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			//SynthDef synth = new SynthDef(synthName, App.sc);
    			InstDef synth = new InstDef(synthName, App.sc);
    			synthdefs.put(synthName, synth);
    			synthListModel.addElement(synth);
    			
    			// Also Add To The List
    		}
    	});
    	
    	App.sc.createListener("/effectdef/add", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			//SynthDef synth = new SynthDef(synthName, App.sc);
    			EffectDef effectDef = new EffectDef(synthName, App.sc);
    			synthdefs.put(synthName, effectDef);
    			synthListModel.addElement(effectDef);
    			
    			// Also Add To The List
    		}
    	});
    	
    	OSCListener paramlistener = new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			final String paramName = (String) arguments.get(1);
    			
    			final float min = AGHelper.convertToFloat(arguments.get(2));
    			final float max = AGHelper.convertToFloat(arguments.get(3));
    			final float value = AGHelper.convertToFloat(arguments.get(4));
       			
    			SynthDef synth = synthdefs.get(synthName);
    			synth.addParameter(paramName, min, max, value);
    			
    		}
    	};
    	
    	App.sc.createListener("/addparam", paramlistener);
    	App.sc.createListener("/instdef/param", paramlistener);
	}
	

}
