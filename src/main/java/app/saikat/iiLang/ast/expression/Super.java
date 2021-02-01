package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.*;

public class Super extends Expr {

	final Token keyword;
	final Token field;

	public Super(Token keyword, Token field, Type resultType) {
		super(resultType);
		this.keyword = keyword;
		this.field = field;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitSuperExpr(this);
	}

	public Token getKeyword() {
		return keyword;
	}

	public Token getField() {
		return field;
	}
}
