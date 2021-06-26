package core.symtab;

import java.util.List;

import core.ErrorHandler;

public class ConstructorDef extends InnerDef {
	// formals
	private List<FieldDef> m_Formals; // reusing FieldDef for FormalDef

	// SymbolTable
	private SymbolTable m_LocalScope;

	// constructor
	public ConstructorDef() {
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

	@Override
	public void debugPrint() {

		System.out.print(accessLevel().toString().toLowerCase() + " ");
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