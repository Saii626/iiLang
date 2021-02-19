package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.ast.interfaces.*;

public class Variable extends Expr {

	private final String name;

	public Variable(String name, Type resultType, CodeLocation codeLocation) {
		super(resultType, codeLocation);
		this.name = name;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitVariableExpr(this);
	}

	public String getName() {
		return name;
	}
}
