package app.saikat.iiLang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import app.saikat.iiLang.interfaces.CmdlineOptions;
import app.saikat.iiLang.parser.Scanner;
import app.saikat.iiLang.parser.Token;

public class ii {
	static boolean hadError = false;
	static boolean hadRuntimeError = false;

	public static void main(String[] args) throws IOException {

		Options options = new Options();
		for (CmdlineOptions opt : CmdlineOptions.values()) {
			options.addOption(opt.getOption());
		}

		try {
			CommandLineParser commandLineParser = new DefaultParser();
			CommandLine cmdLine = commandLineParser.parse(options, args);

			CmdlineOptions.setCommandLine(cmdLine);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		runFile(path);
	}

	// run-file
	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		compile(new String(bytes, Charset.defaultCharset()));
		// exit-code

		// Indicate an error in the exit code.
		if (hadError) System.exit(65);
		// exit-code
		// Evaluating Expressions check-runtime-error
		if (hadRuntimeError) System.exit(70);
		// Evaluating Expressions check-runtime-error
	}
//< run-file
//> prompt
	//private static void runPrompt() throws IOException {
	//	InputStreamReader input = new InputStreamReader(System.in);
	//	BufferedReader reader = new BufferedReader(input);

	//	for (;;) { // [repl]
	//		System.out.print("> ");
	//		String line = reader.readLine();
	//		if (line == null) break;
	//		run(line);
//> rese//t-had-error
	//		hadError = false;
//< rese//t-had-error
	//	}
	//}
//< prompt
//> run
	private static void compile(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
/* Scanning run < Parsing Expressions print-ast

		// For now, just print the tokens.
		for (Token token : tokens) {
			System.out.println(token);
		}
*/
//> Parsing Expressions print-ast
		Parser parser = new Parser(tokens);
/* Parsing Expressions print-ast < Statements and State parse-statements
		Expr expression = parser.parse();
*/
//> Statements and State parse-statements
		List<Stmt> statements = parser.parse();
//< Statements and State parse-statements

		// Stop if there was a syntax error.
		if (hadError) return;

//< Parsing Expressions print-ast
//> Resolving and Binding create-resolver
		Resolver resolver = new Resolver(interpreter);
		resolver.resolve(statements);
//> resolution-error

		// Stop if there was a resolution error.
		if (hadError) return;
//< resolution-error

//< Resolving and Binding create-resolver
/* Parsing Expressions print-ast < Evaluating Expressions interpreter-interpret
		System.out.println(new AstPrinter().print(expression));
*/
/* Evaluating Expressions interpreter-interpret < Statements and State interpret-statements
		interpreter.interpret(expression);
*/
//> Statements and State interpret-statements
		interpreter.interpret(statements);
//< Statements and State interpret-statements
	}
//< run
//> lox-error
	static void error(int line, String message) {
		report(line, "", message);
	}

	private static void report(int line, String where,
														 String message) {
		System.err.println(
				"[line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}
//< lox-error
//> Parsing Expressions token-error
	static void error(Token token, String message) {
		if (token.type == TokenType.EOF) {
			report(token.line, " at end", message);
		} else {
			report(token.line, " at '" + token.lexeme + "'", message);
		}
	}
//< Parsing Expressions token-error
//> Evaluating Expressions runtime-error-method
	static void runtimeError(RuntimeError error) {
		System.err.println(error.getMessage() +
				"\n[line " + error.token.line + "]");
		hadRuntimeError = true;
	}
//< Evaluating Expressions runtime-error-method
}
