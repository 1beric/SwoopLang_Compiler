package core.symtab;

public abstract class StDef {
	// the def's id
	private String m_Id;

	/**
	 * @return m_Id
	 */
	public String id() {
		return m_Id;
	}

	/**
	 * @param id m_Id to set
	 */
	public void id(String id) {
		this.m_Id = id;
	}

	public abstract void debugPrint();

}