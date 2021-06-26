package core;

import java.util.ArrayList;
import java.util.List;

import core.Token.TokenType;
import core.symtab.AccessLevel;
import core.symtab.ClassDef;
import core.symtab.ConstructorDef;
import core.symtab.EnumDef;
import core.symtab.FieldDef;
import core.symtab.InterfaceDef;
import core.symtab.MethodDef;
import core.symtab.PrimitiveType;
import core.symtab.SymbolTable;
import core.symtab.SymbolTableType;

public class Parser {

	private Scanner m_Scanner;

	private SymbolTable m_CurrentSt;
	private AccessLevel m_CurrentAccessLevel;

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

		case CLASS:
		case INTERFACE:
		case ENUM:
			// prog_contents
			PARSE_prog_contents();

			SymbolTable.fileDef().debugPrint();

			break;

		case EOF:
			// epsilon
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in prog's FIRST set");
		}

	}

	private void PARSE_extern_def() {
		switch (m_Token.type()) {
		case USE:
			// EXTERN ID dot_id_chain SEMI
			assertToken(TokenType.USE);
			List<String> ids = new ArrayList<>();
			ids.add(assertToken(TokenType.ID).strCon());
			assertToken(TokenType.PERIOD);
			ids.add(assertToken(TokenType.ID).strCon());
			PARSE_dot_id_chain(ids);
			// TODO import the class/interface/enum

			assertToken(TokenType.SEMI);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in extern_def's FIRST set");
		}
	}

	private void PARSE_dot_id_chain(List<String> ids) {
		switch (m_Token.type()) {
		case PERIOD:
			// PERIOD ID id_chain
			assertToken(TokenType.PERIOD);
			ids.add(assertToken(TokenType.ID).strCon());
			PARSE_dot_id_chain(ids);
			break;

//		case LPAREN:
//		case USE:
//		case PRIVATE:
//		case PROTECTED:
//		case PUBLIC:
//		case CLASS:
//		case INTERFACE:
//		case ENUM:
//		case EOF:
//			// epsilon (FOLLOW set)
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in dot_id_chain's FIRST set"
//					 );
		}
	}

	private void PARSE_access_modifier() {
		switch (m_Token.type()) {
		case PRIVATE:
			// PRIVATE
			assertToken(TokenType.PRIVATE);
			m_CurrentAccessLevel = AccessLevel.PRIVATE;
			break;

		case PROTECTED:
			// PROTECTED
			assertToken(TokenType.PROTECTED);
			m_CurrentAccessLevel = AccessLevel.PROTECTED;
			break;

		case PUBLIC:
			// PUBLIC
			assertToken(TokenType.PUBLIC);
			m_CurrentAccessLevel = AccessLevel.PUBLIC;
			break;

//		case CLASS:
//		case INTERFACE:
//		case ENUM:
//		case ID:
//		case STATIC:
//		case FINAL:
//		case VOID:
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
//		case RBRACE:
//			// epsilon (FOLLOW set)
//			m_CurrentAccessLevel = AccessLevel.PUBLIC;
//			break;

		default:
			// epsilon in default
			m_CurrentAccessLevel = AccessLevel.PUBLIC;
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in access_modifier's FIRST or FOLLOW sets"
//					 );
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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in prog_contents's FIRST set");
		}
	}

	private void PARSE_class_def() {
		switch (m_Token.type()) {
		case CLASS:
			// CLASS ID ext_impl_ep LBRACE access_modifier class_contents RBRACE
			assertToken(TokenType.CLASS);
			Token idToken = assertToken(TokenType.ID);
			List<String> parents = new ArrayList<>();
			PARSE_ext_impl_ep(parents);

			// semantics
			SymbolTable.set(SymbolTableType.CLASS);
			ClassDef def = (ClassDef) SymbolTable.fileDef();
			def.id(idToken.strCon());
			def.library("__MAIN__"); // TODO allow custom libraries
			def.parents(parents);
			SymbolTable.fileSt().addClass(def);

			assertToken(TokenType.LBRACE);
			PARSE_access_modifier();
			PARSE_class_contents();
			assertToken(TokenType.RBRACE);

			// ast
			// TODO ast for class_def

			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in class_def's FIRST set");
		}
	}

	private void PARSE_ext_impl_ep(List<String> parents) {
		switch (m_Token.type()) {
		case COLON:
			// COLON ID id_chain
			assertToken(TokenType.COLON);
			parents.add(assertToken(TokenType.ID).strCon());
			PARSE_id_chain(parents);
			break;

//		case LBRACE:
//			// epsilon (FOLLOW set)
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in ext_impl_ep's FIRST or FOLLOW sets"
//					 );
		}
	}

	private void PARSE_id_chain(List<String> ids) {
		switch (m_Token.type()) {
		case COMMA:
			// COMMA ID id_chain
			assertToken(TokenType.COMMA);
			ids.add(assertToken(TokenType.ID).strCon());
			PARSE_id_chain(ids);
			break;

//		case LBRACE:
//		case RBRACE:
//			// epsilon (FOLLOW set)
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in id_chain's FIRST or FOLLOW sets"
//					 );
		}
	}

	private void PARSE_class_contents() {
		switch (m_Token.type()) {
		case ID:
			// ID class_contents_1
			Token typeToken = assertToken(TokenType.ID);
			PARSE_class_contents_1(typeToken);
			break;

		case STATIC:
			// STATIC final_ep class_contents_2_1
			assertToken(TokenType.STATIC);
			boolean isFinal = PARSE_final_ep();
			PARSE_class_contents_2_1(true, isFinal);
			break;

		case FINAL:
			// FINAL class_contents_2_1
			assertToken(TokenType.FINAL);
			PARSE_class_contents_2_1(false, true);
			break;

		case VOID:
		case INT:
		case CHAR:
		case STR:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// class_contents_2
			PARSE_class_contents_2(false, false);
			break;

//		case CLASS:
//			// class_def access_modifier class_contents
//			PARSE_class_def();
//			PARSE_access_modifier();
//			PARSE_class_contents();
//			break;
//
//		case INTERFACE:
//			// interface_def access_modifier class_contents
//			PARSE_interface_def();
//			PARSE_access_modifier();
//			PARSE_class_contents();
//			break;
//
//		case ENUM:
//			// enum_def access_modifier class_contents
//			PARSE_enum_def();
//			PARSE_access_modifier();
//			PARSE_class_contents();
//			break;
//
//		case RBRACE:
//			// epsilon (FOLLOW set)
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in class_contents's FIRST or FOLLOW sets"
//					 );
		}
	}

	private void PARSE_class_contents_1(Token typeToken) {
		switch (m_Token.type()) {
		case ID:
			// ID class_contents_3
			Token idToken = assertToken(TokenType.ID);
			PARSE_class_contents_3(false, false, typeToken, idToken);
			break;

		case LPAREN:
			// class_method_def access_modifier class_contents
			PARSE_class_method_def(false, false, typeToken, null);
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in class_contents_1's FIRST set");
		}
	}

	private boolean PARSE_final_ep() {
		switch (m_Token.type()) {
		case FINAL:
			// FINAL
			assertToken(TokenType.FINAL);
			return true;

//		case ID:
//		case VOID:
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
//			// epsilon (FOLLOW set)
//			break;

		default:
			// epsilon in default
			return false;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in final_ep's FIRST or FOLLOW sets"
//					 );
		}
	}

	private void PARSE_class_contents_2_1(boolean isStatic, boolean isFinal) {
		switch (m_Token.type()) {
		case ID:
			// ID ID class_contents_3
			Token typeToken = assertToken(TokenType.ID);
			Token idToken = assertToken(TokenType.ID);
			PARSE_class_contents_3(isStatic, isFinal, typeToken, idToken);
			break;

		case VOID:
		case INT:
		case CHAR:
		case STR:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// class_contents_2
			PARSE_class_contents_2(isStatic, isFinal);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in class_contents_2_1's FIRST set");
		}
	}

	private void PARSE_class_contents_2(boolean isStatic, boolean isFinal) {
		switch (m_Token.type()) {
		case VOID:
			// VOID ID class_method_def access_modifier class_contents
			Token typeToken = assertToken(TokenType.VOID);
			Token idToken = assertToken(TokenType.ID);
			PARSE_class_method_def(isStatic, isFinal, typeToken, idToken);
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		case INT:
		case CHAR:
		case STR:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type ID class_contents_3
			Token typeToken_ = PARSE_var_type();
			Token idToken_ = assertToken(TokenType.ID);
			PARSE_class_contents_3(isStatic, isFinal, typeToken_, idToken_);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in class_contents_2's FIRST set");
		}
	}

	private Token PARSE_var_type() {
		switch (m_Token.type()) {
		case INT:
			// INT
			return assertToken(TokenType.INT);

		case CHAR:
			// CHAR
			return assertToken(TokenType.CHAR);

		case STR:
			// STRING
			return assertToken(TokenType.STR);

		case SHORT:
			// SHORT
			return assertToken(TokenType.SHORT);

		case LONG:
			// LONG
			return assertToken(TokenType.LONG);

		case BYTE:
			// BYTE
			return assertToken(TokenType.BYTE);

		case BIT:
			// BIT
			return assertToken(TokenType.BIT);

		case FLOAT:
			// FLOAT
			return assertToken(TokenType.FLOAT);

		case DOUBLE:
			// DOUBLE
			return assertToken(TokenType.DOUBLE);

		case BOOL:
			// BOOL
			return assertToken(TokenType.BOOL);

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in var_type's FIRST set");
			return null;
		}
	}

	private void PARSE_class_contents_3(boolean isStatic, boolean isFinal,
			Token typeToken, Token idToken) {
		switch (m_Token.type()) {
		case EQUAL:
		case COMMA:
		case SEMI:
			// class_var_def access_modifier class_contents
			PARSE_class_var_def(isStatic, isFinal, typeToken, idToken);
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		case LPAREN:
			// class_method_def access_modifier class_contents
			PARSE_class_method_def(isStatic, isFinal, typeToken, idToken);
			PARSE_access_modifier();
			PARSE_class_contents();
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in class_contents_3's FIRST set");
		}
	}

	private void PARSE_class_var_def(boolean isStatic, boolean isFinal,
			Token typeToken, Token idToken) {
		switch (m_Token.type()) {
		case EQUAL:
		case COMMA:
		case SEMI:
			// var_decl_ep var_chain SEMI

			// build field
			FieldDef field = new FieldDef();
			field.accessLevel(m_CurrentAccessLevel);
			field.isStatic(isStatic);
			field.isFinal(isFinal);
			field.type(typeToken.is(TokenType.ID) ? typeToken.strCon()
					: typeToken.type().toString().toLowerCase());
			field.id(idToken.strCon());
			// add to the class
			SymbolTable.fileDef().field(field);

			// this will be added to the beginning of each of the constructors
			PARSE_var_decl_ep();
			PARSE_var_chain(isStatic, isFinal, typeToken, true);
			assertToken(TokenType.SEMI);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in class_var_def's FIRST set");
		}
	}

	private void PARSE_var_decl_ep() {
		switch (m_Token.type()) {
		case EQUAL:
			// EQUAL expr
			assertToken(TokenType.EQUAL);
			PARSE_expr();
			break;

//		case COMMA:
//		case RPAREN:
//		case SEMI:
//			// epsilon (FOLLOW set)
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in var_decl_ep's FIRST or FOLLOW sets"
//					 );
		}
	}

	private void PARSE_var_chain(boolean isStatic, boolean isFinal,
			Token typeToken, boolean isField) {
		switch (m_Token.type()) {
		case COMMA:
			// COMMA ID var_decl_ep var_chain
			assertToken(TokenType.COMMA);
			Token idToken = assertToken(TokenType.ID);

			if (isField) {
				// build field
				FieldDef field = new FieldDef();
				field.accessLevel(m_CurrentAccessLevel);
				field.isStatic(isStatic);
				field.isFinal(isFinal);
				field.type(typeToken.is(TokenType.ID) ? typeToken.strCon()
						: typeToken.type().toString().toLowerCase());
				field.id(idToken.strCon());
				// add to the class/scope
				SymbolTable.fileDef().field(field);
			} else {
				String type = typeToken.is(TokenType.ID) ? typeToken.strCon()
						: typeToken.type().toString().toLowerCase();
				m_CurrentSt.insertEntry(type, idToken.strCon());
			}

			PARSE_var_decl_ep();
			PARSE_var_chain(isStatic, isFinal, typeToken, isField);
			break;

//		case SEMI:
//			// epsilon (FOLLOW set)
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in var_chain's FIRST or FOLLOW sets"
//					 );
		}

	}

	private void PARSE_class_method_def(boolean isStatic, boolean isFinal,
			Token typeToken, Token idToken) {
		switch (m_Token.type()) {
		case LPAREN:
			// LPAREN formals RPAREN stmt

			assertToken(TokenType.LPAREN);
			List<FieldDef> formals = new ArrayList<>();
			PARSE_formals(formals);

			if (idToken == null) {
				// constructor
				ConstructorDef constructor = new ConstructorDef();
				constructor.accessLevel(m_CurrentAccessLevel);
				constructor.id(typeToken.strCon());
				constructor.setFormals(formals);
				// add constructor to class
				SymbolTable.fileDef().constructor(constructor);
				// get scope
				m_CurrentSt = constructor.localScope();
			} else {
				// method
				MethodDef method = new MethodDef();
				method.accessLevel(m_CurrentAccessLevel);
				method.isStatic(isStatic);
				method.isFinal(isFinal);
				method.type(typeToken.is(TokenType.ID) ? typeToken.strCon()
						: typeToken.type().toString().toLowerCase());
				method.id(idToken.strCon());
				method.setFormals(formals);
				// add method to class or interface
				SymbolTable.fileDef().method(method);
				// get scope
				m_CurrentSt = method.localScope();
			}

			assertToken(TokenType.RPAREN);
			PARSE_stmt();

			if (idToken != null)
				System.out.print(idToken.strCon() + " ");
			m_CurrentSt.debugPrint();
			System.out.println('\n');

			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in class_method_def's FIRST set");
		}
	}

	private void PARSE_formals(List<FieldDef> formals) {
		switch (m_Token.type()) {
		case INT:
		case CHAR:
		case STR:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type ID var_decl_ep formals_chain
			Token typeToken = PARSE_var_type();
			Token idToken = assertToken(TokenType.ID);
			PARSE_var_decl_ep();

			FieldDef def = new FieldDef();
			def.type(typeToken.type().toString().toLowerCase());
			def.id(idToken.strCon());
			formals.add(def);

			PARSE_formals_chain(formals);
			break;

		case ID:
			// ID ID var_decl_ep formals_chain
			Token typeToken_ = assertToken(TokenType.ID);
			Token idToken_ = assertToken(TokenType.ID);
			PARSE_var_decl_ep();

			FieldDef def_ = new FieldDef();
			def_.type(typeToken_.strCon());
			def_.id(idToken_.strCon());
			formals.add(def_);

			PARSE_formals_chain(formals);
			break;

//		case RPAREN:
//			// epsilon (FOLLOW set)
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in formals's FIRST or FOLLOW sets"
//					 );
		}
	}

	private void PARSE_formals_chain(List<FieldDef> formals) {
		switch (m_Token.type()) {
		case COMMA:
			// COMMA formals_chain_1
			assertToken(TokenType.COMMA);
			PARSE_formals_chain_1(formals);
			break;

//		case RPAREN:
//			// epsilon (FOLLOW set)
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in formals_chain's FIRST or FOLLOW sets"
//					 );
		}
	}

	private void PARSE_formals_chain_1(List<FieldDef> formals) {
		switch (m_Token.type()) {
		case INT:
		case CHAR:
		case STR:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type ID var_decl_ep formals_chain
			Token typeToken = PARSE_var_type();
			Token idToken = assertToken(TokenType.ID);
			PARSE_var_decl_ep();

			FieldDef def = new FieldDef();
			def.type(typeToken.type().toString().toLowerCase());
			def.id(idToken.strCon());
			formals.add(def);

			PARSE_formals_chain(formals);
			break;

		case ID:
			// ID ID var_decl_ep formals_chain
			Token typeToken_ = assertToken(TokenType.ID);
			Token idToken_ = assertToken(TokenType.ID);
			PARSE_var_decl_ep();

			FieldDef def_ = new FieldDef();
			def_.type(typeToken_.strCon());
			def_.id(idToken_.strCon());
			formals.add(def_);

			PARSE_formals_chain(formals);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in formal_chain_1's FIRST set");
		}
	}

	private void PARSE_stmt() {
		switch (m_Token.type()) {
		case ID:
			// ID stmt_1 SEMI
			Token idToken = assertToken(TokenType.ID);
			PARSE_stmt_1(idToken);
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
		case STR:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type var_def SEMI
			Token typeToken = PARSE_var_type();
			PARSE_var_def(typeToken);
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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in stmt's FIRST set");
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
		case STR:
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

//		case RBRACE:
//			// epsilon
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in stmts's FIRST or FOLLOW sets"
//					 );
		}
	}

	private void PARSE_stmt_1(Token idToken) {
		switch (m_Token.type()) {
		case LPAREN:
		case PERIOD:
			// method_call
			PARSE_method_call(idToken, SymbolTable.fileDef().id(), true);
			break;

		case EQUAL:
			// assg
			PARSE_assg(idToken);
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
			PARSE_rel_assg(idToken);
			break;

		case ID:
			// var_def
			PARSE_var_def(idToken);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in stmt_1's FIRST or FOLLOW sets");
		}
	}

	private String PARSE_method_call(Token idToken, String type,
			boolean canUseStack) {
		switch (m_Token.type()) {
		case LPAREN:
		case PERIOD:
			// dot_id_chain LPAREN exprs RPAREN dot_method_call_ep
			List<String> ids = new ArrayList<>();
			ids.add(idToken.strCon());
			PARSE_dot_id_chain(ids);

			// assert the type is not a primitive
			ErrorHandler.assertFalse(PrimitiveType.contains(type.toUpperCase()),
					"The type " + type
							+ " is primitive and does not allow method calls (please use its wrapper)");

			MethodDef method = null;

			boolean classExists = SymbolTable.fileSt().hasClass(type);
			boolean interfaceExists = SymbolTable.fileSt().hasInterface(type);
			boolean isLastId = ids.size() == 1;

			if (isLastId && classExists) {
				ClassDef def = SymbolTable.fileSt().getClass(type);
				method = def.method(idToken.strCon()); // TODO overloading
			} else if (isLastId && interfaceExists) {
				InterfaceDef indef = SymbolTable.fileSt().getInterface(type);
				method = indef.method(idToken.strCon()); // TODO overloading
			} else if (canUseStack && m_CurrentSt.hasEntry(idToken.strCon()))
				type = m_CurrentSt.getEntry(idToken.strCon()).type();
			else if (classExists) {
				ClassDef def = SymbolTable.fileSt().getClass(type);

				ErrorHandler.assertTrue(def.hasField(idToken.strCon()),
						"The id " + idToken.strCon()
								+ " has not been defined as a field on the type "
								+ type);

				type = def.field(idToken.strCon()).type();
			} else {
				ErrorHandler.assertTrue(canUseStack,
						"The type " + type + " has not been defined");
				ErrorHandler.assertFalse(canUseStack,
						"The id " + idToken.strCon() + " is not on the stack");
			}

			// assert the type is not a primitive
			ErrorHandler.assertFalse(PrimitiveType.contains(type.toUpperCase()),
					"The type " + type
							+ " is primitive and does not allow method calls (please use its wrapper)");
			// assert a method was found or the type exists
			ErrorHandler.assertTrue(
					method != null || SymbolTable.fileSt().hasClass(type)
							|| SymbolTable.fileSt().hasInterface(type),
					"The type " + type + " does not exist, gotten from id "
							+ idToken.strCon());

			for (int i = 1; i < ids.size(); i++) {
				String id = ids.get(i);
				classExists = SymbolTable.fileSt().hasClass(type);
				interfaceExists = SymbolTable.fileSt().hasInterface(type);
				isLastId = i == ids.size() - 1;

				if (classExists && isLastId) {
					ClassDef def = SymbolTable.fileSt().getClass(type);
					method = def.method(id); // TODO overloading
				} else if (classExists) {
					ClassDef def = SymbolTable.fileSt().getClass(type);
					ErrorHandler.assertTrue(def.hasField(id), "The id " + id
							+ " has not been defined as a field on " + type);
					FieldDef field = def.field(id);
					type = field.type();
				} else if (interfaceExists && isLastId) {
					InterfaceDef indef = SymbolTable.fileSt()
							.getInterface(type);
					method = indef.method(id); // TODO overloading
				} else
					ErrorHandler.error(
							"The type " + type + " has not been defined");
			}

			ErrorHandler.assertTrue(method != null,
					"The method " + ids.get(ids.size() - 1)
							+ " could not be found on type " + type);

			assertToken(TokenType.LPAREN);
			List<String> exprTypes = new ArrayList<>();
			PARSE_exprs(exprTypes);

			// validate formals and exprs match
			ErrorHandler.assertTrue(exprTypes.size() == method.formals().size(),
					"The method call does not have the correct number of parameters\nGiven: "
							+ exprTypes.size() + ", required: "
							+ method.formals().size());
			for (int i = 0; i < exprTypes.size(); i++) {
				ErrorHandler.assertTrue(
						exprTypes.get(i).equals(method.formals().get(i).type()),
						"The " + (i + 1)
								+ " parameter is not the right type\nGiven: "
								+ exprTypes.get(i) + ", required: "
								+ method.formals().get(i).type());
			}

			assertToken(TokenType.RPAREN);

			return PARSE_dot_method_call_ep(method.type());

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in method_call's FIRST set");
			return "Default";
		}
	}

	private void PARSE_exprs(List<String> ids) {
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
			String type = PARSE_expr();
			ids.add(type);
			PARSE_expr_chain(ids);
			break;
//
//		case RPAREN:
//			// epsilon
//			break;

		default:
			// epsilon in default
			break;
//  ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in exprs's FIRST or FOLLOW sets"
//					 );
		}
	}

	private void PARSE_expr_chain(List<String> ids) {
		switch (m_Token.type()) {
		case COMMA:
			// COMMA expr expr_chain
			assertToken(TokenType.COMMA);
			PARSE_expr(); // TODO get type and push to ids
			PARSE_expr_chain(ids);
			break;

//		case RPAREN:
//			// epsilon
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in exprs's FIRST or FOLLOW sets"
//					 );
		}
	}

	private String PARSE_dot_method_call_ep(String callReturnType) {
		switch (m_Token.type()) {
		case PERIOD:
			// PERIOD ID method_call
			assertToken(TokenType.PERIOD);
			Token idToken = assertToken(TokenType.ID);
			// TODO validate callReturnType has id as field or method
			return PARSE_method_call(idToken, callReturnType, false);

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
//		case SEMI:
//		case DPERIOD:
//		case RBRACKET:
//		case COMMA:
//		case RPAREN:
//			// epsilon
//			break;

		default:
			// epsilon in default
			return callReturnType;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in dot_method_call_ep's FIRST or FOLLOW sets"
//					 );
		}

	}

	private String PARSE_assg(Token idToken) {
		switch (m_Token.type()) {
		case EQUAL:
			// EQUAL expr
			assertToken(TokenType.EQUAL);
			String type = PARSE_expr();
			// TODO make sure the expr's type can be stored in idToken's type
			// return the type of the id token (can be on stack or this
			// class's field)
			return m_CurrentSt.getStackOrFieldType(idToken.strCon());

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in assg's FIRST set");
			return "Default";
		}
	}

	private String PARSE_rel_assg(Token idToken) {
		switch (m_Token.type()) {
		case DPLUS:
			// DPLUS
			assertToken(TokenType.DPLUS);
			// return the type of the id token (can be on stack or this
			// class's field)
			return m_CurrentSt.getStackOrFieldType(idToken.strCon());

		case DDASH:
			// DDASH
			assertToken(TokenType.DDASH);
			// return the type of the id token (can be on stack or this
			// class's field)
			return m_CurrentSt.getStackOrFieldType(idToken.strCon());

		case PLUSEQ:
		case DASHEQ:
		case STAREQ:
		case FSLASHEQ:
		case AMPERSANDEQ:
		case PIPEEQ:
		case CARETEQ:
		case PERCENTEQ:
			// binopeq expr
			Token binToken = PARSE_binopeq();
			String type = PARSE_expr();
			// return the type of the id token (can be on stack or this
			// class's field)
			return SymbolTable.combineTypes(
					m_CurrentSt.getStackOrFieldType(idToken.strCon()), type);

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in rel_assg's FIRST set");
			return "Default";
		}
	}

	private String PARSE_pre_rel_assg() {
		switch (m_Token.type()) {
		case DPLUS:
			// DPLUS ID
			assertToken(TokenType.DPLUS);
			String id = assertToken(TokenType.ID).strCon();
			// return the type of the id token (can be on stack or this
			// class's field)
			return m_CurrentSt.getStackOrFieldType(id);

		case DDASH:
			// DDASH ID
			assertToken(TokenType.DDASH);
			id = assertToken(TokenType.ID).strCon();
			// return the type of the id token (can be on stack or this
			// class's field)
			return m_CurrentSt.getStackOrFieldType(id);

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in pre_rel_assg's FIRST set");
			return "Default";
		}
	}

	private void PARSE_var_def(Token typeToken) {
		switch (m_Token.type()) {
		case ID:
			// ID var_decl_ep var_chain
			Token idToken = assertToken(TokenType.ID);

			// add to st
			String type = typeToken.is(TokenType.ID) ? typeToken.strCon()
					: typeToken.type().toString().toLowerCase();
			m_CurrentSt.insertEntry(type, idToken.strCon());

			PARSE_var_decl_ep();
			PARSE_var_chain(false, false, typeToken, false);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in var_def's FIRST set");
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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in if_stmt's FIRST set");
		}
	}

	private void PARSE_else_stmt() {
		switch (m_Token.type()) {
		case ELSE:
			// ELSE stmt
			assertToken(TokenType.ELSE);
			PARSE_stmt();
			break;

//		case PRIVATE:
//		case PROTECTED:
//		case PUBLIC:
//		case CLASS:
//		case INTERFACE:
//		case ENUM:
//		case ID:
//		case STATIC:
//		case FINAL:
//		case VOID:
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
//		case SEMI:
//		case DPERIOD:
//		case RBRACKET:
//		case COMMA:
//		case RPAREN:
//		case RBRACE:
//		case IF:
//		case FOR:
//		case RETURN:
//		case DPLUS:
//		case DDASH:
//		case LBRACE:
//		case WHILE:
//		case AT:
//			// epsilon
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in else_stmt's FIRST or FOLLOW sets"
//					 );
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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in while_stmt's FIRST set");
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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in for_stmt's FIRST set");
		}
	}

	private void PARSE_for_middle() {
		switch (m_Token.type()) {
		case INT:
		case CHAR:
		case STR:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// var_type ID for_middle_1
			Token typeToken = PARSE_var_type();
			Token idToken = assertToken(TokenType.ID);
			String type = typeToken.is(TokenType.ID) ? typeToken.strCon()
					: typeToken.type().toString().toLowerCase();
			m_CurrentSt.insertEntry(type, idToken.strCon());
			PARSE_for_middle_1(typeToken, idToken);
			break;

		case ID:
			// ID for_middle_2
			typeToken = assertToken(TokenType.ID);
			PARSE_for_middle_2(typeToken);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in for_middle_stmt's FIRST set");
		}
	}

	private void PARSE_for_middle_1(Token typeToken, Token idToken) {
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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in for_middle_1's FIRST set");
		}
	}

	private void PARSE_for_right() {
		switch (m_Token.type()) {
		case ID:
			// ID for_right_1
			Token idToken = assertToken(TokenType.ID);
			PARSE_for_right_1(idToken);
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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in for_right's FIRST set");
		}
	}

	private void PARSE_for_right_1(Token idToken) {
		switch (m_Token.type()) {
		case LPAREN:
		case PERIOD:
			// method_call
			PARSE_method_call(idToken, SymbolTable.fileDef().id(), true);
			break;

//		case RPAREN:
//			// epsilon
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in for_right_1's FIRST set"
//					 );
		}
	}

	private void PARSE_for_middle_2(Token typeToken) {
		switch (m_Token.type()) {
		case ID:
			// ID for_middle_1
			Token idToken = assertToken(TokenType.ID);
			String type = typeToken.is(TokenType.ID) ? typeToken.strCon()
					: typeToken.type().toString().toLowerCase();
			m_CurrentSt.insertEntry(type, idToken.strCon());
			PARSE_for_middle_1(typeToken, idToken);
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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in for_middle_2's FIRST set");
		}
	}

	private void PARSE_for_middle_3() {
		switch (m_Token.type()) {
		case ID:
			// ID for_middle_4
			Token idToken = assertToken(TokenType.ID);
			PARSE_for_middle_4(idToken);
			break;

		case DPLUS:
		case DDASH:
			// pre_rel_assg
			PARSE_pre_rel_assg();
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in for_middle_2's FIRST set");
		}
	}

	private void PARSE_for_middle_4(Token idToken) {
		switch (m_Token.type()) {
		case EQUAL:
			// assg
			PARSE_assg(idToken);
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
			PARSE_rel_assg(idToken);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in for_middle_2's FIRST set");
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
		case NEW:
			// expr
			PARSE_expr();
			break;

//		case SEMI:
//			// epsilon
//			break;

		default:
			// epsilon in default
			break;
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in exprs's FIRST set"
//					 );
		}
	}

	private String PARSE_expr() {
		switch (m_Token.type()) {
		case ID:
			// ID expr_1 expr_lr
			Token idToken = assertToken(TokenType.ID);
			String firstType = PARSE_expr_1(idToken);
			return PARSE_expr_lr(firstType);

		case NEW:
			// NEW ID LPAREN exprs RPAREN dot_method_call_ep expr_lr
			assertToken(TokenType.NEW);
			String id = assertToken(TokenType.ID).strCon();
			assertToken(TokenType.LPAREN);
			List<String> exprTypes = new ArrayList<>();
			PARSE_exprs(exprTypes);
			// TODO ensure formals match exprTypes in a constructor

			assertToken(TokenType.RPAREN);

			firstType = PARSE_dot_method_call_ep(id);
			return PARSE_expr_lr(firstType);

		case DPLUS:
		case DDASH:
			// pre_rel_assg expr_lr
			firstType = PARSE_pre_rel_assg();
			return PARSE_expr_lr(firstType);

		case DASH:
		case TILDE:
		case EXMARK:
			PARSE_unop();
			firstType = PARSE_expr();
			return PARSE_expr_lr(firstType);

		case LBRACKET:
			// LBRACKET expr DPERIOD expr RBRACKET expr_lr
			ErrorHandler.error("LIST CREATION NOT SUPPORTED");
			// TODO support list creation as expression
			assertToken(TokenType.LBRACKET);
			PARSE_expr();
			assertToken(TokenType.DPERIOD);
			PARSE_expr();
			assertToken(TokenType.RBRACKET);
			firstType = "int[]";
			return PARSE_expr_lr(firstType);

		case LPAREN:
			// LPAREN expr_lambda expr_lr
			assertToken(TokenType.LPAREN);
			firstType = PARSE_expr_lambda();
			return PARSE_expr_lr(firstType);

		case INTCON:
		case CHARCON:
		case STRCON:
		case PERIOD:
			// const expr_lr
			firstType = PARSE_const();
			return PARSE_expr_lr(firstType);

		// TODO null expr

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in expr's FIRST set");
			return "Default";
		}
	}

	private String PARSE_expr_lr(String firstType) {
		switch (m_Token.type()) {
		case PLUS:
		case DASH:
		case STAR:
		case FSLASH:
		case AMPERSAND:
		case PIPE:
		case CARET:
		case PERCENT:
		case DLANGLE:
		case DRANGLE:
			// binop expr expr_lr
			Token binToken = PARSE_binop();
			String type = PARSE_expr();
			String combinedType = SymbolTable.combineTypes(firstType, type);
			return PARSE_expr_lr(combinedType);

		case LANGLE:
		case RANGLE:
		case LEQ:
		case GEQ:
		case DEQ:
		case NEQ:
			// relop expr expr_lr
			Token relToken = PARSE_relop();
			type = PARSE_expr();
			return PARSE_expr_lr("bool");

		case DAMPERSAND:
		case DPIPE:
		case DCARET:
			// logop expr expr_lr
			Token logToken = PARSE_logop();
			type = PARSE_expr();
			return PARSE_expr_lr("bool");

//		case DPERIOD:
//		case RBRACKET:
//		case COMMA:
//		case RPAREN:
//		case SEMI:
//			break;

		default:
			// epsilon in default
			return firstType;
// 			ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in expr_lr's FIRST set"
//					 );
		}
	}

	private String PARSE_expr_1(Token idToken) {
		switch (m_Token.type()) {
		case PERIOD:
		case LPAREN:
			// method_call
			return PARSE_method_call(idToken, SymbolTable.fileDef().id(), true);

		case EQUAL:
			// assg
			return PARSE_assg(idToken);

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
		case DLANGLE:
		case DRANGLE:
			// rel_assg
			return PARSE_rel_assg(idToken);

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
//		case DPERIOD:
//		case RBRACKET:
//		case COMMA:
//		case RPAREN:
//		case SEMI:
//			break;

		default:
			// epsilon in default
			// return the type of the id token (can be on stack or this
			// class's field)
			return m_CurrentSt.getStackOrFieldType(idToken.strCon());
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in expr_1's FIRST set"
//					 );
		}
	}

	private String PARSE_expr_lambda() {
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
		case DLANGLE:
		case DRANGLE:
			// expr RPAREN
			String type = PARSE_expr();
			assertToken(TokenType.RPAREN);
			return type;

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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in expr_lambda's FIRST set");
			return "Default";
		}
	}

	private String PARSE_const() {
		switch (m_Token.type()) {
		case INTCON:
			// INTCON deci_ep
			assertToken(TokenType.INTCON);
			return PARSE_deci_ep();
		case PERIOD:
			// PERIOD INTCON
			assertToken(TokenType.PERIOD);
			assertToken(TokenType.INTCON);
			return "float";
		case CHARCON:
			// CHARCON
			assertToken(TokenType.CHARCON);
			return "char";
		case STRCON:
			// STRCON
			assertToken(TokenType.STRCON);
			return "str";
		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in const's FIRST set");
			return "Default";
		}
	}

	private String PARSE_deci_ep() {
		switch (m_Token.type()) {
		case PERIOD:
			// PERIOD INTCON
			assertToken(TokenType.PERIOD);
			assertToken(TokenType.INTCON);
			return "float";

//		case DPERIOD:
//		case RBRACKET:
//		case COMMA:
//		case RPAREN:
//		case SEMI:
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
//			break;

		default:
			// epsilon in default
			return "int";
//			 ErrorHandler.error("The current token (" + m_Token
//					+ ") is not in deci_ep's FIRST or FOLLOW sets"
//					 );
		}
	}

	private Token PARSE_unop() {
		switch (m_Token.type()) {
		case EXMARK:
			// EXMARK
			return assertToken(TokenType.EXMARK);

		case DASH:
			// DASH
			return assertToken(TokenType.DASH);

		case TILDE:
			// TILDE
			return assertToken(TokenType.TILDE);

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in unop's FIRST set");
			return null;
		}
	}

	private Token PARSE_binop() {
		switch (m_Token.type()) {
		case PLUS:
			// PLUS
			return assertToken(TokenType.PLUS);

		case DASH:
			// DASH
			return assertToken(TokenType.DASH);

		case STAR:
			// STAR
			return assertToken(TokenType.STAR);

		case FSLASH:
			// FSLASH
			return assertToken(TokenType.FSLASH);

		case AMPERSAND:
			// AMPERSAND
			return assertToken(TokenType.AMPERSAND);

		case PIPE:
			// PIPE
			return assertToken(TokenType.PIPE);

		case CARET:
			// CARET
			return assertToken(TokenType.CARET);

		case PERCENT:
			// PERCENT
			return assertToken(TokenType.PERCENT);

		case DLANGLE:
			// DLANGLE
			return assertToken(TokenType.DLANGLE);

		case DRANGLE:
			// DRANGLE
			return assertToken(TokenType.DRANGLE);

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in binop's FIRST set");
			return null;
		}
	}

	private Token PARSE_binopeq() {
		switch (m_Token.type()) {
		case PLUSEQ:
			// PLUSEQ
			return assertToken(TokenType.PLUSEQ);

		case DASHEQ:
			// DASHEQ
			return assertToken(TokenType.DASHEQ);

		case STAREQ:
			// STAREQ
			return assertToken(TokenType.STAREQ);

		case FSLASHEQ:
			// FSLASHEQ
			return assertToken(TokenType.FSLASHEQ);

		case AMPERSANDEQ:
			// AMPERSANDEQ
			return assertToken(TokenType.AMPERSANDEQ);

		case PIPEEQ:
			// PIPEEQ
			return assertToken(TokenType.PIPEEQ);

		case CARETEQ:
			// CARETEQ
			return assertToken(TokenType.CARETEQ);

		case PERCENTEQ:
			// PERCENTEQ
			return assertToken(TokenType.PERCENTEQ);

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in binopeq's FIRST set");
			return null;
		}
	}

	private Token PARSE_relop() {
		switch (m_Token.type()) {
		case LANGLE:
			// LANGLE
			return assertToken(TokenType.LANGLE);

		case RANGLE:
			// RANGLE
			return assertToken(TokenType.RANGLE);

		case LEQ:
			// LEQ
			return assertToken(TokenType.LEQ);

		case GEQ:
			// GEQ
			return assertToken(TokenType.GEQ);

		case DEQ:
			// DEQ
			return assertToken(TokenType.DEQ);

		case NEQ:
			// NEQ
			return assertToken(TokenType.NEQ);

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in relop's FIRST set");
			return null;
		}
	}

	private Token PARSE_logop() {
		switch (m_Token.type()) {
		case DAMPERSAND:
			// DAMPERSAND
			return assertToken(TokenType.DAMPERSAND);

		case DPIPE:
			// DPIPE
			return assertToken(TokenType.DPIPE);

		case DCARET:
			// DCARET
			return assertToken(TokenType.DCARET);

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in logop's FIRST set");
			return null;
		}
	}

	private void PARSE_interface_def() {
		switch (m_Token.type()) {
		case INTERFACE:
			// INTERFACE ID ext_impl_ep LBRACE interface_contents RBRACE
			assertToken(TokenType.INTERFACE);
			Token idToken = assertToken(TokenType.ID);
			List<String> parents = new ArrayList<>();
			PARSE_ext_impl_ep(parents);

			// semantics
			SymbolTable.set(SymbolTableType.INTERFACE);
			InterfaceDef def = (InterfaceDef) SymbolTable.fileDef();
			def.id(idToken.strCon());
			def.library("__MAIN__"); // TODO allow custom libraries

			assertToken(TokenType.LBRACE);
			PARSE_interface_contents();
			assertToken(TokenType.RBRACE);

			// ast

			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in interface_def's FIRST set");
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
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in interface_def's FIRST set");
		}
	}

	private void PARSE_interface_method() {
		switch (m_Token.type()) {
		case AT:
			// AT DEFINE access_modifier ret_type ID class_method_def
			assertToken(TokenType.AT);
			assertToken(TokenType.DEFINE);
			PARSE_access_modifier();
			Token typeToken = PARSE_ret_type();
			Token idToken = assertToken(TokenType.ID);
			PARSE_class_method_def(false, false, typeToken, idToken);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in interface_def's FIRST set");
		}
	}

	private Token PARSE_ret_type() {
		switch (m_Token.type()) {
		case INT:
		case CHAR:
		case STR:
		case SHORT:
		case LONG:
		case BYTE:
		case BIT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
			// vartype
			return PARSE_var_type();

		case VOID:
			// VOID
			return assertToken(TokenType.VOID);

		case ID:
			// ID
			return assertToken(TokenType.ID);

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in var_type's FIRST set");
			return null;
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
			List<FieldDef> formals = new ArrayList<>();
			PARSE_formals(formals);
			assertToken(TokenType.RPAREN);
			assertToken(TokenType.SEMI);
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in interface_def's FIRST set");
		}
	}

	private void PARSE_enum_def() {
		switch (m_Token.type()) {
		case ENUM:
			// ENUM ID LBRACE id_ep id_chain RBRACE
			assertToken(TokenType.ENUM);
			Token idToken = assertToken(TokenType.ID);
			assertToken(TokenType.LBRACE);
			List<String> values = new ArrayList<>();
			PARSE_id_and_chain_ep(values);
			assertToken(TokenType.RBRACE);

			// semantics
			SymbolTable.set(SymbolTableType.ENUM);
			EnumDef def = (EnumDef) SymbolTable.fileDef();
			def.id(idToken.strCon());
			def.library("__MAIN__"); // TODO allow custom libraries

			// ast

			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in enum_def's FIRST set");
		}
	}

	private void PARSE_id_and_chain_ep(List<String> ids) {
		switch (m_Token.type()) {
		case ID:
			// ID
			ids.add(assertToken(TokenType.ID).strCon());
			PARSE_id_chain(ids);
			break;

		case RBRACE:
			break;

		default:
			ErrorHandler.error("The current token (" + m_Token
					+ ") is not in enum_def's FIRST set");
		}
	}

	private Token assertToken(TokenType type) {
		ErrorHandler.assertTrue(m_Token.is(type), "The current token ("
				+ m_Token + ") was expected to be (" + type + ")");
		Token out = m_Token;
		next();
		return out;
	}

	private Token next() {
		m_Token = m_Scanner.next();
		return m_Token;
	}

}
