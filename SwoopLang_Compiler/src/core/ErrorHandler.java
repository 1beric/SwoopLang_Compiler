package core;

public class ErrorHandler {

	public static void assertTrue(boolean value, String errorMsg) {
		if (!value)
			throw new RuntimeException(
					"ERROR " + Scanner.s_LineNumber + "\n" + errorMsg);
	}

	public static void assertFalse(boolean value, String errorMsg) {
		assertTrue(!value, errorMsg);
	}

	public static void assertFalse(boolean value) {
		assertTrue(!value, "");
	}

	public static void error(String errorMsg) {
		assertTrue(false, errorMsg);
	}

	public static void assertTrue(boolean value) {
		assertTrue(value, "");
	}
}
