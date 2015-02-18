+ SynthDef {
	readyLoad {
		~java.addPendingDef(this.name);
		this.add(completionMsg: { ~java.removePendingDef(this.name) });
	}
}