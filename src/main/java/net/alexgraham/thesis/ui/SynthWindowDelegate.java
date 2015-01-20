package net.alexgraham.thesis.ui;

import net.alexgraham.thesis.supercollider.SynthDef;

public interface SynthWindowDelegate {
	void selectSynth();
	void launchSynth(SynthDef synthDef);
}
