package core.symtab;

import java.util.HashMap;
import java.util.Map;

import core.ErrorHandler;

public class SymbolTable {

	private static SymbolTable s_FileSt;
	private static FileDef s_FileDef;

	public static SymbolTable fileSt() {
		return s_FileSt;
	}

	public static FileDef fileDef() {
		return s_FileDef;
	}

	public static void set(SymbolTableType type) {
		new SymbolTable(type);
	}

	private SymbolTableType m_Type;

	// these are imported definitions (either default or through "use")
	public Map<String, ClassDef> m_ClassDefs;
	public Map<String, InterfaceDef> m_InterfaceDefs;
	public Map<String, EnumDef> m_EnumDefs;

	// for m_Type == Local
	private Map<String, StEntry> m_Stack;

	public SymbolTable(SymbolTableType type) {
		m_Type = type;
		switch (type) {
		case CLASS:
		case INTERFACE:
			// need to set s_FileSt to this ST
			s_FileSt = this;
			if (type == SymbolTableType.CLASS)
				s_FileDef = new ClassDef();
			else
				s_FileDef = new InterfaceDef();

			// need to initialize maps for imports
			m_ClassDefs = new HashMap<>();
			m_InterfaceDefs = new HashMap<>();
			m_EnumDefs = new HashMap<>();

			// both classes and interfaces have to import base classes
			// all these reside in the default library
			// - Default : [n/a]
			// - String : Default
			// - Character : Default
			// - Number : Default
			// - Integer: Number
			// - Long: Number
			// - Short: Number
			// - Byte: Number
			// - Bit: Number
			// - Boolean : Default
			// - Decimal: Number
			// - Double: Decimal
			// - Float: Decimal

			break;
		case ENUM:
			// need to set s_FileSt to this ST
			s_FileSt = this;
			s_FileDef = new EnumDef();

			// no imports for enums

			break;
		case LOCAL:
			// add this to the method's ST stack
			// occurs elsewhere

			// initialize stack
			m_Stack = new HashMap<>();

			// no imports for local

			break;
		default:
			break;
		}
	}

	// FILE SymbolTable

	public void addClass(ClassDef def) {
		m_ClassDefs.put(def.id(), def);
	}

	public boolean hasClass(String name) {
		return m_ClassDefs.containsKey(name);
	}

	public ClassDef getClass(String name) {
		return m_ClassDefs.get(name);
	}

	public boolean hasInterface(String name) {
		return m_InterfaceDefs.containsKey(name);
	}

	public InterfaceDef getInterface(String name) {
		return m_InterfaceDefs.get(name);
	}

	public boolean hasEnum(String name) {
		return m_EnumDefs.containsKey(name);
	}

	public EnumDef getEnum(String name) {
		return m_EnumDefs.get(name);
	}

	// LOCAL SymbolTable

	public void insertEntry(StEntry entry) {
		ErrorHandler.assertTrue(m_Type == SymbolTableType.LOCAL,
				"Cannot insert an entry into a non-Local SymbolTable");

		// TODO support scopes
		ErrorHandler.assertFalse(m_Stack.containsKey(entry.id()),
				"Cannot insert another entry for " + entry.id());

		// success
		m_Stack.put(entry.id(), entry);
	}

	public void insertEntry(String type, String id) {
		ErrorHandler.assertTrue(m_Type == SymbolTableType.LOCAL,
				"Cannot insert an entry into a non-Local SymbolTable");

		// TODO support scopes
		ErrorHandler.assertFalse(m_Stack.containsKey(id),
				"Cannot insert another entry for " + id);

		// success
		StEntry entry = createEntry(type, id);
		m_Stack.put(entry.id(), entry);
	}

	public boolean hasEntry(String name) {
		ErrorHandler.assertTrue(m_Type == SymbolTableType.LOCAL,
				"Cannot get an entry from a non-Local SymbolTable");

		// success
		return m_Stack.containsKey(name);
	}

	public StEntry getEntry(String name) {
		ErrorHandler.assertTrue(m_Type == SymbolTableType.LOCAL,
				"Cannot get an entry from a non-Local SymbolTable");

		ErrorHandler.assertTrue(hasEntry(name),
				"The SymbolTable does not contain an entry for " + name);

		// success
		return m_Stack.get(name);
	}

	private StEntry createEntry(String type, String id) {
		SymbolTable file = fileSt();
		if (PrimitiveType.contains(type)) {
			PrimitiveEntry entry = new PrimitiveEntry(type);
			entry.id(id);
			entry.formal(false);
			return entry;
		} else if (file.m_ClassDefs.containsKey(type)) {
			ClassEntry entry = new ClassEntry();
			entry.objectType(type);
			entry.id(id);
			return entry;
		} else if (file.m_InterfaceDefs.containsKey(type)) {
			InterfaceEntry entry = new InterfaceEntry();
			entry.objectType(type);
			entry.id(id);
			return entry;
		} else if (file.m_EnumDefs.containsKey(type)) {
			EnumEntry entry = new EnumEntry();
			entry.objectType(type);
			entry.id(id);
			return entry;
		} else {
			ErrorHandler.error("The type " + type + " has not been defined");
			return null;
		}
	}

	public String getStackOrFieldType(String id) {
		if (hasEntry(id)) {
			StEntry entry = getEntry(id);
			return entry.type();
		} else if (fileDef() instanceof ClassDef) {
			ClassDef def = (ClassDef) fileDef();
			if (def.hasField(id))
				return def.field(id).type();
		}
		// TODO maybe use this function for static calls
		// ie: String.parseInt("1");
		// - - - ^ - - - - - - - - -
		ErrorHandler.error("The id " + id
				+ " is not on the stack or a field of " + fileDef().id());
		return "Default";
	}

	public static String combineTypes(String type1, String type2) {
		if (type1.equals(type2))
			return type1;

		switch (type1) {
		case "int":
			switch (type2) {
			case "int":
			case "short":
			case "byte":
			case "bit":
			case "char":
				return type1;
			case "float":
			case "double":
			case "long":
			case "str":
				return type2;

			case "bool":
				ErrorHandler.error("The primitive types " + type1 + " and "
						+ type2 + " are not compatible");
				return "Default";

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		case "short":
			switch (type2) {
			case "short":
			case "byte":
			case "bit":
			case "char":
				return type1;

			case "float":
			case "double":
			case "int":
			case "long":
			case "str":
				return type2;

			case "bool":
				ErrorHandler.error("The primitive types " + type1 + " and "
						+ type2 + " are not compatible");
				return "Default";

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		case "long":
			switch (type2) {
			case "int":
			case "long":
			case "short":
			case "byte":
			case "bit":
			case "char":
				return type1;

			case "float":
			case "double":
			case "str":
				return type2;

			case "bool":
				ErrorHandler.error("The primitive types " + type1 + " and "
						+ type2 + " are not compatible");
				return "Default";

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		case "byte":
			switch (type2) {
			case "byte":
			case "bit":
			case "char":
				return type1;

			case "int":
			case "long":
			case "short":
			case "float":
			case "double":
			case "str":
				return type2;

			case "bool":
				ErrorHandler.error("The primitive types " + type1 + " and "
						+ type2 + " are not compatible");
				return "Default";

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		case "bit":
			switch (type2) {
			case "bit":
				return type1;

			case "byte":
			case "char":
			case "int":
			case "long":
			case "short":
			case "float":
			case "double":
			case "str":
				return type2;

			case "bool":
				ErrorHandler.error("The primitive types " + type1 + " and "
						+ type2 + " are not compatible");
				return "Default";

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		case "bool":
			switch (type2) {
			case "bool":
				return type1;

			case "str":
				return type2;

			case "bit":
			case "byte":
			case "char":
			case "int":
			case "long":
			case "short":
			case "float":
			case "double":
				ErrorHandler.error("The primitive types " + type1 + " and "
						+ type2 + " are not compatible");
				return "Default";

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		case "float":
			switch (type2) {
			case "bit":
			case "byte":
			case "char":
			case "int":
			case "long":
			case "short":
			case "float":
				return type1;

			case "double":
			case "str":
				return type2;

			case "bool":
				ErrorHandler.error("The primitive types " + type1 + " and "
						+ type2 + " are not compatible");
				return "Default";

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		case "double":
			switch (type2) {
			case "bit":
			case "byte":
			case "char":
			case "int":
			case "long":
			case "short":
			case "float":
			case "double":
				return type1;

			case "str":
				return type2;

			case "bool":
				ErrorHandler.error("The primitive types " + type1 + " and "
						+ type2 + " are not compatible");
				return "Default";

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		case "str":
			switch (type2) {
			case "bit":
			case "byte":
			case "char":
			case "int":
			case "long":
			case "short":
			case "float":
			case "double":
			case "str":
			case "bool":
				return type1;

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		case "char":
			switch (type2) {
			case "bit":
			case "char":
				return type1;

			case "byte":
			case "int":
			case "long":
			case "short":
			case "float":
			case "double":
			case "str":
				return type2;

			case "bool":
				ErrorHandler.error("The primitive types " + type1 + " and "
						+ type2 + " are not compatible");
				return "Default";

			default:
				// CLASS OR INTERFACE
				ErrorHandler.error("The primitive type " + type1
						+ " is nor compatible with the object type " + type2);
				return "Default";
			}

		default:
			// CLASS OR INTERFACE
			break;
		}
		// TODO combineTypes CLASS/INTERFACES
		return type1;
	}

	public void debugPrint() {
		switch (m_Type) {
		case CLASS:
		case ENUM:
		case INTERFACE:
			// TODO implement print for file st
			break;

		case LOCAL:
			int num = 0;
			System.out.print("Local ST {\n\t");
			for (String key : m_Stack.keySet()) {
				m_Stack.get(key).debugPrint();
				if (++num % 4 == 0 && num != m_Stack.size())
					System.out.print("\n\t");
				else if (num != m_Stack.size())
					System.out.print(", ");
			}
			System.out.print("\n}");
			break;

		default:
			break;

		}
	}
}
