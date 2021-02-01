package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.*;

public class Variable extends Expr {

	final Token name;

	public Variable(Token name, Type resultType) {
		super(resultType);
		this.name = name;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitVariableExpr(this);
	}

	public Token getName() {
		return name;
	}
}
