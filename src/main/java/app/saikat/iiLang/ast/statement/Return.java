package app.saikat.iiLang.ast.statement;

import app.saikat.iiLang.ast.interfaces.CodeLocation;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;

public class Return extends Stmt {

	public Return(Expr value, CodeLocation codeLocation) {
		super(codeLocation);
		this.value = value;
	}

	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitReturnStmt(this);
	}

	final Expr value;

	public Expr getValue() {
		return value;
	}
}
