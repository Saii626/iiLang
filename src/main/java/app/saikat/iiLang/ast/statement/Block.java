package app.saikat.iiLang.ast.statement;

import java.util.List;

import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;

public class Block implements Stmt {

	public Block(List<Stmt> statements) {
		this.statements = statements;
	}

	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitBlockStmt(this);
	}

	final List<Stmt> statements;

	public List<Stmt> getStatements() {
		return statements;
	}
}
