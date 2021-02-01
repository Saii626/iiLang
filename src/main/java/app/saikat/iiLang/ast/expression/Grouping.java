package app.saikat.iiLang.ast.expression;
import app.saikat.iiLang.ast.interfaces.*;

public class Grouping extends Expr {

	final Expr expression;

	public Grouping(Expr expression) {
		super(expression.getResultType());
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
