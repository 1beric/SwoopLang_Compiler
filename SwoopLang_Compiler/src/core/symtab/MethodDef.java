package core.symtab;

import java.util.List;

import core.ErrorHandler;

public class MethodDef extends InnerDef {
	// modifiers
	private boolean m_Final;
	private boolean m_Static;
	private boolean m_Proto;

	// if primitive
	private boolean m_Primitive;
	private PrimitiveType m_PrimitiveType;

	// if class, interface, or enum
	private String m_ObjectType;

	// formals
	private List<FieldDef> m_Formals; // reusing FieldDef for FormalDef

	// SymbolTable
	private SymbolTable m_LocalScope;

	// constructor
	public MethodDef() {
		m_LocalScope = new SymbolTable(SymbolTableType.LOCAL);
	}

	public void setFormals(List<FieldDef> formals) {
		m_Formals = formals;
		for (FieldDef formalDef : m_Formals) {
			if (formalDef.primitive()) {
				// build primitive
				PrimitiveEntry entry = new PrimitiveEntry(
						formalDef.primitiveType());
				entry.id(formalDef.id());
				entry.formal(true);

				// insert primitive
				m_LocalScope.insertEntry(entry);
			} else if (SymbolTable.fileSt().m_ClassDefs
					.containsKey(formalDef.objectType())) {
				// build class entry
				ClassEntry entry = new ClassEntry();
				entry.objectType(entry.objectType());
				entry.id(formalDef.id());

				// insert class entry
				m_LocalScope.insertEntry(entry);
			} else if (SymbolTable.fileSt().m_InterfaceDefs
					.containsKey(formalDef.objectType())) {
				// build interface entry
				InterfaceEntry entry = new InterfaceEntry();
				entry.objectType(entry.objectType());
				entry.id(formalDef.id());

				// insert interface entry
				m_LocalScope.insertEntry(entry);
			} else if (SymbolTable.fileSt().m_EnumDefs
					.containsKey(formalDef.objectType())) {
				// build enum entry
				EnumEntry entry = new EnumEntry();
				entry.objectType(entry.objectType());
				entry.id(formalDef.id());

				// insert enum entry
				m_LocalScope.insertEntry(entry);
			} else
				ErrorHandler.error("The formal " + formalDef.id()
						+ "is not a known type " + formalDef.objectType());
		}
	}

	/**
	 * @return m_LocalScope
	 */
	public SymbolTable localScope() {
		return m_LocalScope;
	}

	public List<FieldDef> formals() {
		return m_Formals;
	}

	public void type(String type) {
		if (PrimitiveType.contains(type)) {
			m_Primitive = true;
			m_PrimitiveType = PrimitiveType.valueOf(type.toUpperCase());
		} else {
			m_Primitive = false;
			m_ObjectType = type;
		}
	}

	public String type() {
		if (m_Primitive)
			return m_PrimitiveType.toString().toLowerCase();
		return m_ObjectType;
	}

	public boolean primitive() {
		return m_Primitive;
	}

	public PrimitiveType primitiveType() {
		return m_PrimitiveType;
	}

	public String objectType() {
		return m_ObjectType;
	}

	/**
	 * @return m_Final
	 */
	public boolean isFinal() {
		return m_Final;
	}

	/**
	 * @param isFinal m_Final to set
	 */
	public void isFinal(boolean isFinal) {
		this.m_Final = isFinal;
	}

	/**
	 * @return m_Static
	 */
	public boolean isStatic() {
		return m_Static;
	}

	/**
	 * @param isStatic m_Static to set
	 */
	public void isStatic(boolean isStatic) {
		this.m_Static = isStatic;
	}

	/**
	 * @return m_Proto
	 */
	public boolean isProto() {
		return m_Proto;
	}

	/**
	 * @param isProto m_Proto to set
	 */
	public void isProto(boolean isProto) {
		this.m_Proto = isProto;
	}

	@Override
	public void debugPrint() {

		System.out.print(accessLevel().toString().toLowerCase() + " ");
		if (isStatic())
			System.out.print("static ");
		if (isFinal())
			System.out.print("final ");
		System.out.print(type() + " ");
		System.out.print(id() + " (");

		int numFormals = 0;
		for (FieldDef def : m_Formals) {
			System.out.print(def.type() + " " + def.id());
			if (++numFormals != m_Formals.size())
				System.out.print(", ");
		}

		System.out.print(")");

	}

}