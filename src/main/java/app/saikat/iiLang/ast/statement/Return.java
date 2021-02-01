package app.saikat.iiLang.ast.statement;

import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;
import app.saikat.iiLang.parser.Token;

public class Return implements Stmt {

	public Return(Token keyword, Expr value) {
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
