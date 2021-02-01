package app.saikat.iiLang.ast.interfaces;

public abstract class Expr {

	// Type of result produced by the expression
	private final Type resultType;

	abstract public <R> R accept(ExprVisitor<R> visitor);

	public Type getResultType() {
		return resultType;
	}

	public Expr(Type resultType) {
		this.resultType = resultType;
	}
}
