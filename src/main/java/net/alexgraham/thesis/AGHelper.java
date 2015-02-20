package net.alexgraham.thesis;

public class AGHelper {
	public enum TestEnum {
		THING, OTHER, TEST
	}
	public static Float convertToFloat(Object number) {
		Float toReturn = 0.0f;
		
		if (number.getClass() == Float.class) {
			toReturn = (Float) number;
		} else if (number.getClass() == Integer.class) {
			toReturn = (Float) ((Integer) number).floatValue();
		}
		return toReturn;
	}
	
	public static Integer convertToInt(Object number) {
		Integer toReturn = 0;
		
		if (number.getClass() == Integer.class) {
			toReturn = (Integer) number;
		} else if (number.getClass() == Float.class) {
			toReturn = (Integer) ((Float) number).intValue();
		}
		return toReturn;
	}
	
	
	public static <E extends Enum> boolean allEquals(E value, E... types) {
		boolean passed = false; // Innocent until proven guilty
		
		for (E i : types) {
			passed = passed || (value == i);
		}
		
		return passed;
	}
	
	public static void main(String[] args) {
		TestEnum type = TestEnum.THING;
		if (AGHelper.allEquals(type, TestEnum.OTHER, TestEnum.TEST)) {
			System.out.println("It passed");
		} else {
			System.out.println("it didn't pass");
		}
	}
}
