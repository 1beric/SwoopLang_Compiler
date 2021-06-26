package core.symtab;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTableOLD {

	private Map<String, ClassDef> m_ExtClassDefs = new HashMap<>();
	private Map<String, InterfaceDef> m_ExtInterfaceDefs = new HashMap<>();
	private Map<String, EnumDef> m_ExtEnumDefs = new HashMap<>();

	private StDef fileDef; // class interface enum
	private Scope head; // this is for the current scope
	private Scope tail; // this is for the current scope

	public void initDefaults() {
		// initialize default supported classes
		// Default
		// String
		// Character
		// Number
		// Integer: Number
		// Long: Number
		// Short: Number
		// Byte: Number
		// Bit: Number
		// Boolean
		// Decimal: Number
		// Double: Decimal
		// Float: Decimal
	}

	public SymbolTableOLD fileDef(StDef def) {
		this.fileDef = def;
		return this;
	}

	public StDef fileDef() {
		return fileDef;
	}

	public SymbolTableOLD pushScope() {
		Scope scope = new Scope(tail);
		if (head == null)
			head = scope;
		tail = scope;
		return this;
	}

	public SymbolTableOLD popScope() {
		if (head.equals(tail))
			head = null;
		tail = tail.previousScope;
		return this;
	}

	public Scope scope() {
		return tail;
	}

	public SymbolTableOLD addClass(ClassDef def) {
		m_ExtClassDefs.put(def.name(), def);
		return this;
	}

	public SymbolTableOLD defineFromFile(String file) {
		// "use swoop.standard.List;"
		// TODO file read from a use statement
		try (java.util.Scanner sc = new java.util.Scanner(new File(file))) {
			String[] header = sc.nextLine().split(" ");
			AccessLevel access = AccessLevel.valueOf(header[0].toUpperCase());
			String fileType = header[1];
			String id = header[2];
			String library = header[3];
			switch (fileType) {
			case "class":
				ClassDef classDef = new ClassDef();
				classDef.name(id);
				classDef.accessLevel(access);
				classDef.library(library);

				String[] parents = sc.nextLine().split(",");
				for (String parent : parents)
					classDef.addParent(parent);

				String[] fields = sc.nextLine().split(",");
				for (String field : fields) {
					FieldDef fieldDef = new FieldDef();
					String[] fieldArr = field.split(",");
					fieldDef.accessLevel(AccessLevel.valueOf(fieldArr[0]));
					StEntry entry;
					if (Arrays.stream(PrimitiveType.values()).anyMatch(
							v -> v.toString().equalsIgnoreCase(fieldArr[1])))
						entry = createPrimitive(PrimitiveType
								.valueOf(fieldArr[1].toUpperCase()));
					else if (m_ExtClassDefs.containsKey(fieldArr[1])
							|| m_ExtInterfaceDefs.containsKey(fieldArr[1])) {
						entry = new ObjectEntry().type(fieldArr[1]);
					} else if (m_ExtEnumDefs.containsKey(fieldArr[1])) {
						entry = new EnumEntry().type(fieldArr[1]);

					} else
						throw new RuntimeException(" ERROR type: " + fieldArr[1]
								+ " does not exist");

					if (fieldArr.length >= 4 && fieldArr[3].equals("final"))
						fieldDef.isFinal = true;
					if (fieldArr.length >= 4 && fieldArr[3].equals("static"))
						fieldDef.isStatic = true;
					if (fieldArr.length >= 5 && fieldArr[4].equals("static"))
						fieldDef.isStatic = true;

					classDef.addField(fieldDef);
				}

				String[] constructors = sc.nextLine().split(",");
				String[] methods = sc.nextLine().split(",");
				break;

			case "interface":
				parents = sc.nextLine().split(",");
				methods = sc.nextLine().split(",");
				break;

			case "enum":
				fields = sc.nextLine().split(",");
				break;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return this;
	}

	public String buildHeader() {
		StringBuilder header = new StringBuilder();
		header.append(fileDef.accessLevel()).append(' ');
		if (fileDef instanceof ClassDef)
			header.append("class ");
		else if (fileDef instanceof InterfaceDef)
			header.append("interface ");
		else if (fileDef instanceof EnumDef)
			header.append("enum ");
		header.append(fileDef.name()).append(' ');
		header.append(fileDef.library()).append('\n');
		if (fileDef instanceof ClassDef) {
			ClassDef def = (ClassDef) fileDef;
			// [<parent>, …]
			List<String> parents = def.parents;
			for (int i = 0; i < parents.size(); i++) {
				header.append(parents.get(i));
				if (i != parents.size() - 1)
					header.append(',');
			}
			header.append('\n');
			// [<access> <type> <id> [final] [static], …]
			int fieldCount = 0;
			for (String fieldName : def.fields.keySet()) {
				FieldDef field = def.fields.get(fieldName);
				header.append(field.accessLevel()).append(' ');
				header.append(field.entry.type()).append(' ');
				header.append(fieldName).append(' ');
				if (field.isFinal)
					header.append(" final");
				if (field.isStatic)
					header.append(" static");
				if (++fieldCount < def.fields.size())
					header.append(',');
			}
			// [<access> ([<type> <id>; …]) [final], …]
			int methodCount = 0;
			for (String methodName : def.methods.keySet()) {
				MethodDef method = def.methods.get(methodName);
				if (!method.isConstructor) {
					methodCount++;
					continue;
				}
				header.append(method.accessLevel()).append(" (");
				int formalCount = 0;
				for (String formalName : method.formals.keySet()) {
					StEntry formal = method.formals.get(formalName);
					header.append(formal.type()).append(' ');
					header.append(formal.name());
					if (++formalCount < method.formals.size())
						header.append(';');
				}
				header.append(')');
				if (method.isFinal)
					header.append(" final");
				if (++methodCount < def.methods.size())
					header.append(',');
			}
			// [<access> <type> <id> ([<type> <id>; …]), …] [final] [static]
			methodCount = 0;
			for (String methodName : def.methods.keySet()) {
				MethodDef method = def.methods.get(methodName);
				if (method.isConstructor) {
					methodCount++;
					continue;
				}
				header.append(method.accessLevel()).append(' ');
				header.append(method.returnType).append(' ');
				header.append(methodName).append(" (");
				int formalCount = 0;
				for (String formalName : method.formals.keySet()) {
					StEntry formal = method.formals.get(formalName);
					header.append(formal.type()).append(' ');
					header.append(formal.name());
					if (++formalCount < method.formals.size())
						header.append(';');
				}
				header.append(')');
				if (method.isFinal)
					header.append(" final");
				if (method.isStatic)
					header.append(" static");
				if (++methodCount < def.methods.size())
					header.append(',');
			}
		}
		if (fileDef instanceof InterfaceDef) {
			InterfaceDef def = (InterfaceDef) fileDef;
			List<String> parents = def.parents;
			for (int i = 0; i < parents.size(); i++) {
				header.append(parents.get(i));
				if (i != parents.size() - 1)
					header.append(',');
			}
			header.append('\n');
			// [<access> <type> <id> ([<type> <id>; …]) [proto], …]
			int methodCount = 0;
			for (String methodName : def.methods.keySet()) {
				MethodDef method = def.methods.get(methodName);
				header.append(method.accessLevel()).append(' ');
				header.append(method.returnType).append(' ');
				header.append(methodName).append(" (");
				int formalCount = 0;
				for (String formalName : method.formals.keySet()) {
					StEntry formal = method.formals.get(formalName);
					header.append(formal.type()).append(' ');
					header.append(formal.name());
					if (++formalCount < method.formals.size())
						header.append(';');
				}
				header.append(')');
				if (method.isProto)
					header.append(" proto");
				if (++methodCount < def.methods.size())
					header.append(',');
			}
		}
		if (fileDef instanceof EnumDef) {
			EnumDef def = (EnumDef) fileDef;
			// [<id>, …]
			int idNum = 0;
			for (String field : def.names) {
				header.append(field);
				if (++idNum == def.names.size())
					header.append(',');
			}
		}

		return header.toString();
	}

	private static int scopeIdCount = 0;

	public class Scope {
		private Map<String, StEntry> variables = new HashMap<>();
		private Scope previousScope;
		private int id;

		public Scope(Scope previous) {
			previousScope = previous;
			id = scopeIdCount++;
		}

		public StEntry variable(String name) {
			if (previousScope != null && !definesVariable(name))
				return previousScope.variable(name);
			return variables.get(name);
		}

		public boolean definesVariable(String name) {
			return variables.containsKey(name);
		}

		public Scope addVariable(StEntry variable) {
			if (definesVariable(variable.name()))
				throw new RuntimeException("ERROR the var " + variable
						+ " has already been defined in this scope.");
			variables.put(variable.name(), variable);
			return this;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Scope && ((Scope) obj).id == id;
		}

	}

	public abstract class StEntry {
		private String name;

		public StEntry name(String name) {
			this.name = name;
			return this;
		}

		public String name() {
			return name;
		}

		public abstract String type();

		public abstract String valueStr();
	}

	public PrimitiveEntry createPrimitive(PrimitiveType type) {
		switch (type) {
		case BIT:
			return new BitPrimitive();
		case BOOL:
			return new BoolPrimitive();
		case BYTE:
			return new BytePrimitive();
		case CHAR:
			return new CharPrimitive();
		case DOUBLE:
			return new DoublePrimitive();
		case FLOAT:
			return new FloatPrimitive();
		case INT:
			return new IntPrimitive();
		case LONG:
			return new LongPrimitive();
		case SHORT:
			return new ShortPrimitive();
		case STR:
			return new StringPrimitive();
		case VOID:
		default:
			throw new RuntimeException(
					"cannot initialize an entry as " + type.toString());
		}
	}

	public abstract class PrimitiveEntry extends StEntry {
	}

	public class IntPrimitive extends PrimitiveEntry {
		private int value;

		public IntPrimitive value(int value) {
			this.value = value;
			return this;
		}

		public int value() {
			return value;
		}

		@Override
		public String type() {
			return "int";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class LongPrimitive extends PrimitiveEntry {
		private long value;

		public LongPrimitive value(long value) {
			this.value = value;
			return this;
		}

		public long value() {
			return value;
		}

		@Override
		public String type() {
			return "long";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class ShortPrimitive extends PrimitiveEntry {
		private short value;

		public ShortPrimitive value(short value) {
			this.value = value;
			return this;
		}

		public short value() {
			return value;
		}

		@Override
		public String type() {
			return "short";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class BytePrimitive extends PrimitiveEntry {
		private byte value;

		public BytePrimitive value(byte value) {
			this.value = value;
			return this;
		}

		public byte value() {
			return value;
		}

		@Override
		public String type() {
			return "byte";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class BitPrimitive extends PrimitiveEntry {
		private boolean value;

		public BitPrimitive value(boolean value) {
			this.value = value;
			return this;
		}

		public boolean value() {
			return value;
		}

		@Override
		public String type() {
			return "bit";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class BoolPrimitive extends PrimitiveEntry {
		private boolean value;

		public BoolPrimitive value(boolean value) {
			this.value = value;
			return this;
		}

		public boolean value() {
			return value;
		}

		@Override
		public String type() {
			return "bool";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class CharPrimitive extends PrimitiveEntry {
		private char value;

		public CharPrimitive value(char value) {
			this.value = value;
			return this;
		}

		public char value() {
			return value;
		}

		@Override
		public String type() {
			return "char";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class StringPrimitive extends PrimitiveEntry {
		private String value;

		public StringPrimitive value(String value) {
			this.value = value;
			return this;
		}

		public String value() {
			return value;
		}

		@Override
		public String type() {
			return "str";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class DoublePrimitive extends PrimitiveEntry {
		private double value;

		public DoublePrimitive value(double value) {
			this.value = value;
			return this;
		}

		public double value() {
			return value;
		}

		@Override
		public String type() {
			return "double";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class FloatPrimitive extends PrimitiveEntry {
		private float value;

		public FloatPrimitive value(float value) {
			this.value = value;
			return this;
		}

		public float value() {
			return value;
		}

		@Override
		public String type() {
			return "float";
		}

		@Override
		public String valueStr() {
			return value() + "";
		}
	}

	public class ObjectEntry extends StEntry {
		private String objectType; // can be class or interface name
		private Map<String, FieldDef> fieldsValues = new HashMap<>();

		public ObjectEntry type(String type) {
			this.objectType = type;
			return this;
		}

		public String type() {
			return objectType;
		}

		public StDef object() {
			if (m_ExtClassDefs.containsKey(objectType))
				return m_ExtClassDefs.get(objectType);
			if (m_ExtInterfaceDefs.containsKey(objectType))
				return m_ExtInterfaceDefs.get(objectType);

			return null;
		}

		public FieldDef field(String name) {
			if (!fieldsValues.containsKey(name))
				throw new RuntimeException("ERROR on type: " + objectType
						+ ", no accessible field: " + name);
			return fieldsValues.get(name);
		}

		@Override
		public String valueStr() {
			return "null";
		}

	}

	public class EnumEntry extends StEntry {
		private String enumName;
		private String enumField;

		public EnumEntry type(String type) {
			this.enumName = type;
			return this;
		}

		public String type() {
			return enumName;
		}

		public EnumEntry field(String name) {
			enumField = name;
			return this;
		}

		public String field() {
			return enumField;
		}

		@Override
		public String valueStr() {
			return field();
		}
	}

	public class StDef {
		private String name;
		private String library;
		private AccessLevel accessLevel;

		public StDef name(String name) {
			this.name = name;
			return this;
		}

		public String name() {
			return name;
		}

		public StDef library(String library) {
			this.library = library;
			return this;
		}

		public String library() {
			return library;
		}

		public StDef accessLevel(AccessLevel accessLevel) {
			this.accessLevel = accessLevel;
			return this;
		}

		public AccessLevel accessLevel() {
			return accessLevel;
		}
	}

	public class ClassDef extends StDef {
		private List<String> parents = new ArrayList<>();
		private Map<String, FieldDef> fields = new HashMap<>();
		private Map<String, MethodDef> methods = new HashMap<>();

		public ClassDef() {
			parents.add("Default");
		}

		public ClassDef addParent(String name) {
			parents.add(name);
			return this;
		}

		public boolean hasParent(String name) {
			if (parents.contains(name))
				return true;

			for (String parent : parents) {
				if (m_ExtClassDefs.containsKey(parent)
						&& m_ExtClassDefs.get(parent).hasParent(name))
					return true;
				if (m_ExtInterfaceDefs.containsKey(parent)
						&& m_ExtInterfaceDefs.get(parent).hasParent(name))
					return true;
			}

			return false;
		}

		public ClassDef addField(FieldDef field) {
			if (definesField(field.name()))
				throw new RuntimeException("ERROR Cannot overload fields");
			if (hasField(field.name())) // TODO support field overriding
				throw new RuntimeException(
						"FIELD OVERRIDING NOT SUPPORTED YET");
			fields.put(field.name(), field);
			return this;
		}

		public boolean definesField(String name) {
			return fields.containsKey(name);
		}

		public boolean hasField(String name) {
			if (definesField(name))
				return true;

			for (String parent : parents)
				if (m_ExtClassDefs.containsKey(parent)
						&& m_ExtClassDefs.get(parent).hasField(name))
					return true;

			return false;
		}

		public FieldDef field(String name) {
			return fields.get(name);
		}

		public ClassDef addMethod(MethodDef method) {
			if (definesMethod(method.name())) // TODO support method overloading
				throw new RuntimeException(
						"METHOD OVERLOADING NOT SUPPORTED YET");
			if (hasMethod(method.name())) // TODO support method overriding
				throw new RuntimeException(
						"METHOD OVERRIDING NOT SUPPORTED YET");
			methods.put(method.name(), method);
			return this;
		}

		public boolean definesMethod(String name) {
			return methods.containsKey(name);
		}

		public boolean hasMethod(String name) {
			if (definesMethod(name))
				return true;

			for (String parent : parents) {
				if (m_ExtClassDefs.containsKey(parent)
						&& m_ExtClassDefs.get(parent).hasMethod(name))
					return true;
				if (m_ExtInterfaceDefs.containsKey(parent)
						&& m_ExtInterfaceDefs.get(parent).hasMethod(name))
					return true;
			}

			return false;
		}

		public MethodDef method(String name) {
			return methods.get(name);
		}

	}

	public class InterfaceDef extends StDef {
		private List<String> parents = new ArrayList<>();;
		private Map<String, MethodDef> methods = new HashMap<>();

		public InterfaceDef addParent(String name) {
			parents.add(name);
			return this;
		}

		public boolean hasParent(String name) {
			if (parents.contains(name))
				return true;

			for (String parent : parents)
				if (m_ExtInterfaceDefs.containsKey(parent)
						&& m_ExtInterfaceDefs.get(parent).hasMethod(name))
					return true;

			return false;
		}

		public InterfaceDef addMethod(MethodDef method) {
			if (definesMethod(method.name())) // TODO support method overloading
				throw new RuntimeException(
						"METHOD OVERLOADING NOT SUPPORTED YET");
			if (hasMethod(method.name())) // TODO support method overriding
				throw new RuntimeException(
						"METHOD OVERRIDING NOT SUPPORTED YET");
			methods.put(method.name(), method);
			return this;
		}

		public boolean definesMethod(String name) {
			return methods.containsKey(name);
		}

		public boolean hasMethod(String name) {
			if (definesMethod(name))
				return true;

			for (String parent : parents)
				if (m_ExtInterfaceDefs.containsKey(parent)
						&& m_ExtInterfaceDefs.get(parent).hasMethod(name))
					return true;

			return false;
		}

		public MethodDef method(String name) {
			return methods.get(name);
		}

	}

	public class EnumDef extends StDef {
		private List<String> names = new ArrayList<>();
	}

	public class FieldDef extends StDef {
		private boolean isStatic;
		private boolean isFinal;
		private StEntry entry;
	}

	public class MethodDef extends StDef {
		private boolean isStatic;
		private boolean isFinal;
		private boolean isProto;
		private boolean isConstructor;
		private boolean returnsPrimitive;
		private PrimitiveType pReturnType;
		private String returnType; // can be class enum or interface name
		private Map<String, StEntry> formals = new HashMap<>();
	}

	public enum PrimitiveType {
		STR, INT, CHAR, LONG, SHORT, BYTE, BIT, BOOL, DOUBLE, FLOAT, VOID
	}

	public enum AccessLevel {
		PRIVATE, PUBLIC, PROTECTED
	}

}
