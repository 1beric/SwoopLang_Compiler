package core;

import core.Token.TokenType;

public class Parser {

	private Scanner m_Scanner;

	public Parser() {
		m_Scanner = new Scanner();
	}

	private Token m_Token;

	public void parse(String code) {

		m_Scanner.setCode(code);

		// parse & generate code!
		next();
		PARSE_prog();

	}

	private void PARSE_prog() {

		switch (m_Token.type()) {
		case USE:
			// extern_def prog
			PARSE_extern_def();
			PARSE_prog();
			break;

		case PRIVATE:
		case PROTECTED:
		case PUBLIC:
		case CLASS:
		case INTERFACE:
		case ENUM:
			// access_modifier prog_contents
			PARSE_access_modifier();
			PARSE_prog_contents();
			break;

		case EOF:
			// epsilon
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in prog's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}

	}

	private void PARSE_extern_def() {
		switch (m_Token.type()) {
		case USE:
			// EXTERN ID dot_id_chain
			assertToken(TokenType.USE);
			assertToken(TokenType.ID);
			PARSE_dot_id_chain();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in extern_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_dot_id_chain() {
		switch (m_Token.type()) {
		case PERIOD:
			// PERIOD ID id_chain
			assertToken(TokenType.PERIOD);
			assertToken(TokenType.ID);
			PARSE_dot_id_chain();
			break;

		case LPAREN:
		case USE:
		case PRIVATE:
		case PROTECTED:
		case PUBLIC:
		case CLASS:
		case INTERFACE:
		case ENUM:
		case EOF:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in dot_id_chain's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_access_modifier() {
		switch (m_Token.type()) {
		case PRIVATE:
			// PRIVATE
			assertToken(TokenType.PRIVATE);
			break;

		case PROTECTED:
			// PROTECTED
			assertToken(TokenType.PROTECTED);
			break;

		case PUBLIC:
			// PUBLIC
			assertToken(TokenType.PUBLIC);
			break;

		case CLASS:
		case INTERFACE:
		case ENUM:
		case ID:
		case STATIC:
		case FINAL:
		case VOID:
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
		case RBRACE:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in access_modifier's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_prog_contents() {
		switch (m_Token.type()) {
		case CLASS:
			// class_def
			PARSE_class_def();
			break;

		case INTERFACE:
			// interface_def
			PARSE_interface_def();
			break;

		case ENUM:
			// enum_def
			PARSE_enum_def();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in prog_contents's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_class_def() {
		switch (m_Token.type()) {
		case CLASS:
			// CLASS ID ext_impl_ep LBRACE access_modifier class_contents RBRACE
			assertToken(TokenType.CLASS);
			assertToken(TokenType.ID);
			PARSE_ext_impl_ep();
			assertToken(TokenType.LBRACE);
			PARSE_access_modifier();
			PARSE_class_contents();
			assertToken(TokenType.RBRACE);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in class_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_ext_impl_ep() {
		switch (m_Token.type()) {
		case COLON:
			// COLON ID id_chain
			assertToken(TokenType.COLON);
			assertToken(TokenType.ID);
			PARSE_id_chain();
			break;

		case LBRACE:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in ext_impl_ep's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_id_chain() {
		switch (m_Token.type()) {
		case COMMA:
			// COMMA ID id_chain
			assertToken(TokenType.COLON);
			assertToken(TokenType.ID);
			PARSE_id_chain();
			break;

		case LBRACE:
		case RBRACE:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in id_chain's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_class_contents() {
		switch (m_Token.type()) {
		case ID:
			// ID class_contents_1
			assertToken(TokenType.ID);
			PARSE_class_contents_1();
			break;

		case STATIC:
			// STATIC final_ep class_contents_2_1
			assertToken(TokenType.STATIC);
			PARSE_final_ep();
			PARSE_class_contents_2_1();
			break;

		case FINAL:
			// FINAL class_contents_2_1
			assertToken(TokenType.FINAL);
			PARSE_class_contents_2_1();
			break;

		case VOID:
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// class_contents_2
			PARSE_class_contents_2();
			break;

		case CLASS:
			// class_def access_modifier class_contents
			PARSE_class_def();
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		case INTERFACE:
			// interface_def access_modifier class_contents
			PARSE_interface_def();
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		case ENUM:
			// enum_def access_modifier class_contents
			PARSE_enum_def();
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		case RBRACE:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in class_contents's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_class_contents_1() {
		switch (m_Token.type()) {
		case ID:
			// ID class_contents_3
			assertToken(TokenType.ID);
			PARSE_class_contents_3();
			break;

		case LPAREN:
			// class_method_def access_modifier class_contents
			PARSE_class_method_def();
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in class_contents_1's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_final_ep() {
		switch (m_Token.type()) {
		case FINAL:
			// FINAL
			assertToken(TokenType.FINAL);
			break;

		case ID:
		case VOID:
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in final_ep's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_class_contents_2_1() {
		switch (m_Token.type()) {
		case ID:
			// ID ID class_contents_3
			assertToken(TokenType.ID);
			assertToken(TokenType.ID);
			PARSE_class_contents_3();
			break;

		case VOID:
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// class_contents_2
			PARSE_class_contents_2();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in class_contents_2_1's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_class_contents_2() {
		switch (m_Token.type()) {
		case VOID:
			// VOID ID class_method_def access_modifier class_contents
			assertToken(TokenType.VOID);
			assertToken(TokenType.ID);
			PARSE_class_method_def();
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type ID class_contents_3
			PARSE_var_type();
			assertToken(TokenType.ID);
			PARSE_class_contents_3();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in class_contents_2's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_var_type() {
		switch (m_Token.type()) {
		case INT:
			// INT
			assertToken(TokenType.INT);
			break;

		case CHAR:
			// CHAR
			assertToken(TokenType.CHAR);
			break;

		case STRING:
			// STRING
			assertToken(TokenType.STRING);
			break;

		case SHORT:
			// SHORT
			assertToken(TokenType.SHORT);
			break;

		case LONG:
			// LONG
			assertToken(TokenType.LONG);
			break;

		case BYTE:
			// BYTE
			assertToken(TokenType.BYTE);
			break;

		case BIT:
			// BIT
			assertToken(TokenType.BIT);
			break;

		case FLOAT:
			// FLOAT
			assertToken(TokenType.FLOAT);
			break;

		case DOUBLE:
			// DOUBLE
			assertToken(TokenType.DOUBLE);
			break;

		case BOOL:
			// BOOL
			assertToken(TokenType.BOOL);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in var_type's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_class_contents_3() {
		switch (m_Token.type()) {
		case EQUAL:
		case COMMA:
		case SEMI:
			// class_var_def access_modifier class_contents
			PARSE_class_var_def();
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		case LPAREN:
			// class_method_def access_modifier class_contents
			PARSE_class_method_def();
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in class_contents_3's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_class_var_def() {
		switch (m_Token.type()) {
		case EQUAL:
		case COMMA:
		case SEMI:
			// var_decl_ep var_chain SEMI
			PARSE_var_decl_ep();
			PARSE_var_chain();
			assertToken(TokenType.SEMI);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in class_var_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_var_decl_ep() {
		switch (m_Token.type()) {
		case EQUAL:
			// EQUAL expr
			assertToken(TokenType.EQUAL);
			PARSE_expr();
			break;

		case COMMA:
		case RPAREN:
		case SEMI:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in var_decl_ep's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_var_chain() {
		switch (m_Token.type()) {
		case COMMA:
			// COMMA ID var_decl_ep var_chain
			assertToken(TokenType.COMMA);
			assertToken(TokenType.ID);
			PARSE_var_decl_ep();
			PARSE_var_chain();
			break;

		case SEMI:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in var_chain's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}

	}

	private void PARSE_class_method_def() {
		switch (m_Token.type()) {
		case LPAREN:
			// LPAREN formals RPAREN stmt
			assertToken(TokenType.LPAREN);
			PARSE_formals();
			assertToken(TokenType.RPAREN);
			PARSE_stmt();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in class_method_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_formals() {
		switch (m_Token.type()) {
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type ID var_decl_ep formals_chain
			PARSE_var_type();
			assertToken(TokenType.ID);
			PARSE_var_decl_ep();
			PARSE_formals_chain();
			break;

		case ID:
			// ID ID var_decl_ep formals_chain
			assertToken(TokenType.ID);
			assertToken(TokenType.ID);
			PARSE_var_decl_ep();
			PARSE_formals_chain();
			break;

		case RPAREN:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in formals's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_formals_chain() {
		switch (m_Token.type()) {
		case COMMA:
			// COMMA formals_chain_1
			assertToken(TokenType.COMMA);
			PARSE_formals_chain_1();
			break;

		case RPAREN:
			// epsilon (FOLLOW set)
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in formals_chain's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_formals_chain_1() {
		switch (m_Token.type()) {
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type ID var_decl_ep formals_chain
			PARSE_var_type();
			assertToken(TokenType.ID);
			PARSE_var_decl_ep();
			PARSE_formals_chain();
			break;

		case ID:
			// ID ID var_decl_ep formals_chain
			assertToken(TokenType.ID);
			assertToken(TokenType.ID);
			PARSE_var_decl_ep();
			PARSE_formals_chain();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in formal_chain_1's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_stmt() {
		switch (m_Token.type()) {
		case ID:
			// ID stmt_1 SEMI
			assertToken(TokenType.ID);
			PARSE_stmt_1();
			assertToken(TokenType.SEMI);
			break;

		case IF:
			// if_stmt
			PARSE_if_stmt();
			break;

		case WHILE:
			// while_stmt
			PARSE_while_stmt();
			break;

		case FOR:
			// for_stmt
			PARSE_for_stmt();
			break;

		case RETURN:
			// RETURN expr_ep SEMI
			assertToken(TokenType.RETURN);
			PARSE_expr_ep();
			assertToken(TokenType.SEMI);
			break;

		case DDASH:
		case DPLUS:
			// pre_rel_assg SEMI
			PARSE_pre_rel_assg();
			assertToken(TokenType.SEMI);
			break;

		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type var_def SEMI
			PARSE_var_type();
			PARSE_var_def();
			assertToken(TokenType.SEMI);
			break;

		case LBRACE:
			// LBRACE stmts RBRACE
			assertToken(TokenType.LBRACE);
			PARSE_stmts();
			assertToken(TokenType.RBRACE);
			break;

		case SEMI:
			// SEMI
			assertToken(TokenType.SEMI);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in stmt's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_stmts() {
		switch (m_Token.type()) {
		case ID:
		case IF:
		case WHILE:
		case FOR:
		case RETURN:
		case DDASH:
		case DPLUS:
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
		case LBRACE:
		case SEMI:
			// stmt stmts
			PARSE_stmt();
			PARSE_stmts();
			break;

		case RBRACE:
			// epsilon
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in stmts's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_stmt_1() {
		switch (m_Token.type()) {
		case LPAREN:
		case PERIOD:
			// method_call
			PARSE_method_call();
			break;

		case EQUAL:
			// assg
			PARSE_assg();
			break;

		case DPLUS:
		case DDASH:
		case PLUSEQ:
		case DASHEQ:
		case STAREQ:
		case FSLASHEQ:
		case AMPERSANDEQ:
		case PIPEEQ:
		case CARETEQ:
		case PERCENTEQ:
			// rel_assg
			PARSE_rel_assg();
			break;

		case ID:
			// var_def
			PARSE_var_def();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in stmt_1's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_method_call() {
		switch (m_Token.type()) {
		case LPAREN:
		case PERIOD:
			// dot_id_chain LPAREN exprs RPAREN dot_method_call_ep
			PARSE_dot_id_chain();
			assertToken(TokenType.LPAREN);
			PARSE_exprs();
			assertToken(TokenType.RPAREN);
			PARSE_dot_method_call_ep();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in method_call's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_exprs() {
		switch (m_Token.type()) {
		case ID:
		case LBRACKET:
		case LPAREN:
		case INTCON:
		case CHARCON:
		case STRCON:
		case DPLUS:
		case DDASH:
		case DASH:
		case EXMARK:
		case TILDE:
			// expr expr_chain
			PARSE_expr();
			PARSE_expr_chain();
			break;

		case RPAREN:
			// epsilon
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in exprs's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_expr_chain() {
		switch (m_Token.type()) {
		case COMMA:
			// COMMA expr expr_chain
			assertToken(TokenType.COMMA);
			PARSE_expr();
			PARSE_expr_chain();
			break;

		case RPAREN:
			// epsilon
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in exprs's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_dot_method_call_ep() {
		switch (m_Token.type()) {
		case PERIOD:
			// PERIOD ID method_call
			assertToken(TokenType.PERIOD);
			assertToken(TokenType.ID);
			PARSE_method_call();
			break;

		case PLUS:
		case DASH:
		case STAR:
		case FSLASH:
		case AMPERSAND:
		case PIPE:
		case CARET:
		case PERCENT:
		case LANGLE:
		case RANGLE:
		case LEQ:
		case GEQ:
		case DEQ:
		case NEQ:
		case DAMPERSAND:
		case DPIPE:
		case DCARET:
		case SEMI:
		case DPERIOD:
		case RBRACKET:
		case COMMA:
		case RPAREN:
			// epsilon
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in dot_method_call_ep's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}

	}

	private void PARSE_assg() {
		switch (m_Token.type()) {
		case EQUAL:
			// EQUAL expr
			assertToken(TokenType.EQUAL);
			PARSE_expr();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in assg's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_rel_assg() {
		switch (m_Token.type()) {
		case DPLUS:
			// DPLUS
			assertToken(TokenType.DPLUS);
			break;

		case DDASH:
			// DDASH
			assertToken(TokenType.DDASH);
			break;

		case PLUSEQ:
		case DASHEQ:
		case STAREQ:
		case FSLASHEQ:
		case AMPERSANDEQ:
		case PIPEEQ:
		case CARETEQ:
		case PERCENTEQ:
			// binopeq EQUAL expr
			PARSE_binopeq();
			PARSE_expr();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in rel_assg's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_pre_rel_assg() {
		switch (m_Token.type()) {
		case DPLUS:
			// DPLUS ID
			assertToken(TokenType.DPLUS);
			assertToken(TokenType.ID);
			break;

		case DDASH:
			// DDASH ID
			assertToken(TokenType.DDASH);
			assertToken(TokenType.ID);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in pre_rel_assg's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_var_def() {
		switch (m_Token.type()) {
		case ID:
			// ID var_decl_ep var_chain
			assertToken(TokenType.ID);
			PARSE_var_decl_ep();
			PARSE_var_chain();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in var_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_if_stmt() {
		switch (m_Token.type()) {
		case IF:
			// IF LPAREN expr RPAREN stmt else_stmt
			assertToken(TokenType.IF);
			assertToken(TokenType.LPAREN);
			PARSE_expr();
			assertToken(TokenType.RPAREN);
			PARSE_stmt();
			PARSE_else_stmt();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in if_stmt's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_else_stmt() {
		switch (m_Token.type()) {
		case ELSE:
			// ELSE stmt
			assertToken(TokenType.ELSE);
			PARSE_stmt();
			break;

		case PRIVATE:
		case PROTECTED:
		case PUBLIC:
		case CLASS:
		case INTERFACE:
		case ENUM:
		case ID:
		case STATIC:
		case FINAL:
		case VOID:
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
		case PLUS:
		case DASH:
		case STAR:
		case FSLASH:
		case AMPERSAND:
		case PIPE:
		case CARET:
		case PERCENT:
		case LANGLE:
		case RANGLE:
		case LEQ:
		case GEQ:
		case DEQ:
		case NEQ:
		case DAMPERSAND:
		case DPIPE:
		case DCARET:
		case SEMI:
		case DPERIOD:
		case RBRACKET:
		case COMMA:
		case RPAREN:
		case RBRACE:
		case IF:
		case FOR:
		case RETURN:
		case DPLUS:
		case DDASH:
		case LBRACE:
		case WHILE:
		case AT:
			// epsilon
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in else_stmt's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_while_stmt() {
		switch (m_Token.type()) {
		case WHILE:
			// WHILE LPAREN expr RPAREN stmt
			assertToken(TokenType.WHILE);
			assertToken(TokenType.LPAREN);
			PARSE_expr();
			assertToken(TokenType.RPAREN);
			PARSE_stmt();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in while_stmt's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_for_stmt() {
		switch (m_Token.type()) {
		case FOR:
			// FOR LPAREN for_middle RPAREN stmt
			assertToken(TokenType.FOR);
			assertToken(TokenType.LPAREN);
			PARSE_for_middle();
			assertToken(TokenType.RPAREN);
			PARSE_stmt();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in for_stmt's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_for_middle() {
		switch (m_Token.type()) {
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type ID for_middle_1
			PARSE_var_type();
			assertToken(TokenType.ID);
			PARSE_for_middle_1();
			break;

		case ID:
			// ID for_middle_2
			assertToken(TokenType.ID);
			PARSE_for_middle_2();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in for_middle_stmt's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_for_middle_1() {
		switch (m_Token.type()) {
		case EQUAL:
			// EQUAL expr SEMI expr SEMI for_middle_3
			assertToken(TokenType.EQUAL);
			PARSE_expr();
			assertToken(TokenType.SEMI);
			PARSE_expr();
			assertToken(TokenType.SEMI);
			PARSE_for_middle_3();
			break;

		case COLON:
			// COLON for_right
			assertToken(TokenType.COLON);
			PARSE_for_right();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in for_middle_1's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_for_right() {
		switch (m_Token.type()) {
		case ID:
			// ID for_right_1
			assertToken(TokenType.ID);
			PARSE_for_right_1();
			break;

		case LBRACKET:
			// LBRACKET expr DPERIOD expr RBRACKET
			assertToken(TokenType.LBRACKET);
			PARSE_expr();
			assertToken(TokenType.DPERIOD);
			PARSE_expr();
			assertToken(TokenType.RBRACKET);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in for_right's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_for_right_1() {
		switch (m_Token.type()) {
		case LPAREN:
		case PERIOD:
			// method_call
			PARSE_method_call();
			break;

		case RPAREN:
			// epsilon
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in for_right_1's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_for_middle_2() {
		switch (m_Token.type()) {
		case ID:
			// ID for_middle_1
			assertToken(TokenType.ID);
			PARSE_for_middle_1();
			break;

		case EQUAL:
			// EQUAL expr SEMI expr SEMI for_middle_3
			assertToken(TokenType.EQUAL);
			PARSE_expr();
			assertToken(TokenType.SEMI);
			PARSE_expr();
			assertToken(TokenType.SEMI);
			PARSE_for_middle_3();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in for_middle_2's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_for_middle_3() {
		switch (m_Token.type()) {
		case ID:
			// ID for_middle_4
			assertToken(TokenType.ID);
			PARSE_for_middle_4();
			break;

		case DPLUS:
		case DDASH:
			// pre_rel_assg
			PARSE_pre_rel_assg();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in for_middle_2's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_for_middle_4() {
		switch (m_Token.type()) {
		case EQUAL:
			// assg
			PARSE_assg();
			break;

		case DPLUS:
		case DDASH:
		case PLUSEQ:
		case DASHEQ:
		case STAREQ:
		case FSLASHEQ:
		case AMPERSANDEQ:
		case PIPEEQ:
		case CARETEQ:
		case PERCENTEQ:
			// rel_assg
			PARSE_rel_assg();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in for_middle_2's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_expr_ep() {
		switch (m_Token.type()) {
		case ID:
		case LBRACKET:
		case LPAREN:
		case INTCON:
		case CHARCON:
		case STRCON:
		case DPLUS:
		case DDASH:
		case DASH:
		case TILDE:
		case EXMARK:
			// expr
			PARSE_expr();
			break;

		case SEMI:
			// epsilon
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in exprs's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_expr() {
		switch (m_Token.type()) {
		case ID:
			// ID expr_1 expr_lr
			assertToken(TokenType.ID);
			PARSE_expr_1();
//			PARSE_expr_lr();
			break;

		case DPLUS:
		case DDASH:
			// pre_rel_assg expr_lr
			PARSE_pre_rel_assg();
			break;

		case DASH:
		case TILDE:
		case EXMARK:
			PARSE_unop();
			PARSE_expr();
			break;

		case LBRACKET:
			// LBRACKET expr DPERIOD expr RBRACKET expr_lr
			assertToken(TokenType.LBRACKET);
			PARSE_expr();
			assertToken(TokenType.DPERIOD);
			PARSE_expr();
			assertToken(TokenType.RBRACKET);
			break;

		case LPAREN:
			// LPAREN expr_lambda expr_lr
			assertToken(TokenType.LPAREN);
			PARSE_expr_lambda();
			break;

		case INTCON:
		case CHARCON:
		case STRCON:
			// const expr_lr
			PARSE_const();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in expr's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_expr_1() {
		switch (m_Token.type()) {
		case PERIOD:
		case LPAREN:
			// method_call
			PARSE_method_call();
			break;

		case EQUAL:
			// assg
			PARSE_assg();
			break;

		case DPLUS:
		case DDASH:
		case PLUSEQ:
		case DASHEQ:
		case STAREQ:
		case FSLASHEQ:
		case AMPERSANDEQ:
		case PIPEEQ:
		case CARETEQ:
		case PERCENTEQ:
			// rel_assg
			PARSE_rel_assg();
			break;

//		case PLUS:
//		case DASH:
//		case STAR:
//		case FSLASH:
//		case AMPERSAND:
//		case PIPE:
//		case CARET:
//		case PERCENT:
//		case LANGLE:
//		case RANGLE:
//		case LEQ:
//		case GEQ:
//		case DEQ:
//		case NEQ:
//		case DAMPERSAND:
//		case DPIPE:
//		case DCARET:
		case DPERIOD:
		case RBRACKET:
		case COMMA:
		case RPAREN:
		case SEMI:
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in for_middle_2's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_expr_lambda() {
		switch (m_Token.type()) {
		case ID:
		case LBRACKET:
		case LPAREN:
		case INTCON:
		case CHARCON:
		case STRCON:
		case DPLUS:
		case DDASH:
		case DASH:
		case EXMARK:
		case TILDE:
			// expr RPAREN
			PARSE_expr();
			assertToken(TokenType.RPAREN);
			break;

//		case INT:
//		case CHAR:
//		case STRING:
//		case SHORT:
//		case LONG:
//		case BYTE:
//		case BIT:
//		case FLOAT:
//		case DOUBLE:
//		case BOOL:
//		case ID:
//		case RPAREN:
//			// formals RPAREN ARROW stmt
//			PARSE_formals();
//			assertToken(TokenType.RPAREN);
//			assertToken(TokenType.ARROW);
//			PARSE_stmt();
//			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in expr_lambda's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_const() {
		switch (m_Token.type()) {
		case INTCON:
			// INTCON deci_ep
			assertToken(TokenType.INTCON);
			PARSE_deci_ep();
			break;
		case CHARCON:
			// CHARCON
			assertToken(TokenType.CHARCON);
			break;
		case STRCON:
			// STRCON
			assertToken(TokenType.STRCON);
			break;
		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in const's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_deci_ep() {
		switch (m_Token.type()) {
		case PERIOD:
			// PERIOD INTCON
			assertToken(TokenType.PERIOD);
			assertToken(TokenType.INTCON);
			break;

		case DPERIOD:
		case RBRACKET:
		case COMMA:
		case RPAREN:
		case SEMI:
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in const's FIRST or FOLLOW sets. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_unop() {
		switch (m_Token.type()) {
		case EXMARK:
			// EXMARK
			assertToken(TokenType.EXMARK);
			break;

		case DASH:
			// DASH
			assertToken(TokenType.DASH);
			break;

		case TILDE:
			// TILDE
			assertToken(TokenType.TILDE);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in unop's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_binop() {
		switch (m_Token.type()) {
		case PLUS:
			// PLUS
			assertToken(TokenType.PLUS);
			break;

		case DASH:
			// DASH
			assertToken(TokenType.DASH);
			break;

		case STAR:
			// STAR
			assertToken(TokenType.STAR);
			break;

		case FSLASH:
			// FSLASH
			assertToken(TokenType.FSLASH);
			break;

		case AMPERSAND:
			// AMPERSAND
			assertToken(TokenType.AMPERSAND);
			break;

		case PIPE:
			// PIPE
			assertToken(TokenType.PIPE);
			break;

		case CARET:
			// CARET
			assertToken(TokenType.CARET);
			break;

		case PERCENT:
			// PERCENT
			assertToken(TokenType.PERCENT);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in binop's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_binopeq() {
		switch (m_Token.type()) {
		case PLUSEQ:
			// PLUSEQ
			assertToken(TokenType.PLUSEQ);
			break;

		case DASHEQ:
			// DASHEQ
			assertToken(TokenType.DASHEQ);
			break;

		case STAREQ:
			// STAREQ
			assertToken(TokenType.STAREQ);
			break;

		case FSLASHEQ:
			// FSLASHEQ
			assertToken(TokenType.FSLASHEQ);
			break;

		case AMPERSANDEQ:
			// AMPERSANDEQ
			assertToken(TokenType.AMPERSANDEQ);
			break;

		case PIPEEQ:
			// PIPEEQ
			assertToken(TokenType.PIPEEQ);
			break;

		case CARETEQ:
			// CARETEQ
			assertToken(TokenType.CARETEQ);
			break;

		case PERCENTEQ:
			// PERCENTEQ
			assertToken(TokenType.PERCENTEQ);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in binopeq's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_relop() {
		switch (m_Token.type()) {
		case LANGLE:
			// LANGLE
			assertToken(TokenType.LANGLE);
			break;

		case RANGLE:
			// RANGLE
			assertToken(TokenType.RANGLE);
			break;

		case LEQ:
			// LEQ
			assertToken(TokenType.LEQ);
			break;

		case GEQ:
			// GEQ
			assertToken(TokenType.GEQ);
			break;

		case DEQ:
			// DEQ
			assertToken(TokenType.DEQ);
			break;

		case NEQ:
			// NEQ
			assertToken(TokenType.NEQ);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in relop's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_logop() {
		switch (m_Token.type()) {
		case DAMPERSAND:
			// DAMPERSAND
			assertToken(TokenType.DAMPERSAND);
			break;

		case DPIPE:
			// DPIPE
			assertToken(TokenType.DPIPE);
			break;

		case DCARET:
			// DCARET
			assertToken(TokenType.DCARET);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in logop's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_interface_def() {
		switch (m_Token.type()) {
		case INTERFACE:
			// INTERFACE ID ext_impl_ep LBRACE interface_contents RBRACE
			assertToken(TokenType.INTERFACE);
			assertToken(TokenType.ID);
			PARSE_ext_impl_ep();
			assertToken(TokenType.LBRACE);
			PARSE_interface_contents();
			assertToken(TokenType.RBRACE);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in interface_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_interface_contents() {
		switch (m_Token.type()) {
		case AT:
			// interface_method interface_contents
			PARSE_interface_method();
			PARSE_interface_contents();
			break;

		case PRIVATE:
		case PROTECTED:
		case PUBLIC:
			// prototype_def interface_contents
			PARSE_prototype_def();
			PARSE_interface_contents();
			break;

		case RBRACE:
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in interface_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_interface_method() {
		switch (m_Token.type()) {
		case AT:
			// AT DEFINE access_modifier ret_type ID class_method_def
			assertToken(TokenType.AT);
			assertToken(TokenType.DEFINE);
			PARSE_access_modifier();
			PARSE_ret_type();
			assertToken(TokenType.ID);
			PARSE_class_method_def();
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in interface_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_ret_type() {
		switch (m_Token.type()) {
		case INT:
		case CHAR:
		case STRING:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// vartype
			PARSE_var_type();
			break;

		case VOID:
			// VOID
			assertToken(TokenType.VOID);
			break;

		case ID:
			// ID
			assertToken(TokenType.ID);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in var_type's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_prototype_def() {
		switch (m_Token.type()) {
		case PRIVATE:
		case PROTECTED:
		case PUBLIC:
			// access_modifier ret_type ID LPAREN formals RPAREN SEMI
			PARSE_access_modifier();
			PARSE_ret_type();
			assertToken(TokenType.ID);
			assertToken(TokenType.LPAREN);
			PARSE_formals();
			assertToken(TokenType.RPAREN);
			assertToken(TokenType.SEMI);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in interface_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_enum_def() {
		switch (m_Token.type()) {
		case ENUM:
			// ENUM ID LBRACE id_ep id_chain RBRACE
			assertToken(TokenType.ENUM);
			assertToken(TokenType.ID);
			assertToken(TokenType.LBRACE);
			PARSE_id_ep();
			PARSE_id_chain();
			assertToken(TokenType.RBRACE);
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in enum_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private void PARSE_id_ep() {
		switch (m_Token.type()) {
		case ID:
			// ID
			assertToken(TokenType.ID);
			break;

		case RBRACE:
			break;

		default:
			throw new RuntimeException("The current token (" + m_Token
					+ ") is not in enum_def's FIRST set. LINE --- "
					+ m_Scanner.s_LineNumber);
		}
	}

	private boolean assertToken(TokenType type) {
		if (m_Token.is(type)) {
			next();
			return true;
		}
		throw new RuntimeException("The current token (" + m_Token
				+ ") was expected to be (" + type + ")");
	}

	private Token next() {
		m_Token = m_Scanner.next();
		return m_Token;
	}

}
