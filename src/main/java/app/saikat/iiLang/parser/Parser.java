//> Parsing Expressions parser
package app.saikat.iiLang.parser;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.saikat.iiLang.datatypes.ClassType;
import app.saikat.iiLang.datatypes.LambdaType;
import app.saikat.iiLang.ast.expression.*;
import app.saikat.iiLang.ast.interfaces.*;
import app.saikat.iiLang.ast.statement.*;
import app.saikat.iiLang.ast.visitors.AstPrinter;
import app.saikat.iiLang.datatypes.SystemDefinedTypes.Primitive;
import app.saikat.iiLang.parser.interfaces.Scope;
import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.interfaces.CmdlineOptions;
import app.saikat.iiLang.interfaces.Reporter;
import app.saikat.iiLang.parser.interfaces.TokenType;

import static app.saikat.iiLang.datatypes.SystemDefinedTypes.Primitive.*;
import static app.saikat.iiLang.parser.interfaces.TokenType.*;

public class Parser {

    // Input to parser
    private final List<Token> tokens;
    private final Reporter reporter;

    // Keep track of which token we are processing
    private int current = 0;

    // This is "class name" to ClassType map. Since each class is a separate type,
    // this map keeps track of all the classes declared so far. Not scoped (global)
    private final Map<String, ClassType> classMap = new HashMap<>();

    // Scope to keep tack of visibility and lifecycle
    private final Scope scope = new Scope(parent, definedTypes, variables);

    public Parser(List<Token> tokens, Reporter reporter) {
        this.tokens = tokens;
        this.reporter = reporter;

        // Push global block scope onto stack
        scope.addScope();
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

    private static record OutOfOrderResult(List<Variable> variables, List<ClassType> klasses) {};

    private OutOfOrderResult outOfOrderParseDefinitions() {
        List<Variable> declaredVars = new ArrayList<>();
        List<ClassType> declaredKlasses = new ArrayList<>();

        int currentCopy = current;

        while(!match(RIGHT_BRACE, EOF)) { // Stay in the block and in the file
            if (match(CLASS)) {
                declaredKlasses.add(outOfOrderParseKlassDefinitions());
            } else if (match(DATA_TYPE)) {
                declaredVars.add(outOfOrderParseVarDefinitions());
            } else {

                int nestingCount = 0;
                while (!(match(SEMICOLON) && nestingCount == 0) && !isAtEnd()) {
                    Token token = advance();

                    if (token.type() == LEFT_BRACE) nestingCount++;
                    if (token.type() == RIGHT_BRACE) nestingCount--;
                }
            }
        }

        current = currentCopy;

        return new OutOfOrderResult(declaredVars, declaredKlasses);
    }

    private ClassType outOfOrderParseKlassDefinitions() {
        Token name = consume(IDENTIFIER, "Expect class name.");

        ClassType superclass = null;
        if (match(COLON)) {
            Token superClassToken = consume(IDENTIFIER, "Expect superclass name.");
            superclass = classMap.get(superClassToken.lexeme());
            if (superclass == null) {
                reporter.reportError("No such superclass found", superClassToken);
            }
        }

        ClassType newClassType = new ClassType(name.lexeme(), superclass, name.codeLocation());

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            newClassType.addField(outOfOrderParseVarDefinitions());
        }

        consume(RIGHT_BRACE, "Expect '}' after class body.");

        return newClassType;
    }

    private Variable outOfOrderParseVarDefinitions() {
        Type varType = parseVarType(true);
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

        Klass superclass = null;
        if (match(COLON)) {
            Token superClassToken = consume(IDENTIFIER, "Expect superclass name.");
            superclass = classMap.get(superClassToken.getLexeme());
            if (superclass == null) {
                reporter.reportError("No such superclass found", superClassToken);
            }
        }

        Klass newKlass = new Klass(name, superclass, debugInfo);
        newKlass.getType().accept(finishDefinition);

        classMap.put(name.getLexeme(), newKlass);

        // Enter a new class scope
        scope.addScope(newKlass);

        consume(LEFT_BRACE, "Expect '{' before class body.");

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            newKlass.addField(varDeclaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after class body.");

        // End the nested class scope
        scope.popScope();
        return newKlass;
    }

    private Stmt statement() {
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return new Block(block(), debugInfo);

        return expressionStatement();
    }

    private If ifStatement() {
        Token ifToken = previous();

        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression(BOOL_T);
        consume(RIGHT_PAREN, "Expect ')' after if condition."); // [parens]

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        if (condition.getResultType() != BOOL_T) {
            reporter.reportError("Expression doesnot evaluate to boolean", ifToken);
        }

        consume(SEMICOLON, "Expect ';' after if statement.");
        return new If(condition, thenBranch, elseBranch, debugInfo);
    }

    private Print printStatement() {
        Token printToken = previous();

        Expr value = expression(STRING_T);
        consume(SEMICOLON, "Expect ';' after value.");

        if (value.getResultType() != STRING_T) {
            reporter.reportError("Expression doesnot evaluate to string", printToken);
        }
        return new Print(value, debugInfo);
    }

    private Return returnStatement() {
        Token keyword = previous();
        Lambda enclosingLambda = scope.getTopLambda();
        if (enclosingLambda == null) {
            reporter.reportError("Can only return from function", keyword);
            return null;
        }

        Expr value = null;
        if (!check(SEMICOLON)) {
            Type returnType = enclosingLambda.getLambdaType().getReturnType();
            if (returnType == null) {
                // Return type not yet known. Set the return type as type of expression
                value = expression(null);
                enclosingLambda.getLambdaType().setReturnType(value.getResultType());
            } else {
                // Return type of function is known. Verify that it is assignable to that type
                value = expression(returnType);
                if (!returnType.isAssignableFrom(value.getResultType())) {
                    reporter.reportError("Wrong return type. Expected: " + returnType.typeName() + " found " + value.getResultType(), keyword);
                }
            }
        } else {
            Type returnType = enclosingLambda.getLambdaType().getReturnType();
            if (returnType == null) {
                // Set the return type as type of expression
                enclosingLambda.getLambdaType().setReturnType(VOID_T);
            } else {
                if (returnType != VOID_T) {
                    reporter.reportError("Not returning anything when " + returnType.typeName() + " is expected", keyword);
                }
            }
        }

        consume(SEMICOLON, "Expect ';' after return value.");
        return new Return(keyword, value);
    }

    private Var varDeclaration() {
        Type varType = parseVarType(true);
        Token name = consume(IDENTIFIER, "Expect variable name.");
        if (name.getLexeme().equals("init") && varType instanceof LambdaType fnType && scope.isInsideClass()) {
            fnType.getParamTypes().remove(0); // Special function. Defined on the class, not on the instance
            if (fnType.getReturnType() != VOID_T) {
                reporter.reportError("Returning not allowed from init", name);
            }
            fnType.setReturnType(scope.getTopClass().getType());
        }

        varType.accept(finishDefinition);
        Variable variable = new Variable(name.getLexeme(), varType, debugInfo);

        // Add variable to scope
        scope.addVariableToScope(variable);

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression(varType);
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Var(variable, initializer, debugInfo);
    }

    /**
     * Try to parse a type. Does not modify 'current' if it is unable to parse the type
     * BE SURE TO FINISH DEFINING THE RETURNED TYPE
     *
     * @return parsed type
     */
    private Type parseVarType(boolean reportErrorIfFails) {
        int currentCopy = current;
        Token typeToken = previous();
        Type type = switch (typeToken.getType()) {
            case INT_8_K -> INT_8_T;
            case INT_16_K -> INT_16_T;
            case INT_32_K -> INT_32_T;
            case INT_64_K -> INT_64_T;
            case FLOAT_32_K -> FLOAT_32_T;
            case FLOAT_64_K -> FLOAT_64_T;
            case BOOL_K -> BOOL_T;
            case STRING_K -> STRING_T;
            case TYPE_K -> TYPE_T;
            case FUN -> parseLambdaType();
            case IDENTIFIER -> classMap.containsKey(previous().getLexeme()) ? classMap.get(previous().getLexeme()).getType() : null;
            default -> null;
        };

        if (type == null) {
            current = currentCopy;

            if (reportErrorIfFails) {
                reporter.reportError("No such type found", typeToken);
            }
        }

        return type;
    }

    private LambdaType parseLambdaType() {
        List<Type> paramTypes = new ArrayList<>();
        Type returnType = VOID_T;
        if (scope.isInsideClass()) {
            paramTypes.add(scope.getTopClass().getType());
        }

        if (match(LEFT_PAREN)) {
            while (true) {
                if (match(DATA_TYPE)) {
                    Type t = parseVarType(true);
                    if (t != null) {
                        paramTypes.add(t);
                    }

                    if (!match(COMMA, COLON)) {
                        reporter.reportError("Expected comma separated params and colon separated return type", advance());
                    }
                }

                if (previous().getType() == COLON) {
                    returnType = parseVarType(false);
                    consume(RIGHT_PAREN, "Expect ')' after return type");
                    break;
                }
            }
        }
        return new LambdaType(paramTypes, returnType);
    }

    private While whileStatement() {
        Token whileToken = previous();
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression(BOOL_T);
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();

        if (condition.getResultType() != BOOL_T) {
            reporter.reportError("Expression does not evaluate to boolean", whileToken);
        }

        consume(SEMICOLON, "Expect ';' after while statement.");
        return new While(condition, body, debugInfo);
    }

    private Expression expressionStatement() {
        Expr expr = expression(null);
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Expression(expr);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        scope.addScope();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        scope.popScope();
        return statements;
    }

    private Expr assignment(Type expectedType) {
        Expr expr = or(expectedType);

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment(expectedType);

            if (expr instanceof Variable variable) {
                return new Assign(variable, value);
            } else if (expr instanceof Get get) {
                return new Set(get.getObject(), get.getName(), value, debugInfo);
            }

            reporter.reportError("Invalid assignment target.", equals);
        }

        return expr;
    }

    private Expr or(Type expectedType) {
        Expr expr = and(expectedType);

        while (match(OR)) {
            Token operator = previous();
            Expr right = and(expectedType);

            if (expr.getResultType() != BOOL_T) {
                reporter.reportError("Left expr does not evaluate to boolean", operator);
            }

            if (right.getResultType() != BOOL_T) {
                reporter.reportError("Right expr does not evaluate to boolean", operator);
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

            if (expr.getResultType() != BOOL_T) {
                reporter.reportError("Left expr does not evaluate to boolean", operator);
            }

            if (right.getResultType() != BOOL_T) {
                reporter.reportError("Right expr does not evaluate to boolean", operator);
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
                if (!Primitive.NUMBERS.contains(expr.getResultType()) && expr.getResultType() != STRING_T) {
                    reporter.reportError("LHS cannot perform addition on " + expr.getResultType().typeName(), operator);
                }

                if (!Primitive.NUMBERS.contains(expr.getResultType()) && expr.getResultType() != STRING_T) {
                    reporter.reportError("RHS cannot perform addition on " + right.getResultType().typeName(), operator);
                }
            } else {
                if (Primitive.NUMBERS.contains(expr.getResultType())) {
                    reporter.reportError("LHS cannot perform substraction on " + expr.getResultType().typeName(), operator);
                }

                if (Primitive.NUMBERS.contains(right.getResultType())) {
                    reporter.reportError("RHS cannot perform substraction on " + right.getResultType().typeName(), operator);
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
                reporter.reportError("LHS cannot perform operation on " + expr.getResultType().typeName(), operator);
            }

            if (Primitive.NUMBERS.contains(right.getResultType())) {
                reporter.reportError("RHS cannot perform operation on " + right.getResultType().typeName(), operator);
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
                if (right.getResultType() != BOOL_T) {
                    reporter.reportError("Expression does not evaluate to boolean", operator);
                }
            } else {
                if (Primitive.NUMBERS.contains(right.getResultType())) {
                    reporter.reportError("Expression does not evaluate to number", operator);
                }
            }
            return new Unary(operator, right, debugInfo);
        }

        return call(expectedType);
    }

    private List<Expr> parseCallArgs(LambdaType type) {
        List<Expr> args = new ArrayList<>(type.getParamTypes().size());
        if (type.getParamTypes().size() != 0) {
            List<Type> expectedTypes = type.getParamTypes();
            for (int i = 0; i < expectedTypes.size(); i++) {
                Type expectedType = expectedTypes.get(i);

                Expr arg = expression(expectedType);
                if (!expectedType.isAssignableFrom(arg.getResultType())) {
                    reporter.reportError("Wrong type of argument provided. Expected " + expectedType.typeName(), previous());
                }
                args.add(arg);

                if (i < expectedTypes.size() - 1) {
                    consume(COMMA, "Expected ',' after argument");
                }
            }
        }

        return args;
    }

    private Expr finishCall(Expr callee) {
        if (!callee.getResultType().getFields().containsKey("call") && !callee.getResultType().getFields().containsKey("init")) {
            reporter.reportError("Cannot call.", previous());
        }

        LambdaType callType = (LambdaType) callee.getResultType().getFields().get("call");
        List<Expr> arguments;

        if (callee.getResultType() instanceof LambdaType calleeType) {
            // Its a member function.

            if ()
                arguments = parseCallArgs(calleeType);
        } else if (callee.getResultType() instanceof app.saikat.iiLang.datatypes.ClassType classType) {
            LambdaType constructor = (LambdaType) classType.getField("init");

            arguments = parseCallArgs(constructor);
        } else {
            reporter.reportError("Not a function type. Cannot call", previous());
        }
        consume(RIGHT_PAREN, "Expect ')' after arguments.");

        return new Call(callee, arguments, debugInfo);
    }

    private Expr call(Type expectedType) {
        Expr expr = primary(null);

        while (true) { // [while-true]
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "Expect property name after '.'.");

                Type exprType = expr.getResultType();
                if (!exprType.getFields().containsKey(name.getLexeme())) {
                    reporter.reportError("No such field found on " + exprType.typeName(), name);
                }

                expr = new Get(expr, name, exprType, debugInfo);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr primary(Type expectedType) {
        if (match(FALSE)) return new Literal(false, BOOL_T, debugInfo);
        if (match(TRUE)) return new Literal(true, BOOL_T, debugInfo);
        if (match(NIL)) return new Literal(null, VOID_T, debugInfo);

        if (match(STRING, INT_8, INT_16, INT_32, INT_64, FLOAT_32, FLOAT_64)) {
            return new Literal(previous().literal, Primitive.getType(previous().getType()), debugInfo);
        }

        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token name = consume(IDENTIFIER, "Expect superclass field name.");

            app.saikat.iiLang.datatypes.ClassType scopedClass = scope.getTopClass().getSuperclass().getType();
            if (scopedClass == null) {
                reporter.reportError("super can only be used in class", keyword);
            } else {
                Type fieldType = scopedClass.getField(name.getLexeme());
                if (fieldType == null) {
                    reporter.reportError("No such method field in current class", name);
                }
                return new Super(keyword, name, fieldType);
            }

        }

        if (match(THIS)) {
            ClassType scopedClass = scope.getTopClass().getType();
            if (scopedClass == null) {
                reporter.reportError("this can only be used in class", previous());
            }
            Scope.VariableData capturedThis = scope.searchAndResolve(Names.THIS);
            assert (capturedThis != null);
            if (capturedThis.isCaptured()) {
                scope.getTopLambda().addCapturedParam(capturedThis.getVariable());
            }
            return capturedThis.getVariable();
        }

        if (match(IDENTIFIER)) {
            Scope.VariableData variableData = scope.searchAndResolve(previous().getLexeme());
            if (variableData == null) {
                reporter.reportError("No such variable found", previous());
                return null;
            } else {
                if (variableData.isCaptured() && scope.getTopLambda() != null) {
                    scope.getTopLambda().addCapturedParam(variableData.getVariable());
                }
            }
            return variableData.getVariable();
        }

        if (match(LEFT_PAREN)) {
            Expr castOrLambda = matchCastOrLambda(expectedType);

            if (castOrLambda == null) {
                // Grouping
                Expr expr = expression(expectedType);
                consume(RIGHT_PAREN, "Expect ')' after expression.");
                return new Grouping(expr, debugInfo);
            } else {
                return castOrLambda;
            }
        }

        reporter.reportError("Expect expression.", peek());
        return null;
    }

    private Expr matchCastOrLambda(Type expectedType) {
        int currentCopy = current;
        List<Token> params = new ArrayList<>();
        List<Type> paramTypes = new ArrayList<>();

        //Match '()' case too
        if (!match(RIGHT_BRACE)) {

            // Match 1st item

            // Check if params are defined with type, i.e. 'i8 count, f32 precision'
            if (match(DATA_TYPE)) {
                Type t = parseVarType(false);

                if (t != null) {

                    if (match(RIGHT_PAREN)) {

                        // Of type '(i8) <expression>'
                        Expr expression = expression(t);
                        t.accept(finishDefinition);
                        return new Cast(expression, t, debugInfo);
                    }

                    paramTypes.add(t);
                } else {
                    return null;
                }
            } else {
                return null;
            }

            // Match identifier. May be of type 'i64 t1, f64 f1'
            if (match(IDENTIFIER)) {
                params.add(previous());
            } else {
                current = currentCopy;
                return null;
            }

            // Map rest of the element similarly but we know if we expect datatype or not
            while (match(COMMA)) {
                    advance();
                    paramTypes.add(parseVarType(true));
                    params.add(consume(IDENTIFIER, "Expected parameter argument"));
            }
            consume(RIGHT_PAREN, "Expected ')' after parameters");
        }

        if (!match(LAMBDA)) {
            current = currentCopy;
            return null;
        } else {
            // Definitely a lambda

            List<Variable> lambdaParameters = createVariablesFromTokenAndType(params, paramTypes);

            if (scope.isInsideClass()) {
                lambdaParameters.add(0, new Variable(new Token("__this__"), scope.getTopClass().getType().getInstanceType(), debugInfo));
            }
            Lambda lambda = new Lambda(lambdaParameters, debugInfo);

            // Create lambda scope and add parameters to the scope
            scope.addScope(lambda);
            for (Variable param : lambda.getParams()) {
                scope.addVariableToScope(param);
            }


            List<Stmt> statements = new ArrayList<>();

            while (!check(RIGHT_BRACE) && !isAtEnd()) {
                statements.add(declaration());
            }

            lambda.getLambdaType().accept(finishDefinition);

            consume(RIGHT_BRACE, "Expect '}' after block.");
            scope.popScope();
            Block block = new Block(statements, debugInfo);
            lambda.setBody(block);
            return lambda;
        }
    }

    private List<Variable> createVariablesFromTokenAndType(List<Token> tokens, List<Type> types) {
        assert (tokens.size() == types.size());
        List<Variable> variables = new ArrayList<>(tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            variables.add(new Variable(tokens.get(i), types.get(i), debugInfo));
        }
        return variables;
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

        reporter.reportError(message, peek());
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
