(
~java = JavaHelper.new;
ParamBuilder.createParamsList(
	ParamBuilder.choice(\scale, "pentatonic", [0,3,5,6,10]),
	ParamBuilder.int(\root, 0, 127, 62),
	ParamBuilder.int(\octaveMin, -5, 5, 0),
	ParamBuilder.int(\octaveMax, -5, 5, 1),
	ParamBuilder.range(\length, \int, 0, 20, 5, 9)
	ParamBuilder.
)


)


(
~java = JavaHelper.new;
~java.createParamsList
.addChoice(\scale, "pentatonic", [0,3,5,6,10])
.addInt(\root, 0, 127, 62)
.addInt(\octaveMin, -5, 5, 0)
.addInt(\octaveMax, -5, 5, 1)
.addRange(\length, \int, 0, 20, 5, 9);

)

)
