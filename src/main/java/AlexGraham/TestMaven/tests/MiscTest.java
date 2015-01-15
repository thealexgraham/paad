package AlexGraham.TestMaven.tests;

public class MiscTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	       System.out.println("Working Directory = " +
	               System.getProperty("user.dir"));
		
		String test = "Test";
		
		if (test == "Test") {
			System.out.println("worked");
		}
		
		testArguments(new Object[] {"test", 3, 5});
	}
	
	public static void testArguments(Object... args) {
		for (Object object : args) {
			System.out.println(object);
		}
	}

}
