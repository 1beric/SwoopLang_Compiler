package core.symtab;

public class FieldDef extends InnerDef {
	// modifiers
	private boolean m_Final;
	private boolean m_Static;

	// if primitive
	private boolean m_Primitive;
	private PrimitiveType m_PrimitiveType;

	// if class, interface, or enum
	private String m_ObjectType;

	public FieldDef() {

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
	 * @param m_Final m_Final to set
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
	 * @param m_Static m_Static to set
	 */
	public void isStatic(boolean isStatic) {
		this.m_Static = isStatic;
	}

	@Override
	public void debugPrint() {

		System.out.print(accessLevel().toString().toLowerCase() + " ");
		if (isStatic())
			System.out.print("static ");
		if (isFinal())
			System.out.print("final ");
		System.out.print(type() + " ");
		System.out.print(id());

	}

}