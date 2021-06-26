package core.symtab;

import java.util.Arrays;

public enum PrimitiveType {
	STR, INT, CHAR, LONG, SHORT, BYTE, BIT, BOOL, DOUBLE, FLOAT, VOID;

	public static boolean contains(String type) {
		return Arrays.stream(values())
				.anyMatch(v -> v.toString().equalsIgnoreCase(type));
	}
}
