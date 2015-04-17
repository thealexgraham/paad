package net.alexgraham.thesis.supercollider.synths.parameters.models;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.alexgraham.thesis.ui.components.swingosc.EnvelopeView;
import net.alexgraham.thesis.ui.components.swingosc.EnvelopeView.Node;

public class EnvelopeModel {
	
	public interface EnvelopeListener {
		public void envelopeChanged(EnvelopeModel model);
		public void rangesChanged(float xMin, float xMax, float yMin, float yMax);
	}
	
	private List<EnvelopeListener> listeners = new CopyOnWriteArrayList<EnvelopeListener>();
	
	private float xMin = 0;
	private float xMax = 1;
	private float yMin = 0;
	private float yMax = 1;
	
	private float[] x = new float[]{};
	private float[] y = new float[]{};
	private int[] shapes = new int[]{};
	private float[] curves = new float[]{};
	
	private int length = 0;
	
	// Getters / Setters 
	public float[] getX() { return x; }
	public float[] getY() { return y; }
	public int[] getShapes() { return shapes; }
	public float[] getCurves() { return curves; }
	public int getLength() { return length; } 
 
	// Listener Business
	public void addListener(EnvelopeListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(EnvelopeListener listener) {
		listeners.remove(listener);
	}
	
	
	public EnvelopeModel() {
		// TODO Auto-generated constructor stub
		length = 0;
	}
	
	public float scale (float value, float oldMin, float oldMax, float newMin, float newMax) {
		float newVal = ((value - oldMin) / (oldMax - oldMin) ) * (newMax - newMin) + newMin;
		return newVal;
	}
	
	public float scale (float value, float newMin, float newMax) {
		return scale(value, 0, 1, newMin, newMax);
	}
	
	public void toSuperColliderVersion() {
		float[] values = new float[length];
		float[] times = new float[length - 1]; // 
		
		
		// Get the first value, since the first time isn't used
		values[0] =  scale(y[0], yMin, yMax);
		float lastTime = scale(x[0], xMin, xMax); // Should just be 0
		
		for (int i = 1; i < length; i++ ) {
			float currentValue = scale(y[i], yMin, yMax);
			float currentTime = scale(x[i], xMin, xMax);
			
			values[i] = currentValue;
			times[i - 1] = currentTime - lastTime;
			lastTime = currentTime;
		}
		
		System.out.println(Arrays.toString(values));
		System.out.println(Arrays.toString(times));
	}

	
	public void updateFromNodes(Node[] nodes, EnvelopeView sender) {
		
		boolean changed = false;
		
		if (getLength() != nodes.length) {
			changed = true;
		} else {
			for (int i = 0; i < nodes.length; i++) {
				Node node = nodes[i];
				if (x[i] != node.x || y[i] != node.y || shapes[i] != node.shape || curves[i] != node.curve) {
					changed = true;
					break;
				}
			}
		}
		
		if (!changed) {
			// if nothing has changed, get out
			return;
		}
		
		// otherwise update
		
		length = nodes.length;
		x = new float[nodes.length];
		y = new float[nodes.length];
		shapes = new int[nodes.length];
		curves = new float[nodes.length];
		
		for (int i = 0; i < nodes.length; i++) {
			x[i] = nodes[i].x;
			y[i] = nodes[i].y;
			shapes[i] = nodes[i].shape;
			curves[i] = nodes[i].curve;
		}
		
		fireChangedUpdate(sender);
	}
	
	public void setXRange(float min, float max) {
		boolean changed = false;
		if (xMin != min) {
			xMin = min;
			changed = true;
		}
		
		if (xMax != max) {
			xMax = max;
			changed = true;
		}
		
		if (changed) 
			fireRangesChangeUpdate();
	}

	public void setYRange(float min, float max) {
		boolean changed = false;
		if (yMin != min) {
			yMin = min;
			changed = true;
		}
		
		if (yMax != max) {
			yMax = max;
			changed = true;
		}
		
		if (changed) 
			fireRangesChangeUpdate();
	}
	
	private void fireChangedUpdate(Object sender) {
		for (EnvelopeListener listener : listeners) {
			if (listener != sender) {
				// Make sure we don't update the View that did this change
				listener.envelopeChanged(this);
			}
		}
	}
	
	private void fireRangesChangeUpdate() {
		for (EnvelopeListener listener : listeners) {
			listener.rangesChanged(xMin, xMax, yMin, yMax);
		}
	}
}
