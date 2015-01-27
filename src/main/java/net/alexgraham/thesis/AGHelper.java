package net.alexgraham.thesis;

public class AGHelper {
	public static Float convertToFloat(Object number) {
		Float toReturn = 0.0f;
		
		if (number.getClass() == Float.class) {
			toReturn = (Float) number;
		} else if (number.getClass() == Integer.class) {
			toReturn = (Float) ((Integer) number).floatValue();
		}
		return toReturn;
	}
}
