package net.alexgraham.thesis.supercollider.synths.parameters.models;

import net.alexgraham.thesis.supercollider.synths.parameters.ChoiceParam;
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParam;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParam;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;

public class ParamModelFactory {
	public static ParamModel createParamModel(Param baseParam) {
		ParamModel model = null;
		
		if (baseParam.getClass() == IntParam.class) {
			IntParam param = (IntParam) baseParam;
			model = new IntParamModel(param.getValue(), param.getMin(), param.getMax());
							
		} else if (baseParam.getClass() == ChoiceParam.class) {
			ChoiceParam param = (ChoiceParam) baseParam;
			model = new ChoiceParamModel(param.getChoiceName(), param.getChoiceValue(), param.getChoiceType());
			
		} else if (baseParam.getClass() == DoubleParam.class) {
			DoubleParam param = (DoubleParam)baseParam;
			model = new DoubleParamModel(2, param.getMin(), param.getMax(), param.getValue());
		} else {
			throw new ClassCastException("ParamModel not found for Param");
		}
		
		model.setName(baseParam.getName());
		return model;
	}
}
