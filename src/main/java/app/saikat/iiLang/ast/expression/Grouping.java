package app.saikat.iiLang.ast.expression;
import app.saikat.iiLang.ast.interfaces.*;

public class Grouping extends Expr {

	private final Expr expression;

	public Grouping(Expr expression, CodeLocation codeLocation) {
		super(expression.getResultType(), codeLocation);
		this.expression = expression;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitGroupingExpr(this);
	}

	public Expr getExpression() {
		return expression;
	}
}
