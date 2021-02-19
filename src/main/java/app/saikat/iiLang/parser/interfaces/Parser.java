package app.saikat.iiLang.parser.interfaces;

import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.datatypes.ClassType;
import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.parser.Token;

import java.util.List;

public interface Parser {

    ClassType parseClass(List<Token> tokens, Scope scope);

    Expr parseExpression(List<Token> tokens, Type expectedType, Scope scope);

    Stmt parseStatement(List<Token> tokens, Scope scope);

    Type parseType(List<Token> tokens, Scope scope);
}
