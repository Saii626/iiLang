package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.ast.interfaces.*;

public class Super extends Expr {

	private final String field;

	public Super(String field, Type resultType, CodeLocation codeLocation) {
		super(resultType, codeLocation);
		this.field = field;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitSuperExpr(this);
	}

	public String getField() {
		return field;
	}
}
