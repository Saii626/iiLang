package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.ExprVisitor;
import app.saikat.iiLang.ast.interfaces.Type;

public class Cast extends Expr {

	private final Expr expr;
	private final Type castType;

	public Cast(Expr expr, Type castType) {
		super(castType);
		this.expr = expr;
		this.castType = castType;
	}
	
	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitCastExpr(this);
	}

	public Expr getExpr() {
		return expr;
	}

	public Type getCastType() {
		return castType;
	}
}
