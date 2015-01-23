package net.alexgraham.thesis.ui.old;

import net.alexgraham.thesis.supercollider.SynthDef;

public interface SynthWindowDelegate {
	void selectSynth();
	void launchSynth(SynthDef synthDef);
}
