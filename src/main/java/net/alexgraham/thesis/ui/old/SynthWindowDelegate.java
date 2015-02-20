package net.alexgraham.thesis.ui.old;

import net.alexgraham.thesis.supercollider.synths.defs.Def;

public interface SynthWindowDelegate {
	void selectSynth();
	void launchSynth(Def def);
}
