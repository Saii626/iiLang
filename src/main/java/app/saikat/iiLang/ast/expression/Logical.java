package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.datatypes.Primitive;
import app.saikat.iiLang.ast.interfaces.*;

public class Logical extends Expr {

	final Expr left;
	final Token operator;
	final Expr right;

	public Logical(Expr left, Token operator, Expr right) {
		super(Primitive.BOOL);
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitLogicalExpr(this);
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
