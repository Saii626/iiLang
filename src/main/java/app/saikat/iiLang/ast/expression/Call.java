package app.saikat.iiLang.ast.expression;

import java.util.List;

import app.saikat.iiLang.ast.datatypes.FunctionType;
import app.saikat.iiLang.ast.interfaces.*;

public class Call extends Expr {

	final Expr callee;
	final List<Expr> arguments;

	public Call(Expr callee, List<Expr> arguments) {
		super(((FunctionType)callee.getResultType()).getReturnType());
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
