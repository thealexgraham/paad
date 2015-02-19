(
c = ChangeFunc.new({
	| value, min, max, maxChangePercent |
	var changeTop, change;
	"I was called".postln;
	changeTop = (max - min) * (maxChangePercent / 100);
	change = changeTop.rand * [1, -1].choose;

	value + change;
}, [[\maxChangePercent, 0, 100, 10]]);
)

p = ParameterBus.new(\param, 5, 0, 100);
p.min
p.getMin

c.doChange(p);

p.value;


(
~effect = Dictionary.new;
~effect.put(\param1, ParameterBus.new(\param1, 5, 100, 200));
~effect.put(\param2, ParameterBus.new(\param1, 1000, 1000, 5000));

~changeFunc = ChangeFunc.new({
	| value, min, max, maxChangePercent |
	var changeTop, change;
	"I was called".postln;
	changeTop = (max - min) * (maxChangePercent / 100);
	change = changeTop.rand * [1, -1].choose;

	value + change;
}, [[\maxChangePercent, 0, 100, 10]]);

~changeFunc.addListener(~effect.at(\param1));
~changeFunc.addListener(~effect.at(\param2));

~changeFunc.doAction;


)

(
~changeFunc.doAction;
~changeFunc.removeListener(~effect.at(\param1));
~changeFunc.removeListener(~effect.at(\param2));

)