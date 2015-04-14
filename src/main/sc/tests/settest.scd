
(
d = Set.new;
t = Task({});
r = (key: \test, task: t);
d.add(r);
r.at(\task).postln;
d.remove((key: \test, task: t));
d.add(t);
)


(
f = { |arg1|
	arg1.postln;
};

f.value("test", "me");

)