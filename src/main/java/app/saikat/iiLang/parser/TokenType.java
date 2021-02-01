//> Scanning token-type
package app.saikat.iiLang.parser;

import java.util.EnumSet;

public enum TokenType {
	// Single-character tokens.
	LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
	COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, COLON,

	// One or two character tokens.
	BANG, BANG_EQUAL,
	EQUAL, EQUAL_EQUAL,
	GREATER, GREATER_EQUAL,
	LESS, LESS_EQUAL, LAMBDA,

	// Literals.
	IDENTIFIER, 

	// primitive dataypes (actual literal)
	STRING, INT_8, INT_16, INT_32, INT_64,
	FLOAT_32, FLOAT_64, BOOL,

	// dataype keywords (keyword)
	STRING_K, INT_8_K, INT_16_K, INT_32_K, INT_64_K,
	FLOAT_32_K, FLOAT_64_K, BOOL_K,

	// Keywords.
	AND, CLASS, ELSE, FALSE, FUN, IF, NIL, OR,
	PRINT, RETURN, SUPER, THIS, TRUE, WHILE,

	EOF;

	public static EnumSet<TokenType> LOGICAL_OPERATORS = EnumSet.of(BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);

	public static EnumSet<TokenType> DATA = EnumSet.of(STRING, INT_8, INT_16, INT_32, INT_64, FLOAT_32, FLOAT_64, BOOL);

	public static EnumSet<TokenType> DATA_TYPE = EnumSet.of(STRING_K, INT_8_K, INT_16_K, INT_32_K, INT_64_K, FLOAT_32_K, FLOAT_64_K, BOOL_K, FUN, IDENTIFIER);
}
