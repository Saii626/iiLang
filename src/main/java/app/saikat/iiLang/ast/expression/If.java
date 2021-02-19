package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.ast.interfaces.*;
import app.saikat.iiLang.datatypes.SystemDefinedTypes.Primitive;

public class If extends Expr {

	public If(Expr condition, Expr thenBranch, Expr elseBranch, CodeLocation codeLocation) {
		super(thenBranch.getResultType(), codeLocation);
		assert(condition.getResultType() == Primitive.BOOL_T);
		assert(thenBranch.getResultType().isAssignableFrom(elseBranch.getResultType()) ||
				(elseBranch.getResultType().isAssignableFrom(thenBranch.getResultType())));

		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	private final Expr condition;
	private final Expr thenBranch;
	private final Expr elseBranch;

	public Expr getCondition() {
		return condition;
	}

	public Expr getThenBranch() {
		return thenBranch;
	}

	public Expr getElseBranch() {
		return elseBranch;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitIfExpr(this);
	}
}
