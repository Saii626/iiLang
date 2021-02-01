package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.ExprVisitor;

public class Binary extends Expr {

	final Expr left;
	final Token operator;
	final Expr right;

	public Binary(Expr left, Token operator, Expr right) {
		super(left.getResultType());
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitBinaryExpr(this);
	}

	public Expr getLeft() {
		return left;
	}

	public Token getOperator() {
		return operator;
	}

	public Expr getRight() {
		return right;
	}
}
