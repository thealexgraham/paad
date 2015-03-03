package net.alexgraham.thesis.supercollider;


public class SimpleID {
	static long current = 0;
	private long id = 0;
	public SimpleID() {
		current = current + 1;
		this.id = current;
	}
	
	public String toString() {
		return String.valueOf(id);
	}
}
