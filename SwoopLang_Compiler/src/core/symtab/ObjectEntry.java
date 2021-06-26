package core.symtab;

public abstract class ObjectEntry extends StEntry {
	// the libaray this def fits into
	private String m_Library;

	// the type of object
	private String m_ObjectType;

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

	/**
	 * @return m_ObjectType
	 */
	public String objectType() {
		return m_ObjectType;
	}

	/**
	 * @param objectType m_ObjectType to set
	 */
	public void objectType(String objectType) {
		this.m_ObjectType = objectType;
	}

	public String type() {
		return objectType();
	}

	@Override
	protected void debugPrint() {
		System.out.print(objectType() + " " + id());
	}

}