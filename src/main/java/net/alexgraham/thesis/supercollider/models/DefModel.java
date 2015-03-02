package net.alexgraham.thesis.supercollider.models;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.defs.ChangeFuncDef;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.defs.EffectDef;
import net.alexgraham.thesis.supercollider.synths.defs.InstDef;
import net.alexgraham.thesis.supercollider.synths.defs.PatternGenDef;
import net.alexgraham.thesis.supercollider.synths.defs.SynthDef;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class DefModel {
	
	private DefaultListModel<Def> defListModel = new DefaultListModel<Def>();
	
	private Hashtable<String, Def> synthdefs = new Hashtable<String, Def>();

	public void setDefListModel(DefaultListModel<Def> model) {
		this.defListModel = model;
	}
	
	public DefModel() throws SocketException {
		createListeners();
	}

	public DefaultListModel<Def> getSynthDefListModel() {
		return defListModel;
	}
	
	public void clearSynthDefListModel() {
		defListModel = new DefaultListModel<Def>();
		synthdefs = new Hashtable<String, Def>();
	}
	
	public ArrayList<InstDef> getInstDefs() {
		ArrayList<InstDef> instDefs = new ArrayList<InstDef>();
		
		for (Enumeration<Def> e = defListModel.elements(); e.hasMoreElements();)  {
			Def def = e.nextElement();
			if (def.getClass() == InstDef.class) {
				instDefs.add((InstDef) def);
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
    			defListModel.addElement(synth);
    			App.launchTreeModel.addSynthDef(synth);	
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
    			defListModel.addElement(synth);
    			App.launchTreeModel.addSynthDef(synth);	
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
    			defListModel.addElement(effectDef);
    			App.launchTreeModel.addSynthDef(effectDef);

    			// Also Add To The List
    		}
    	});
    	
    	App.sc.createListener("/changefuncdef/add", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);

    			ChangeFuncDef changeDef = new ChangeFuncDef(synthName, App.sc);
    			synthdefs.put(synthName, changeDef);
    			defListModel.addElement(changeDef);
    			App.launchTreeModel.addSynthDef(changeDef);
    			
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
       			
    			Def synth = synthdefs.get(synthName);
    			synth.addParameter(paramName, min, max, value);
    			
    		}
    	};
    	
    	App.sc.createListener("/synthdef/param", paramlistener);
    	App.sc.createListener("/instdef/param", paramlistener);
    	App.sc.createListener("/effectdef/param", paramlistener);
    	App.sc.createListener("/changefuncdef/param", paramlistener);
    	createGenListeners();
	}
	
	public void createGenListeners() throws SocketException {
    	App.sc.createListener("/patterngendef/add", new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {
    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);

    			PatternGenDef def = new PatternGenDef(synthName, App.sc);
    			synthdefs.put(synthName, def);
    			defListModel.addElement(def);
    			System.out.println("Addding def " + def.getClass());
    			App.launchTreeModel.addSynthDef(def);
    		}
    	});
		
    	OSCListener paramlistener = new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			List<Object> arguments = message.getArguments();
    			final String name = (String) arguments.get(0);
    			final String paramName = (String) arguments.get(1);
    			final String paramType = (String) arguments.get(2);
    			
    			PatternGenDef def = (PatternGenDef) synthdefs.get(name);
    			
    			switch (paramType) {
    				case "int":
    	    			final int min = AGHelper.convertToInt(arguments.get(3));
    	    			final int max = AGHelper.convertToInt(arguments.get(4));
    	    			final int value = AGHelper.convertToInt(arguments.get(5));
    	    			def.addParameter(paramName, min, max, value);
    	    			break;
    				case "choice":
    					final String choiceName = (String) arguments.get(3);
    					// The rest of the arguments should be the array
    					// Can probaably check how long this is instead, to see if it is just one number
    					List<Object> choiceList = arguments.subList(4, arguments.size() - 1);
    					def.addParameter(paramName, choiceName, choiceList.toArray());
    					break;
    				default:
    					break;
    					
    			}
    		}
    	};
		App.sc.createListener("/patterngendef/param", paramlistener);
    	
	}
	
	

}
