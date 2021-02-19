//> Scanning token-type
package app.saikat.iiLang.parser.interfaces;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TokenType {
	// Single-character tokens.
	LEFT_PAREN("("), RIGHT_PAREN(")"), LEFT_BRACE("{"), RIGHT_BRACE("}"),
	LEFT_SQ_BRACE("["), RIGHT_SQ_BRACE("]"), COMMA(","), DOT("."), MINUS("-"),
	PLUS("+"), SEMICOLON(";"), SLASH("/"),	STAR("*"), COLON(":"),

	// One or two character tokens.
	BANG("!"), BANG_EQUAL("!="), EQUAL("="), EQUAL_EQUAL("=="),
	GREATER(">"), GREATER_EQUAL(">="), LESS("<"), LESS_EQUAL("<="), LAMBDA("->"),

	// Literals.
	IDENTIFIER(null),

	// primitive data types (actual literal)
	STRING(null), INT(null), LONG(null), FLOAT(null), DOUBLE(null), BOOL(null),

	// data type keywords (keyword)
	STRING_K("string"), INT_K("int"), LONG_K("long"), FLOAT_K("float"), DOUBLE_K("double"),
	BOOL_K("bool"), CLASS_K("class"), FUN_K("fn"), TYPE_K("type"),

	// Keywords.
	AND("and"), ELSE("else"), FALSE("false"), IF("if"), NIL("nil"), OR("or"),
	PRINT("print"), RETURN("return"), SUPER("super"), THIS("this"), TRUE("true"),
	WHILE("while"),

	// Modifiers
	STATIC("static"), NON_FINAL("nonfinal"),

	EOF(null);

	private final String string;

	TokenType(String str) {
		this.string = str;
	}

	public String getString() {
		return string;
	}

	public static final EnumSet<TokenType> LOGICAL_OPERATORS = EnumSet.of(BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);

	public static final EnumSet<TokenType> BINARY_OPERATORS = EnumSet.of(BANG_EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, MINUS, PLUS, SLASH, STAR);

	public static final EnumSet<TokenType> UNARY_OPERATORS = EnumSet.of(MINUS, BANG);

	public static final EnumSet<TokenType> SYSTEM_DEFINED_DATA = EnumSet.of(STRING, INT, LONG, FLOAT, DOUBLE, BOOL);

	public static final EnumSet<TokenType> SYSTEM_DEFINED_DATA_TYPE = EnumSet.of(STRING_K, INT_K, LONG_K, FLOAT_K, DOUBLE_K, BOOL_K, FUN_K, CLASS_K, TYPE_K);

	public static final EnumSet<TokenType> KEYWORDS = EnumSet.of(AND, ELSE, FALSE, IF, NIL, OR, PRINT, RETURN, SUPER, THIS, TRUE, WHILE);

	public static final EnumSet<TokenType> MODIFIERS = EnumSet.of(STATIC, NON_FINAL);

	private static final Map<String, TokenType> keywordsAndDataType = new HashMap<>();

	static {
		EnumSet<TokenType> set = SYSTEM_DEFINED_DATA_TYPE.clone();
		set.addAll(KEYWORDS);
		set.addAll(MODIFIERS);

		for (TokenType type : set) {
			assert (type.getString() != null);
			keywordsAndDataType.put(type.getString(), type);
		}
	}

	public static TokenType parseKeywordOrDataType(String str) {
		return keywordsAndDataType.get(str);
	}
}
