package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.ExprVisitor;

public class Assign extends Expr {
	final Variable variable;
	final Expr value;

	public Assign(Variable variable, Expr value) {
		super(value.getResultType());
		assert(variable.getResultType().isAssignableFrom(value.getResultType()));
		this.variable = variable;
		this.value = value;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitAssignExpr(this);
	}

	public Variable getName() {
		return variable;
	}

	public Expr getValue() {
		return value;
	}
}
