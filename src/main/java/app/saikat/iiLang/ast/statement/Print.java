package app.saikat.iiLang.ast.statement;

import app.saikat.iiLang.ast.datatypes.Primitive;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;

public class Print implements Stmt {

	public Print(Expr expression) {
		assert(expression.getResultType() == Primitive.STRING);
		this.expression = expression;
	}

	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitPrintStmt(this);
	}

	final Expr expression;

	public Expr getExpression() {
		return expression;
	}
}
