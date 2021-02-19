package app.saikat.iiLang.interfaces;

import app.saikat.iiLang.parser.Token;

public interface Reporter {

	void reportError(String msg, int lineno, int charno);

	default void reportError(String msg, Token token) {
		reportError(msg, token.getLine(), -1);
	}

	void debugInfo(String tag, String info);
}
