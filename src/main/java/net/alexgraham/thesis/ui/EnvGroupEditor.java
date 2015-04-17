package net.alexgraham.thesis.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jdk.nashorn.internal.ir.BaseNode;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamGroup;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.tests.EnvTest.RangeListener;
import net.alexgraham.thesis.ui.components.swingosc.EnvelopeView;

import org.apache.commons.lang.math.NumberUtils;

public class EnvGroupEditor extends JPanel {
	
	public EnvGroupEditor( ParamGroup group) {

	    // Create layout
	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		for (ParamModel baseModel : group.getParamModels()) {
			if (baseModel.getClass() == DoubleParamModel.class) {
				DoubleParamModel model = (DoubleParamModel) baseModel;
				EnvelopeView env = new EnvelopeView();
				float min = (float) model.getDoubleMinimum();
				float max = (float) model.getDoubleMaximum();
				float value = (float) model.getDoubleValue();
				
				env.setValues(new float[]{0f, 0.5f, 1.0f}, new float[] {(float) value, (float) value, (float) value});
				env.setRange("x", new float[] {0f, 1f});
				env.setRange("y", new float[] {min, max});
				
			    env.setHorizontalEditMode(EnvelopeView.HEDIT_CLAMP);
			    env.setSelectionColor(Color.red);
			    
			    add(env);
			}
		}	 
		
	}	  
}
