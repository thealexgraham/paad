package net.alexgraham.thesis.supercollider.models;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
		
	private Hashtable<String, Def> defTable = new Hashtable<String, Def>();

	public Hashtable<String, Def> getDefTable() { return defTable; }
	public void setDefTable(Hashtable<String, Def> defTable) { this.defTable = defTable; }

	public DefModel() throws SocketException {
		createListeners();
	}
	
	public void clearDefModel() {
		defTable = new Hashtable<String, Def>();
	}
	
	public ArrayList<InstDef> getInstDefs() {
		ArrayList<InstDef> instDefs = new ArrayList<InstDef>();
		
		for (Def def : defTable.values()) {
			if (def.getClass() == InstDef.class) {
				instDefs.add((InstDef) def);
			}
		}
		return instDefs;
	}
	
	/**
	 * Add a Def directly
	 * @param def
	 */
	public void addDef(Def def) {
		defTable.put(def.getDefName(), def);
	}
	
	public void createListeners() throws SocketException {
		
		OSCListener defListener = new OSCListener() {
			@Override
			public void acceptMessage(Date time, OSCMessage message) {

    			List<Object> arguments = message.getArguments();
    			final String defName = (String) arguments.get(0);
    			final String type = (String) arguments.get(1);
    			
    			// Keep existing defs in memory since a Synth might point to it
    			if (defTable.containsKey(defName)) {
    				// Clear def so it can be recreated
    				Def def = defTable.get(defName);
    				def.setType(type); // Necessary?
    				def.setFunctionString("");
    				def.clearParameters();
        			App.sc.sendMessage("/"+defName+"/ready", 1);
    				return;    				
    			}
    			
    			// Create a new Def
    			Def def = null;
    			
    			// Make sure it gets the right type
    			switch (type) {
					case "synth":
						def = new SynthDef(defName);
						break;
					case "instrument":
						def = new InstDef(defName);
						break;
					case "effect":
						def = new EffectDef(defName);
						break;
					case "changeFunc":
						def = new ChangeFuncDef(defName);
						break;
					case "patternGen":
						def = new PatternGenDef(defName);
						break;
					default:
						break;
				}
    			
    			def.setType(type);
    			defTable.put(defName, def);
    			App.launchTreeModel.addSynthDef(def);
    			App.sc.sendMessage("/"+defName+"/ready", 1);
			}
		};
    	// TODO: 
    	OSCListener paramlistener = new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			List<Object> arguments = message.getArguments();
    			final String name = (String) arguments.get(0);
    			final String paramName = (String) arguments.get(1);
    			final String paramType = (String) arguments.get(2);
    			
    			Def def = defTable.get(name);
    			switch (paramType) {
    				case "int":
    	    			final int intMin = AGHelper.convertToInt(arguments.get(3));
    	    			final int intMax = AGHelper.convertToInt(arguments.get(4));
    	    			final int intValue = AGHelper.convertToInt(arguments.get(5));
    	    			((PatternGenDef)def).addParameter(paramName, intMin, intMax, intValue);
    	    			break;
    				case "choice":
    					final String choiceName = (String) arguments.get(3);
    					// The rest of the arguments should be the array
    					// Can probaably check how long this is instead, to see if it is just one number
    					List<Object> choiceList = arguments.subList(4, arguments.size() - 1);
    					// Assume its a pattern gen because this is the only thing that accepts it right now!
    					((PatternGenDef)def).addParameter(paramName, choiceName, choiceList.toArray()); //TODO: FIX THIS
    					break;
    				case "float":
    	    			final float floatMin = AGHelper.convertToFloat(arguments.get(3));
    	    			final float floatMax = AGHelper.convertToFloat(arguments.get(4));
    	    			final float floatValue = AGHelper.convertToFloat(arguments.get(5));
    	    			def.addParameter(paramName, floatMin, floatMax, floatValue);
    					break;
    				default:
    					break;
    					
    			}
    		}
    	};
    	
    	OSCListener fullDefListener = new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			LinkedList<Object> arguments = new LinkedList<Object>();
    			arguments.addAll(message.getArguments());
    			
    			final String defName = (String) arguments.removeFirst();
    			final String type = (String) arguments.removeFirst();
    			
    			Def def = addNewDef(defName, type);
    			
    			final String functionString = (String) arguments.removeFirst();
    			def.setFunctionString(functionString);
    			
    			while (!arguments.isEmpty()) {
    				// While there are still parameters to add
    				
        			final String paramName = (String) arguments.removeFirst();
        			final String paramType = (String) arguments.removeFirst();

        			switch (paramType) {
        				case "int":
        	    			final int intMin = AGHelper.convertToInt(arguments.removeFirst());
        	    			final int intMax = AGHelper.convertToInt(arguments.removeFirst());
        	    			final int intValue = AGHelper.convertToInt(arguments.removeFirst());
        	    			((PatternGenDef)def).addParameter(paramName, intMin, intMax, intValue);
        	    			break;
        				case "choice":
        					final String choiceName = (String) arguments.removeFirst();
        					final int choiceListSize = (int) arguments.removeFirst();
        					
        					final Object[] choiceArray = new Object[choiceListSize];
        					
        					// The rest of the arguments should be the array
        					// Can probaably check how long this is instead, to see if it is just one number
        					for (int i = 0; i < choiceListSize; i++) {
        						choiceArray[i] = (Object) arguments.removeFirst();
        					}
        					// Assume its a pattern gen because this is the only thing that accepts it right now!
        					((PatternGenDef)def).addParameter(paramName, choiceName, choiceArray); //TODO: FIX THIS
        					break;
        				case "float":
        	    			final float floatMin = AGHelper.convertToFloat(arguments.removeFirst());
        	    			final float floatMax = AGHelper.convertToFloat(arguments.removeFirst());
        	    			final float floatValue = AGHelper.convertToFloat(arguments.removeFirst());
        	    			def.addParameter(paramName, floatMin, floatMax, floatValue);
        					break;
        				default:
        					break;
        			}
    			}
    			

    		}
    	};
    	
    	OSCListener defaultParamListener = new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			List<Object> arguments = message.getArguments();
    			final String name = (String) arguments.get(0);
    			final String paramName = (String) arguments.get(1);
    			
    			Def def = defTable.get(name);

    			final float floatMin = AGHelper.convertToFloat(arguments.get(2));
    			final float floatMax = AGHelper.convertToFloat(arguments.get(3));
    			final float floatValue = AGHelper.convertToFloat(arguments.get(4));
    			def.addParameter(paramName, floatMin, floatMax, floatValue);
    		}
    	};
    	
    	OSCListener functionListener = new OSCListener() {
    		public void acceptMessage(java.util.Date time, OSCMessage message) {

    			List<Object> arguments = message.getArguments();
    			final String synthName = (String) arguments.get(0);
    			final String functionString = (String) arguments.get(1);
    			
    			Def synth = defTable.get(synthName);
    			synth.setFunctionString(functionString);
    		}
    	};

    	App.sc.createListener("/def/add", defListener);
    	App.sc.createListener("/def/add/full", fullDefListener);
    	App.sc.createListener("/def/param", paramlistener);
    	App.sc.createListener("/def/param/default", defaultParamListener);
    	App.sc.createListener("/def/func", functionListener);
	}
	
	public Def addNewDef(String defName, String type) {
		
		// Keep existing defs in memory since a Synth might point to it
		if (defTable.containsKey(defName)) {
			// Clear def so it can be recreated
			Def def = defTable.get(defName);
			def.setType(type); // Necessary?
			def.setFunctionString("");
			def.clearParameters();
			App.sc.sendMessage("/"+defName+"/ready", 1);
			return def;    				
		}
		
		// Create a new Def
		Def def = null;
		
		// Make sure it gets the right type
		switch (type) {
			case "synth":
				def = new SynthDef(defName);
				break;
			case "instrument":
				def = new InstDef(defName);
				break;
			case "effect":
				def = new EffectDef(defName);
				break;
			case "changeFunc":
				def = new ChangeFuncDef(defName);
				break;
			case "patternGen":
				def = new PatternGenDef(defName);
				break;
			default:
				break;
		}
		
		def.setType(type);
		defTable.put(defName, def);
		App.launchTreeModel.addSynthDef(def);
		
		return def;
	}

}
