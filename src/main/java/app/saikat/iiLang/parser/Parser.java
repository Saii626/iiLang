//> Parsing Expressions parser
package app.saikat.iiLang.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.saikat.iiLang.ast.datatypes.ClassType;
import app.saikat.iiLang.ast.datatypes.FunctionType;
import app.saikat.iiLang.ast.datatypes.Primitive;
import app.saikat.iiLang.ast.expression.*;
import app.saikat.iiLang.ast.interfaces.*;
import app.saikat.iiLang.ast.statement.*;
import app.saikat.iiLang.ast.visitors.AstPrinter;
import app.saikat.iiLang.interfaces.CmdlineOptions;
import app.saikat.iiLang.interfaces.ErrorReporter;

import static app.saikat.iiLang.parser.TokenType.*;

public class Parser {

	// Input to parser
	private final List<Token> tokens;
	private final ErrorReporter errorReporter;

	// Keep track of which token we are processing
	private int current = 0;

	// This is "class name" to ClassType map. Since each class is a separate type,
	// this map keeps track of all the classes declared sofar. Not scoped (global)
	private Map<String, ClassType> classMap = new HashMap<>();

	// Stack of classes being constructed
	private Deque<ClassType> classScope = new ArrayDeque<>();

	// Stack of functions being constructed
	private Deque<FunctionType> functionScope = new ArrayDeque<>();

	private static class BlockScope {
		private final boolean hasReturn;
		private final Type returnType;
		private final Map<String, Variable> declaredVariables;

		BlockScope(boolean hasReturn, Type returnType) {
			this.hasReturn = hasReturn;
			this.returnType = returnType;
			this.declaredVariables = new HashMap<>();
		}
	}

	// Stack of blocks being constructed
	private Deque<BlockScope> blockScope = new ArrayDeque<>();

	public Parser(List<Token> tokens, ErrorReporter reporter) {
		this.tokens = tokens;
		this.errorReporter = reporter;

		// Push global block scope onto stack
		blockScope.push(new BlockScope(false, null));
	}

	// Statements and State parse
	public List<Stmt> parse() {
		List<Stmt> statements = new ArrayList<>();
		while (!isAtEnd()) {
			statements.add(declaration());
		}

		if (CmdlineOptions.selectedOptions.contains(CmdlineOptions.DUMP_AST)) {
			AstPrinter astPrinter = new AstPrinter();
			for (Stmt stmt : statements) {
				System.out.println("After parsing:");
				System.out.println(stmt.accept(astPrinter));
			}
		}
		return statements;
	}

	// Statements and State parse expression
	private Expr expression(Type expectedType) {
		return assignment(expectedType);
	}

	private Stmt declaration() {
		try {
			if (match(CLASS)) return classDeclaration();
			if (match(DATA_TYPE)) return varDeclaration();

			return statement();
		} catch (Exception error) {
			synchronize();
			return null;
		}
	}

	private Stmt classDeclaration() {
		Token name = consume(IDENTIFIER, "Expect class name.");

		ClassType superClassType = null;
		Klass superclass = null;
		if (match(COLON)) {
			Token superClassToken = consume(IDENTIFIER, "Expect superclass name.");
			superClassType = classMap.get(superClassToken.getLexeme());
			if (superClassType == null) {
				errorReporter.reportError("No such superclass found", superClassToken);
				return null;
			}
			if (superClassType.getKlass() == null) {
				errorReporter.reportError("Cannot use class as superclass inside its own declaration", superClassToken);
				return null;
			}
		}

		ClassType classType = new ClassType(name.getLexeme(), null);
		classMap.put(name.getLexeme(), classType);

		// Enter a new class scope and block scope
		classScope.push(classType);
		blockScope.push(new BlockScope(false, null));

		consume(LEFT_BRACE, "Expect '{' before class body.");

		List<Var> variables = new ArrayList<>();
		while (!check(RIGHT_BRACE) && !isAtEnd()) {
			match(DATA_TYPE);
			variables.add(varDeclaration());
		}

		consume(RIGHT_BRACE, "Expect '}' after class body.");

		// End the nested class scope and block scope
		classScope.pop();
		blockScope.pop();

		Klass newClass = new Klass(name, superclass, variables);
		classMap.put(name.getLexeme(), newClass.getType());
		return newClass;
	}

	private Stmt statement() {
		if (match(IF)) return ifStatement();
		if (match(PRINT)) return printStatement();
		if (match(RETURN)) return returnStatement();
		if (match(WHILE)) return whileStatement();
		if (match(LEFT_BRACE)) return new Block(block());

		errorReporter.reportError("Not a statement", previous());
		return null;
		//return expressionStatement();
	}

	//private Stmt forStatement() {
	//	consume(LEFT_PAREN, "Expect '(' after 'for'.");

	//	Stmt initializer;
	//	if (match(SEMICOLON)) {
	//		initializer = null;
	//	} else if (match(STRING_K, INT_8_K, INT_16_K, INT_32_K, INT_64_K, FLOAT_32_K, FLOAT_64_K, BOOL_K)) {
	//		initializer = varDeclaration();
	//	} else {
	//		initializer = expressionStatement();
	//	}

	//	Expr condition = null;
	//	if (!check(SEMICOLON)) {
	//		condition = expression();
	//	}
	//	consume(SEMICOLON, "Expect ';' after loop condition.");

	//	Expr increment = null;
	//	if (!check(RIGHT_PAREN)) {
	//		increment = expression();
	//	}
	//	consume(RIGHT_PAREN, "Expect ')' after for clauses.");
	//	Stmt body = statement();

	//	if (increment != null) {
	//		body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
	//	}

	//	if (condition == null) condition = new Expr.Literal(true);
	//	body = new Stmt.While(condition, body);

	//	if (initializer != null) {
	//		body = new Stmt.Block(Arrays.asList(initializer, body));
	//	}

	//	return body;
	//}

	private If ifStatement() {
		Token ifToken = previous();

		consume(LEFT_PAREN, "Expect '(' after 'if'.");
		Expr condition = expression(Primitive.BOOL);
		consume(RIGHT_PAREN, "Expect ')' after if condition."); // [parens]

		Stmt thenBranch = statement();
		Stmt elseBranch = null;
		if (match(ELSE)) {
			elseBranch = statement();
		}

		if (condition.getResultType() !=  Primitive.BOOL) {
			errorReporter.reportError("Expression doesnot evaluate to boolean", ifToken);
		}
		return new If(condition, thenBranch, elseBranch);
	}

	private Print printStatement() {
		Token printToken = previous();

		Expr value = expression(Primitive.STRING);
		consume(SEMICOLON, "Expect ';' after value.");

		if (value.getResultType() !=  Primitive.STRING) {
			errorReporter.reportError("Expression doesnot evaluate to string", printToken);
		}
		return new Print(value);
	}

	private Return returnStatement() {
		Token keyword = previous();
		if (functionScope.peek() == null) {
			errorReporter.reportError("Can only return from function", keyword);
		}


		Expr value = null;
		if (!check(SEMICOLON)) {
			value = expression(functionScope.peek().getReturnType());
		}

		consume(SEMICOLON, "Expect ';' after return value.");

		if (value.getResultType() != functionScope.peek().getReturnType()) {
			errorReporter.reportError("Trying to return " + value.getResultType().typeName() + " from function which returns "
					+ functionScope.peek().getReturnType().typeName(), keyword);
		}
		return new Return(keyword, value);
	}

	private Var varDeclaration() {
		Type varType = parseVarType(previous());
		if (varType == null) {
			errorReporter.reportError("No such type found", previous());
		}
		Token name = consume(IDENTIFIER, "Expect variable name.");

		Variable variable = new Variable(name, varType);
		blockScope.peek().declaredVariables.put(name.getLexeme(), variable);

		Expr initializer = null;
		if (match(EQUAL)) {
			initializer = expression(varType);
		}

		consume(SEMICOLON, "Expect ';' after variable declaration.");
		return new Var(variable, initializer);
	}

	private Type parseVarType(Token token) {
		switch (token.type) {
			case INT_8_K:
				return Primitive.INT_8;
			case INT_16_K:
				return Primitive.INT_16;
			case INT_32_K:
				return Primitive.INT_32;
			case INT_64_K:
				return Primitive.INT_64;
			case FLOAT_32_K:
				return Primitive.FLOAT_32;
			case FLOAT_64_K:
				return Primitive.FLOAT_64;
			case BOOL_K:
				return Primitive.BOOL;
			case STRING_K:
				return Primitive.STRING;
			case FUN:
				return parseFunctionType();
			case IDENTIFIER:
				return classMap.get(token.getLexeme());
				//if (classType == null) {
				//	errorReporter.reportError("Classtype not found", token);
				//}

				//if (classType.getKlass() == null) {
				//	errorReporter.reportError("Cannot use class inside itself", token);
				//}
				//return classType;
			default:
				return null;
				//errorReporter.reportError("Expected datatype", token);
		}
	}

	private FunctionType parseFunctionType() {
		List<Type> paramTypes = new ArrayList<>();
		Type returnType = Primitive.VOID;
		ClassType declaringClass = classScope.peek();
		FunctionType declaringFunction = functionScope.peek();
		if (declaringClass != null && declaringFunction == null) {
			paramTypes.add(declaringClass);
		}

		if (match(LEFT_PAREN)) {
			while (true) {
				if (match(DATA_TYPE)) {
					paramTypes.add(parseVarType(previous()));
					if (!match(COMMA, COLON)) {
						errorReporter.reportError("Expected comma separated params and colon separated return type", advance());
					}
				}

				if (previous().getType() == COLON) {
					returnType = parseVarType(advance());
					consume(RIGHT_PAREN, "Expect ')' after return type");
					break;
				}
			}
		}
		return new FunctionType(paramTypes, returnType);
	}

	private While whileStatement() {
		Token whileToken = previous();
		consume(LEFT_PAREN, "Expect '(' after 'while'.");
		Expr condition = expression(Primitive.BOOL);
		consume(RIGHT_PAREN, "Expect ')' after condition.");
		Stmt body = statement();

		if (condition.getResultType() !=  Primitive.BOOL) {
			errorReporter.reportError("Expression doesnot evaluate to boolean", whileToken);
		}
		return new While(condition, body);
	}

	//private Expression expressionStatement() {
	//	Expr expr = expression(null);
	//	consume(SEMICOLON, "Expect ';' after expression.");
	//	return new Expression(expr);
	//}

	//private Function function(String kind) {
	//	Token name = consume(IDENTIFIER, "Expect " + kind + " name.");

	//	consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
	//	List<Token> parameters = new ArrayList<>();
	//	if (!check(RIGHT_PAREN)) {
	//		do {
	//			if (parameters.size() >= 255) {
	//				errorReporter.reportError("Can't have more than 255 parameters. ", peek());
	//			}

	//			parameters.add(consume(IDENTIFIER, "Expect parameter name."));
	//		} while (match(COMMA));
	//	}
	//	consume(RIGHT_PAREN, "Expect ')' after parameters.");

	//	consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
	//	List<Stmt> body = block();
	//	return new Function(name, parameters, body);
	//}

	private List<Stmt> block() {
		List<Stmt> statements = new ArrayList<>();

		blockScope.push(new BlockScope(false, null));
		while (!check(RIGHT_BRACE) && !isAtEnd()) {
			statements.add(declaration());
		}

		consume(RIGHT_BRACE, "Expect '}' after block.");
		blockScope.pop();
		return statements;
	}

	private Expr assignment(Type expectedType) {
		Expr expr = or(expectedType);

		if (match(EQUAL)) {
			Token equals = previous();
			Expr value = assignment(expectedType);

			if (expr instanceof Variable) {
				Token name = ((Variable)expr).getName();
				return new Assign(name, value);
			} else if (expr instanceof Get) {
				Get get = (Get)expr;
				return new Set(get.getObject(), get.getName(), value);
			}

			errorReporter.reportError("Invalid assignment target.", equals);
		}

		return expr;
	}

	private Expr or(Type expectedType) {
		Expr expr = and(expectedType);

		while (match(OR)) {
			Token operator = previous();
			Expr right = and(expectedType);

			if (expr.getResultType() != Primitive.BOOL) {
				errorReporter.reportError("Left expr does not evaluate to boolean", operator);
			}

			if (right.getResultType() != Primitive.BOOL) {
				errorReporter.reportError("Right expr does not evaluate to boolean", operator);
			}
			expr = new Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr and(Type expectedType) {
		Expr expr = equality(expectedType);

		while (match(AND)) {
			Token operator = previous();
			Expr right = equality(expectedType);

			if (expr.getResultType() != Primitive.BOOL) {
				errorReporter.reportError("Left expr does not evaluate to boolean", operator);
			}

			if (right.getResultType() != Primitive.BOOL) {
				errorReporter.reportError("Right expr does not evaluate to boolean", operator);
			}
			expr = new Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr equality(Type expectedType) {
		Expr expr = comparison(expectedType);

		while (match(BANG_EQUAL, EQUAL_EQUAL)) {
			Token operator = previous();
			Expr right = comparison(expectedType);
			expr = new Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr comparison(Type expectedType) {
		Expr expr = term(expectedType);

		while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = previous();
			Expr right = term(expectedType);

			//if (expr.getResultType() instanceof Primitive) {
			//	errorReporter.reportError("Left expr does not evaluate to a data", operator);
			//}

			//if (right.getResultType() != DATA) {
			//	errorReporter.reportError("Right expr does not evaluate to a data", operator);
			//}
			expr = new Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr term(Type expectedType) {
		Expr expr = factor(expectedType);

		while (match(MINUS, PLUS)) {
			Token operator = previous();
			Expr right = factor(expectedType);

			if (operator.getType() == PLUS) {
				if (!Primitive.NUMBERS.contains(expr.getResultType()) && expr.getResultType() != Primitive.STRING) {
					errorReporter.reportError("LHS cannot perform addition on " + expr.getResultType().typeName(), operator);
				}

				if (!Primitive.NUMBERS.contains(expr.getResultType()) && expr.getResultType() != Primitive.STRING) {
					errorReporter.reportError("RHS cannot perform addition on " + right.getResultType().typeName(), operator);
				}
			} else {
				if (Primitive.NUMBERS.contains(expr.getResultType())) {
					errorReporter.reportError("LHS cannot perform substraction on " + expr.getResultType().typeName(), operator);
				}

				if (Primitive.NUMBERS.contains(right.getResultType())) {
					errorReporter.reportError("RHS cannot perform substraction on " + right.getResultType().typeName(), operator);
				}
			}

			expr = new Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr factor(Type expectedType) {
		Expr expr = unary(expectedType);

		while (match(SLASH, STAR)) {
			Token operator = previous();
			Expr right = unary(expectedType);

			if (Primitive.NUMBERS.contains(expr.getResultType())) {
				errorReporter.reportError("LHS cannot perform operation on " + expr.getResultType().typeName(), operator);
			}

			if (Primitive.NUMBERS.contains(right.getResultType())) {
				errorReporter.reportError("RHS cannot perform operation on " + right.getResultType().typeName(), operator);
			}
			expr = new Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr unary(Type expectedType) {
		if (match(BANG, MINUS)) {
			Token operator = previous();
			Expr right = unary(expectedType);
			if (operator.getType() == BANG) {
				if (right.getResultType() != Primitive.BOOL) {
					errorReporter.reportError("Expression does not evaluate to boolean", operator);
				}
			} else {
				if (Primitive.NUMBERS.contains(right.getResultType())) {
					errorReporter.reportError("Expression does not evaluate to number", operator);
				}
			}
			return new Unary(operator, right);
		}

		return call(expectedType);
	}

	private Expr finishCall(Expr callee) {
		List<Expr> arguments = new ArrayList<>();

		if (callee.getResultType() instanceof FunctionType) {
			// Its a member function. 
			FunctionType calleeType = (FunctionType) callee.getResultType();

			if(calleeType.getParamTypes().size() != 0) {
				List<Type> expectedTypes = calleeType.getParamTypes();
				for (int i = 0; i < expectedTypes.size(); i++) {
					Type expectedType = expectedTypes.get(i);

					Expr arg = expression(expectedType);
					if (!expectedType.isAssignableFrom(arg.getResultType())) {
						errorReporter.reportError("Wrong type of argument provided. Expected " + expectedType.typeName(), previous());
					}
					arguments.add(arg);

					if (i < expectedTypes.size() - 1) {
						consume(COMMA, "Expected ',' after argument");
					}
				}
			}

		} else {
			errorReporter.reportError("Not a function type. Cannot call", previous());
		}
		consume(RIGHT_PAREN, "Expect ')' after arguments.");

		return new Call(callee, arguments);
	}

	private Expr call(Type expectedType) {
		Expr expr = primary(expectedType);

		while (true) { // [while-true]
			if (match(LEFT_PAREN)) {
				expr = finishCall(expr);
			} else if (match(DOT)) {
				Token name = consume(IDENTIFIER, "Expect property name after '.'.");

				Type exprType = expr.getResultType();
				if (exprType instanceof ClassType) {
					ClassType classType = (ClassType)exprType;
					Type resultType = classType.getField(name.getLexeme());

					if (resultType != null) {
						expr = new Get(expr, name, resultType);
					} else {
						errorReporter.reportError("No such field found on " + classType.typeName(), name);
					}
				} else {
					errorReporter.reportError("Not a class type", name);
				}
			} else {
				break;
			}
		}

		return expr;
	}

	private Expr primary(Type expectedType) {
		if (match(FALSE)) return new Literal(false, Primitive.BOOL);
		if (match(TRUE)) return new Literal(true, Primitive.BOOL);
		if (match(NIL)) return new Literal(null, Primitive.VOID);

		if (match(STRING, INT_8, INT_16, INT_32, INT_64, FLOAT_32, FLOAT_64)) {
			return new Literal(previous().literal, Primitive.getType(previous().getType()));
		}

		if (match(SUPER)) {
			Token keyword = previous();
			consume(DOT, "Expect '.' after 'super'.");
			Token name = consume(IDENTIFIER, "Expect superclass field name.");

			ClassType scopedClass = classScope.peek();
			if (scopedClass == null) {
				errorReporter.reportError("super can only be used in class", keyword);
			} else {
				Type fieldType = scopedClass.getField(name.getLexeme());
				if (fieldType == null) {
					errorReporter.reportError("No such method field in current class", name);
				}
				return new Super(keyword, name, fieldType);
			}

		}

		if (match(THIS)) {
			ClassType scopedClass = classScope.peek();
			if (scopedClass == null) {
				errorReporter.reportError("this can only be used in class", previous());
			}
			return new This(previous(), scopedClass);
		}

		if (match(IDENTIFIER)) {
			Variable variable = searchVariableScoped(previous());
			if (variable == null) {
				errorReporter.reportError("No such variable found", previous());
			}
			return variable;
		}

		if (match(LEFT_PAREN)) {
			if (expectedType instanceof FunctionType) {
				// Lambda
				FunctionType funcType = (FunctionType) expectedType;
				List<Token> parameters = new ArrayList<>();

				functionScope.push(funcType);
				blockScope.push(new BlockScope(true, funcType.getReturnType()));

				do  {
					parameters.add(consume(IDENTIFIER, "Expected identifier in parameter list"));
				} while (match(COMMA));

				if (parameters.size() != funcType.getParamTypes().size()) {
					errorReporter.reportError("Wrong number of arguments provided", previous());
				} else {
					List<Type> paramsType = funcType.getParamTypes();
					for (int i = 0; i < parameters.size(); i++) {
						Token arg = parameters.get(i);
						blockScope.peek().declaredVariables.put(arg.getLexeme(), new Variable(arg, paramsType.get(i)));
					}
				}

				consume(RIGHT_PAREN, "Expected ')' after specifying arguments");

				consume(LAMBDA, "Expected '->' after lambda parameters");

				Block block;
				if (match(LEFT_BRACE)) {
					block = new Block(block());
				} else {
					Expr expr = expression(funcType.getReturnType());
					block = new Block(List.of(new Return(previous(), expr)));
				}

				return new Lambda(funcType, parameters, block);
			}

			if (match(DATA_TYPE)) {
				// Cast
				Type castType = parseVarType(previous());
				if (castType != null) {
					Expr expression = expression(castType);
					return new Cast(expression, castType);
				}
				// Unconsume the datatype (should be only 1 token)
				current--;
			}

			// Grouping 
			Expr expr = expression(expectedType);
			consume(RIGHT_PAREN, "Expect ')' after expression.");
			return new Grouping(expr);

		}

		errorReporter.reportError("Expect expression.", peek());
		return null;
	}

	private Variable searchVariableScoped(Token identifier) {
		for (BlockScope scope : blockScope) {
			if (scope.declaredVariables.containsKey(identifier.getLexeme())) {
				return scope.declaredVariables.get(identifier.getLexeme());
			}
		}
		return null;
	}

	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}

		return false;
	}

	private boolean match(EnumSet<TokenType> types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}

		return false;
	}

	private Token consume(TokenType type, String message) {
		if (check(type)) return advance();

		errorReporter.reportError(message, peek());
		return null;
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) return false;
		return peek().type == type;
	}

	private Token advance() {
		if (!isAtEnd()) current++;
		return previous();
	}

	private boolean isAtEnd() {
		return peek().type == EOF;
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}

	private void synchronize() {
		advance();

		while (!isAtEnd()) {
			if (previous().type == SEMICOLON) return;

			switch (peek().getType()) {
				case CLASS:
				case FUN:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					return;
				default:
					break;

			}

			advance();
		}
	}
}
