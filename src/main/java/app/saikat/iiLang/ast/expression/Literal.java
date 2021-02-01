package app.saikat.iiLang.ast.expression;
import app.saikat.iiLang.ast.interfaces.*;

public class Literal extends Expr {

	final Object value;

	public Literal(Object value, Type literalType) {
		super(literalType);
		this.value = value;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitLiteralExpr(this);
	}

	public Object getValue() {
		return value;
	}
}
