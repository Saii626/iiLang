package app.saikat.iiLang.ast.statement;

import app.saikat.iiLang.ast.interfaces.CodeLocation;
import app.saikat.iiLang.datatypes.SystemDefinedTypes.Primitive;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;

public class Print extends Stmt {

	public Print(Expr expression, CodeLocation codeLocation) {
		super(codeLocation);
		assert(expression.getResultType() == Primitive.STRING_T);
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
