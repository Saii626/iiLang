package app.saikat.iiLang.interfaces;

import app.saikat.iiLang.parser.Token;

public interface ErrorReporter {

	void reportError(String msg, int lineno, int charno);

	default void reportError(String msg, Token token) {
		reportError(msg, token.getLine(), -1);
	}
}
