package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.*;

public class Unary extends Expr {

	final Token operator;
	final Expr right;

	public Unary(Token operator, Expr right) {
		super(right.getResultType());
		this.operator = operator;
		this.right = right;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitUnaryExpr(this);
	}

	public Token getOperator() {
		return operator;
	}

	public Expr getRight() {
		return right;
	}
}
