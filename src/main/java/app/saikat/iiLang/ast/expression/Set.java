package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.*;

public class Set extends Expr {

	final Expr object;
	final Token name;
	final Expr value;

	public Set(Expr object, Token name, Expr value) {
		super(value.getResultType());
		this.object = object;
		this.name = name;
		this.value = value;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitSetExpr(this);
	}

	public Expr getObject() {
		return object;
	}

	public Token getName() {
		return name;
	}

	public Expr getValue() {
		return value;
	}
}
