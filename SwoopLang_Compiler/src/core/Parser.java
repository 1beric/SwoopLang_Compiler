package core;

import core.Token.TokenType;

public class Parser {

	private Scanner m_Scanner;

	public Parser() {
		m_Scanner = new Scanner();
	}

	public void parse(String code) {

		m_Scanner.setCode(code);

		// parse & generate code!
		while (true) {
			Token tok = m_Scanner.next();
			if (tok.type().equals(TokenType.EOF))
				break;
//			if (!tok.type().equals(TokenType.PLUS))
			System.out.println(tok);
		}
		System.out.println("LINES: " + m_Scanner.s_LineNumber);
	}

}
