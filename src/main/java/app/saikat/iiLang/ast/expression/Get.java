package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.*;

public class Get extends Expr {

	final Expr object;
	final Token name;

	public Get(Expr object, Token name, Type resultType) {
		super(resultType);
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

	public Token getName() {
		return name;
	}
}
