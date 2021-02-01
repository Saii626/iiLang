//> Scanning scanner-class
package app.saikat.iiLang.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.saikat.iiLang.interfaces.CmdlineOptions;
import app.saikat.iiLang.interfaces.ErrorReporter;

import static app.saikat.iiLang.parser.TokenType.*;

public class Scanner {
	// keyword-map
	private static final Map<String, TokenType> keywords;

	// Populate keywords
	static {
		keywords = new HashMap<>();

		// Class
		keywords.put("class", CLASS);
		keywords.put("super", SUPER);
		keywords.put("this", THIS);

		// Function
		keywords.put("fn", FUN);
		keywords.put("return", RETURN);

		// Datatypes
		keywords.put("i8", INT_8_K);
		keywords.put("i16", INT_16_K);
		keywords.put("i32", INT_32_K);
		keywords.put("i64", INT_64_K);
		keywords.put("f32", FLOAT_32_K);
		keywords.put("f64", FLOAT_64_K);
		keywords.put("bool", BOOL_K);
		keywords.put("string", STRING_K);

		// Control flow
		keywords.put("if", IF);
		keywords.put("else", ELSE);
		keywords.put("while", WHILE);

		// Special values
		keywords.put("false", FALSE);
		keywords.put("true", TRUE);
		keywords.put("nil", NIL);

		// Relational operators
		keywords.put("or", OR);
		keywords.put("and", AND);

		// Print
		keywords.put("print", PRINT);
	}

	// Source
	private final String source;
	private final ErrorReporter errorReporter;

	// Final output
	private final List<Token> tokens = new ArrayList<>();

	// scan-state
	private int start = 0;
	private int current = 0;
	private int line = 1;

	public Scanner(String source, ErrorReporter errorReporter) {
		this.source = source;
		this.errorReporter = errorReporter;
	}

	public List<Token> scanTokens() {
		while (!isAtEnd()) {
			// We are at the beginning of the next lexeme.
			start = current;
			scanToken();
		}

		tokens.add(new Token(EOF, "", null, line));

		if (CmdlineOptions.selectedOptions.contains(CmdlineOptions.DUMP_TOKENS)) {
			System.out.println("After scanning:");
			StringBuilder builder = new StringBuilder();
			for (Token token : tokens) {
				builder.append(token.toString());
			}
			System.out.println(builder.toString());
		}
		return tokens;
	}

	// Entrypoint for scanning
	private void scanToken() {
		char c = advance();
		switch (c) {
			case '(': addToken(LEFT_PAREN); break;
			case ')': addToken(RIGHT_PAREN); break;
			case '{': addToken(LEFT_BRACE); break;
			case '}': addToken(RIGHT_BRACE); break;
			case ',': addToken(COMMA); break;
			case '.': addToken(DOT); break;
			case '-':
				addToken(match('>') ? LAMBDA : MINUS);
				break;
			case '+': addToken(PLUS); break;
			case ';': addToken(SEMICOLON); break;
			case ':': addToken(COLON); break;
			case '*': addToken(STAR); break; // [slash]
			case '!':
				addToken(match('=') ? BANG_EQUAL : BANG);
				break;
			case '=':
				addToken(match('=') ? EQUAL_EQUAL : EQUAL);
				break;
			case '<':
				addToken(match('=') ? LESS_EQUAL : LESS);
				break;
			case '>':
				addToken(match('=') ? GREATER_EQUAL : GREATER);
				break;
			case '/':
				if (match('/')) {
					// A comment goes until the end of the line.
					while (peek() != '\n' && !isAtEnd()) advance();
				} else {
					addToken(SLASH);
				}
				break;
			case ' ':
			case '\r':
			case '\t':
				break;
			case '\n':
				line++;
				break;
			case '"': string(); break;
			default:
				if (isDigit(c)) {
					number();
				} else if (isAlpha(c)) {
					identifier();
				} else {
					errorReporter.reportError("Unexpected character " + c, this.line, this.current);
				}
				break;
		}
	}

	// Match an identifier
	private void identifier() {
		while (isAlphaNumeric(peek())) advance();

		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if (type == null) type = IDENTIFIER;
		addToken(type);
	}
	
	// Match an number. Assume the biggest possible datatype here
	private void number() {
		while (isDigit(peek())) advance();

		// Look for a fractional part.
		if (peek() == '.' && isDigit(peekNext())) {
			// Consume the "."
			advance();

			while (isDigit(peek())) advance();
			addToken(FLOAT_64, Double.parseDouble(source.substring(start, current)));
		} else {
			addToken(INT_64, Integer.parseInt(source.substring(start, current)));
		}
	}

	// Match an sring
	private void string() {
		while (peek() != '"' && !isAtEnd()) {
			if (peek() == '\n') line++;
			advance();
		}

		if (isAtEnd()) {
			errorReporter.reportError("Unterminated string", line, current);
			return;
		}

		// The closing ".
		advance();

		// Trim the surrounding quotes.
		String value = source.substring(start + 1, current - 1);
		addToken(STRING, value);
	}

	// Helper functions
	
	private boolean match(char expected) {
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;

		current++;
		return true;
	}

	private char peek() {
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}

	private char peekNext() {
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current + 1);
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
					 (c >= 'A' && c <= 'Z') ||
						c == '_';
	}

	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}

	private char advance() {
		current++;
		return source.charAt(current - 1);
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}
}
