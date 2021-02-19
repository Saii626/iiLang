package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.ast.interfaces.*;

public class Set extends Expr {

	private final Expr object;
	private final Variable field;
	private final Expr value;

	public Set(Expr object, Variable field, Expr value, CodeLocation codeLocation) {
		super(value.getResultType(), codeLocation);
		this.object = object;
		this.field = field;
		this.value = value;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitSetExpr(this);
	}

	public Expr getObject() {
		return object;
	}

	public Expr getValue() {
		return value;
	}

	public Variable getField() {
		return field;
	}
}
