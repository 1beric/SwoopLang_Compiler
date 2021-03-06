package core;

public class Token {

	public static String[] s_Keywords = new String[] { "if", "else", "while",
			"for", "return", "class", "struct", "define", "void", "public",
			"protected", "private", "static", "final", "use", "int", "short",
			"long", "byte", "bit", "char", "float", "double", "bool", "str",
			"enum", "extends", "implements", "interface", "true", "false",
			"new", "this", "null" };

	private TokenType m_Type;
	private int m_IntCon = 0;
	private char m_CharCon = '\0';
	private String m_StrCon = "";

	public Token(TokenType type) {
		m_Type = type;
	}

	public Token(TokenType type, int con) {
		m_Type = type;
		m_IntCon = con;
	}

	public Token(TokenType type, char con) {
		m_Type = type;
		m_CharCon = con;
	}

	public Token(TokenType type, String con) {
		m_Type = type;
		m_StrCon = con;
	}

	public int intCon() {
		return m_IntCon;
	}

	public char charCon() {
		return m_CharCon;
	}

	public String strCon() {
		return m_StrCon;
	}

	public TokenType type() {
		return m_Type;
	}

	public boolean is(TokenType type) {
		return m_Type.equals(type);
	}

	@Override
	public String toString() {
		if (m_Type.equals(TokenType.ID))
			return "ID: " + m_StrCon;
		if (m_Type.equals(TokenType.STRCON))
			return "STRCON: " + m_StrCon;
		if (m_Type.equals(TokenType.CHARCON))
			return "CHARCON: " + m_CharCon;
		if (m_Type.equals(TokenType.INTCON))
			return "INTCON: " + m_IntCon;
		return "" + m_Type;
	}

	public enum TokenType {

		UNDEF, EOF, ID, // := letter { letter | digit | _ } - kws
		INTCON, // := digit { digit }
		CHARCON, // := 'ch' | '\n' | '\0' | '\\' | '\'', where ch denotes any
					// printable ASCII character, as specified by isprint(),
					// other than \
					// (backslash) and ' (single quote).
		STRCON, // := "{ch}" | `{ch}`, where ch denotes any printable ASCII
				// character (as specified by isprint()) other than " (double
				// quotes) and the
				// newline character.
		IF, // := if
		ELSE, // := else
		WHILE, // := while
		FOR, // := for
		RETURN, // := return
		CLASS, // := class
		DEFINE, // := define
		VOID, // := void
		PUBLIC, // := public
		PRIVATE, // := private
		PROTECTED, // := protected
		STATIC, // := static
		USE, // := use
		FINAL, // := final
		INT, // := int
		SHORT, // := short
		LONG, // := long
		BYTE, // := byte
		BIT, // := bit
		CHAR, // := char
		FLOAT, // := float
		DOUBLE, // := double
		BOOL, // := bool
		STR, // := string
		TRUE, // := true
		FALSE, // := false
		ENUM, // := enum
		INTERFACE, // := interface
		NEW, // := new
		THIS, // := this
		NULL, // := null
		LEQ, // := <=
		GEQ, // := >=
		DEQ, // := ==
		NEQ, // := !=
		ARROW, // := ->
		EQUAL, // := =
		PLUS, // := +
		DPLUS, // := ++
		PLUSEQ, // := +=
		DASH, // := -
		DDASH, // := --
		DASHEQ, // := -=
		STAR, // := *
		STAREQ, // := *=
		DSTAR, // := **
		FSLASH, // := /
		FSLASHEQ, // := /=
		BSLASH, // := \
		PIPE, // := |
		DPIPE, // := ||
		PIPEEQ, // := |=
		LPAREN, // := (
		RPAREN, // := )
		LBRACKET, // := [
		RBRACKET, // := ]
		LANGLE, // := <
		DLANGLE, // := <<
		RANGLE, // := >
		DRANGLE, // := >>
		LBRACE, // := {
		RBRACE, // := }
		UNDERSCORE, // := _
		PERIOD, // := .
		DPERIOD, // := ..
		TPERIOD, // := ...
		COMMA, // := ,
		SEMI, // := ;
		COLON, // := :
		QMARK, // := ?
		EXMARK, // := !
		AMPERSAND, // := &
		DAMPERSAND, // := &&
		AMPERSANDEQ, // := &=
		AT, // := @
		HASH, // := #
		DOLLAR, // := $
		PERCENT, // := %
		PERCENTEQ, // := %=
		CARET, // := ^
		DCARET, // := ^^
		CARETEQ, // := ^=
		TILDE // := ~
	}

}
