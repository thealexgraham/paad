+ SynthDef {
	readyLoad {
		~java.addPendingDef(this.name);
		this.send(completionMsg: { ~java.removePendingDef(this.name);}.value);
	}
}