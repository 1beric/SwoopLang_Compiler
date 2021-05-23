package core;

import core.Token.TokenType;

public class Scanner {

	public int s_LineNumber = 1;

	private String m_Code = "";
	private int m_Index = 0;

	public void setCode(String code) {
		m_Code = code;
	}

	public Token next() {
		// need to check for EOF / UNDEF
		if (m_Index == m_Code.length())
			return new Token(TokenType.EOF);
		if (m_Index > m_Code.length())
			return new Token(TokenType.UNDEF);

		while (Character.isWhitespace(getChar()) || checkForComment()) {
			if (getChar() == '\n')
				s_LineNumber++;
			if (Character.isWhitespace(getChar()))
				moveIndex(1);
		}

		// kws and IDs
		if (Character.isAlphabetic(getChar()))
			return beginsAlphabetical();

		// INTCON
		if (Character.isDigit(getChar()))
			return intCon();

		// CHARCON
		if (getChar() == '\'')
			return charCon();

		// STRCON
		if (getChar() == '\"')
			return strCon();

		// check for the rest
		switch (getChar()) {
		case '=':
			if (safe(1) && getChar(1) == '=') {
				moveIndex(2);
				return new Token(TokenType.DEQ);
			}
			moveIndex(1);
			return new Token(TokenType.EQUAL);
		case '+':
			if (safe(1) && getChar(1) == '+') {
				moveIndex(2);
				return new Token(TokenType.DPLUS);
			}
			moveIndex(1);
			return new Token(TokenType.PLUS);
		case '-':
			if (safe(1) && getChar(1) == '-') {
				moveIndex(2);
				return new Token(TokenType.DDASH);
			}
			if (safe(1) && getChar(1) == '>') {
				moveIndex(2);
				return new Token(TokenType.ARROW);
			}
			moveIndex(1);
			return new Token(TokenType.DASH);
		case '*':
			moveIndex(1);
			return new Token(TokenType.STAR);
		case '/':
			moveIndex(1);
			return new Token(TokenType.FSLASH);
		case '\\':
			moveIndex(1);
			return new Token(TokenType.BSLASH);
		case '|':
			if (safe(1) && getChar(1) == '|') {
				moveIndex(2);
				return new Token(TokenType.DPIPE);
			}
			moveIndex(1);
			return new Token(TokenType.PIPE);
		case '(':
			moveIndex(1);
			return new Token(TokenType.LPAREN);
		case ')':
			moveIndex(1);
			return new Token(TokenType.RPAREN);
		case '[':
			moveIndex(1);
			return new Token(TokenType.LBRACKET);
		case ']':
			moveIndex(1);
			return new Token(TokenType.RBRACKET);
		case '{':
			moveIndex(1);
			return new Token(TokenType.LBRACE);
		case '}':
			moveIndex(1);
			return new Token(TokenType.RBRACE);
		case '<':
			if (safe(1) && getChar(1) == '=') {
				moveIndex(2);
				return new Token(TokenType.LEQ);
			}
			moveIndex(1);
			return new Token(TokenType.LANGLE);
		case '>':
			if (safe(1) && getChar(1) == '=') {
				moveIndex(2);
				return new Token(TokenType.GEQ);
			}
			moveIndex(1);
			return new Token(TokenType.RANGLE);
		case '_':
			moveIndex(1);
			return new Token(TokenType.UNDERSCORE);
		case '.':
			if (safe(2) && getChar(1) == '.' && getChar(2) == '.') {
				moveIndex(3);
				return new Token(TokenType.TPERIOD);
			}
			if (safe(1) && getChar(1) == '.') {
				moveIndex(2);
				return new Token(TokenType.DPERIOD);
			}
			moveIndex(1);
			return new Token(TokenType.PERIOD);
		case ',':
			moveIndex(1);
			return new Token(TokenType.COMMA);
		case ';':
			moveIndex(1);
			return new Token(TokenType.SEMI);
		case ':':
			moveIndex(1);
			return new Token(TokenType.COLON);
		case '?':
			moveIndex(1);
			return new Token(TokenType.QMARK);
		case '!':
			if (safe(1) && getChar(1) == '=') {
				moveIndex(2);
				return new Token(TokenType.NEQ);
			}
			moveIndex(1);
			return new Token(TokenType.EXMARK);
		case '&':
			if (safe(1) && getChar(1) == '&') {
				moveIndex(2);
				return new Token(TokenType.DAMPERSAND);
			}
			moveIndex(1);
			return new Token(TokenType.AMPERSAND);
		case '@':
			moveIndex(1);
			return new Token(TokenType.AT);
		case '#':
			moveIndex(1);
			return new Token(TokenType.HASH);
		case '$':
			moveIndex(1);
			return new Token(TokenType.DOLLAR);
		case '%':
			moveIndex(1);
			return new Token(TokenType.PERCENT);
		case '^':
			moveIndex(1);
			return new Token(TokenType.UPCARET);
		case '~':
			moveIndex(1);
			return new Token(TokenType.TILDE);
		}

		return new Token(TokenType.UNDEF);
	}

	/**
	 * checkForComment() Scans the next characters (by m_Index in m_Code) to
	 * check if there is a comment that should be skipped. This code manipulates
	 * m_Index, but leaves m_Code alone.
	 * 
	 * @return if a comment was removed
	 */
	private boolean checkForComment() {
		if (!safe(1))
			return false;
		if (getChar() == '/' && getChar(1) == '*') {
			// handle comment & get rid of it
			moveIndex(2);
			while (true) {
				if (!safe(1))
					throw new RuntimeException(
							"Must close comment at end of file.");
				if (getChar() == '*' && getChar(1) == '/') {
					moveIndex(2);
					return true;
				}
				if (getChar() == '\n')
					s_LineNumber++;
				moveIndex(1);
			}
		}

		return false;
	}

	/**
	 * beginsAlphabetical() Scans the next characters to check for kws. If no
	 * keywords are found, it procedes to check for an ID
	 * 
	 * @return the next token
	 */
	private Token beginsAlphabetical() {
		String next32 = m_Code.substring(m_Index,
				m_Index + 32 > m_Code.length() ? m_Code.length()
						: m_Index + 32);
		String str = "";
		for (String kw : Token.s_Keywords) {
			if (next32.startsWith(kw)) {
				// update m_Index and return the kw's token
				moveIndex(kw.length());
				if (Character.isAlphabetic(getChar())
						|| Character.isDigit(getChar()) || getChar() == '_') {
					// cannot be this kw, continue:
					moveIndex(-kw.length());
					continue;
				}
				return new Token(TokenType.valueOf(kw.toUpperCase()));
			}
		}
		// no kws were found, must be an ID
		while (Character.isAlphabetic(getChar()) || Character.isDigit(getChar())
				|| getChar() == '_') {
			str += getChar();
			moveIndex(1);
		}
		return new Token(TokenType.ID, str);
	}

	/**
	 * intCon() Scans the next characters for an INTCON
	 * 
	 * @return an INTCON token
	 */
	private Token intCon() {
		String str = "";
		while (Character.isDigit(getChar()) && safe(0)) {
			str += getChar();
			moveIndex(1);
		}
		if (safe(0) && Character.isAlphabetic(getChar()))
			throw new RuntimeException(
					"Code cannot have alphabetic character following INTCON");
		if (safe(0) && getChar() == '_')
			throw new RuntimeException("Code cannot have _ following INTCON");
		return new Token(TokenType.INTCON, Integer.parseInt(str));
	}

	/**
	 * charCon() Scans the next characters for a CHARCON
	 * 
	 * @return a CHARCON token
	 */
	private Token charCon() {
		if (!safe(2))
			throw new RuntimeException("Cannot end file with open CHARCON");
		char ch = getChar(1);
		if (ch == '\\') {
			ch = getChar(2);
			moveIndex(1);
		}
		if (getChar(2) != '\'')
			throw new RuntimeException(
					"CHARCON tokens must contain only one character");

		moveIndex(3);
		return new Token(TokenType.CHARCON, ch);
	}

	/**
	 * strCon() Scans the next characters for a STRCON
	 * 
	 * @return a STRCON token
	 */
	private Token strCon() {
		String str = "";
		moveIndex(1); // to pass "
		while (getChar() != '"') {
			if (getChar() == '\\') {
				str += getChar(1);
				moveIndex(1);
			} else
				str += getChar();
			moveIndex(1);
		}
		moveIndex(1); // to pass "
		if (m_Index >= m_Code.length())
			throw new RuntimeException("Cannot end file with open STRCON");
		return new Token(TokenType.STRCON, str);
	}

	/**
	 * safe(int addend) Validates that m_Index + addend is in bounds of m_Code
	 * 
	 * @param addend The value to add to m_Index
	 * @return if the value is in bounds
	 */
	private boolean safe(int addend) {
		return m_Index + addend < m_Code.length();
	}

	/**
	 * moveIndex(int addend) Moves m_Index by addend
	 * 
	 * @param addend The amount to move m_Index by
	 */
	private void moveIndex(int addend) {
		m_Index += addend;
	}

	/**
	 * getChar(int addend) Gets the character in m_Code at the point m_Code +
	 * addend
	 * 
	 * @param addend the amount to add to m_Index
	 * @return the character
	 */
	private char getChar(int addend) {
		return m_Code.charAt(m_Index + addend);
	}

	/**
	 * getChar() A default method to getChar(int)
	 * 
	 * @return getChar(0)
	 */
	private char getChar() {
		return getChar(0);
	}
}
