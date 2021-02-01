//> Scanning token-class
package app.saikat.iiLang.parser;

public class Token {
	final TokenType type;
	final String lexeme;
	final Object literal;
	final int line; // [location]

	Token(TokenType type, String lexeme, Object literal, int line) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line;
	}

	public String toString() {
		return "( " + type + " " + lexeme + " " + literal + " )";
	}

	public TokenType getType() {
		return type;
	}

	public String getLexeme() {
		return lexeme;
	}

	public Object getLiteral() {
		return literal;
	}

	public int getLine() {
		return line;
	}

}
