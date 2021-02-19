package app.saikat.iiLang.ast.expression;
import app.saikat.iiLang.ast.interfaces.*;
import app.saikat.iiLang.datatypes.interfaces.Type;

public class Literal extends Expr {

	private final Object value;

	public Literal(Object value, Type literalType, CodeLocation codeLocation) {
		super(literalType, codeLocation);
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
