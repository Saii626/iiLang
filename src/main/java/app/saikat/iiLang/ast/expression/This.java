package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.*;

public class This extends Expr {

	final Token keyword;

	public This(Token keyword, Type resultType) {
		super(resultType);
		this.keyword = keyword;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitThisExpr(this);
	}

	public Token getKeyword() {
		return keyword;
	}
}
