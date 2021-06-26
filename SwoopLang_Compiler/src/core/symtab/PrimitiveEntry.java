package core.symtab;

public class PrimitiveEntry extends StEntry {
	private PrimitiveType m_Type;

	public PrimitiveEntry(PrimitiveType type) {
		m_Type = type;
	}

	public PrimitiveEntry(String strType) {
		this(PrimitiveType.valueOf(strType.toUpperCase()));
	}

	public String type() {
		return m_Type.toString().toLowerCase();
	}

	@Override
	protected void debugPrint() {
		System.out.print(type() + " " + id());
	}

}