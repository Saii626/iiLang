package app.saikat.iiLang.ast.expression;

import java.util.List;

import app.saikat.iiLang.ast.interfaces.*;
import app.saikat.iiLang.datatypes.interfaces.Type;

public class Block extends Expr {

	private final List<Stmt> statements;

	public Block(Type resultType, List<Stmt> statements, CodeLocation codeLocation) {
		super(resultType, codeLocation);
		this.statements = statements;
	}

	public List<Stmt> getStatements() {
		return statements;
	}

	@Override
	public void setResultType(Type resultType) {
		super.setResultType(resultType);
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return null;
	}
}
