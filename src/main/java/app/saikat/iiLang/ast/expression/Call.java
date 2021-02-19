package app.saikat.iiLang.ast.expression;

import java.util.List;

import app.saikat.iiLang.ast.interfaces.*;

public class Call extends Expr {

	private final Expr callee;
	private final List<Expr> arguments;

	public Call(Expr callee, List<Expr> arguments, CodeLocation codeLocation) {
		super(callee.getResultType().getFields().get("call"), codeLocation);
		assert (callee.getResultType().getFields().containsKey("call"));
		this.callee = callee;
		this.arguments = arguments;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitCallExpr(this);
	}

	public Expr getCallee() {
		return callee;
	}

	public List<Expr> getArguments() {
		return arguments;
	}
}
