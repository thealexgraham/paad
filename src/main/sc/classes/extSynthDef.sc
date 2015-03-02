+ SynthDef {
	readyLoad {
		this.func.postln;
		~java.addPendingDef(this.name);
		this.add(completionMsg: { ~java.removePendingDef(this.name) });
	}
}