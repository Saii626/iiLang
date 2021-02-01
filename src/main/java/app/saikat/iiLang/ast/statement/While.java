package app.saikat.iiLang.ast.statement;

import app.saikat.iiLang.ast.datatypes.Primitive;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;

public class While implements Stmt {
	public While(Expr condition, Stmt body) {
		assert(condition.getResultType() == Primitive.BOOL);
		this.condition = condition;
		this.body = body;
	}

	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitWhileStmt(this);
	}

	final Expr condition;
	final Stmt body;

	public Expr getCondition() {
		return condition;
	}

	public Stmt getBody() {
		return body;
	}
}
