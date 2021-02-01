package app.saikat.iiLang.ast.statement;

import app.saikat.iiLang.ast.datatypes.Primitive;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;

public class If implements Stmt {

	public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
		assert(condition.getResultType() == Primitive.BOOL);
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitIfStmt(this);
	}

	final Expr condition;
	final Stmt thenBranch;
	final Stmt elseBranch;

	public Expr getCondition() {
		return condition;
	}

	public Stmt getThenBranch() {
		return thenBranch;
	}

	public Stmt getElseBranch() {
		return elseBranch;
	}
}
