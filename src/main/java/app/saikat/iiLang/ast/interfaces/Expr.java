package app.saikat.iiLang.ast.interfaces;

import app.saikat.iiLang.datatypes.interfaces.Type;

public abstract class Expr {

	// Type of result produced by the expression
	private Type resultType;
	private final CodeLocation codeLocation;

	abstract public <R> R accept(ExprVisitor<R> visitor);

	public Type getResultType() {
		return resultType;
	}

	protected void setResultType(Type resultType) {
		this.resultType = resultType;
	}

	public CodeLocation getDebugInfo() {
		return codeLocation;
	}

	public Expr(Type resultType, CodeLocation codeLocation) {
		this.resultType = resultType;
		this.codeLocation = codeLocation;
	}
}
