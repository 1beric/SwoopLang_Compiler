package core.symtab;

public abstract class FileDef extends StDef {
	// the libaray this def fits into
	private String m_Library;

	/**
	 * @return m_Library
	 */
	public String library() {
		return m_Library;
	}

	/**
	 * @param library m_Library to set
	 */
	public void library(String library) {
		this.m_Library = library;
	}

	public void field(FieldDef def) {
	}

	public FieldDef field(String id) {
		return null;
	}

	public void constructor(ConstructorDef def) {
	}

	public ConstructorDef constructor(String id) {
		return null;
	}

	public void method(MethodDef def) {
	}

	public MethodDef method(String id) {
		return null;
	}

}