package core.symtab;

import core.ast.AbstractSyntaxTree;

public abstract class InnerDef extends StDef {
	// level of access
	private AccessLevel m_AccessLevel;

	// initialization for fields, and code to run for methods/constructors
	private AbstractSyntaxTree m_AstHead;

	/**
	 * @return m_AccessLevel
	 */
	public AccessLevel accessLevel() {
		return m_AccessLevel;
	}

	/**
	 * @param accessLevel m_AccessLevel to set
	 */
	public void accessLevel(AccessLevel accessLevel) {
		this.m_AccessLevel = accessLevel;
	}

	/**
	 * @return m_AstHead
	 */
	public AbstractSyntaxTree astHead() {
		return m_AstHead;
	}

	/**
	 * @param astHead m_AstHead to set
	 */
	public void astHead(AbstractSyntaxTree astHead) {
		this.m_AstHead = astHead;
	}

}