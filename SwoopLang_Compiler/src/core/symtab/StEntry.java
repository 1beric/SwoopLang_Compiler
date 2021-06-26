package core.symtab;

public abstract class StEntry {
	// the entry's id
	private String m_Id;

	// if the entry is a formal
	private boolean m_Formal;

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

	/**
	 * @return m_Formal
	 */
	public boolean formal() {
		return m_Formal;
	}

	/**
	 * @param formal m_Formal to set
	 */
	public void formal(boolean formal) {
		this.m_Formal = formal;
	}

	public abstract String type();

	protected abstract void debugPrint();

}