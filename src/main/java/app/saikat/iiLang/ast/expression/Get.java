package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.ast.interfaces.*;

public class Get extends Expr {

	private final Expr object;
	private final String name;

	public Get(Expr object, String name, Type resultType, CodeLocation codeLocation) {
		super(resultType, codeLocation);
		this.object = object;
		this.name = name;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitGetExpr(this);
	}

	public Expr getObject() {
		return object;
	}

	public String getName() {
		return name;
	}
}
