package app.saikat.iiLang.ast.statement;

import app.saikat.iiLang.ast.expression.Variable;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;

public class Var implements Stmt {

	public Var(Variable variable, Expr initializer) {
		assert(variable.getResultType().isAssignableFrom(initializer.getResultType()));
		this.variable = variable;
		this.initializer = initializer;
	}

	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitVarStmt(this);
	}

	final Variable variable;
	final Expr initializer;

	public Variable getVariable() {
		return variable;
	}

	public Expr getInitializer() {
		return initializer;
	}
}
