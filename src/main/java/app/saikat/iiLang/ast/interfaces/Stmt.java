package app.saikat.iiLang.ast.interfaces;

public abstract class Stmt {

	private CodeLocation codeLocation;

	protected Stmt(CodeLocation codeLocation) {
		this.codeLocation = codeLocation;
	}

	public CodeLocation getDebugInfo() {
		return codeLocation;
	}

	public void setDebugInfo(CodeLocation codeLocation) {
		this.codeLocation = codeLocation;
	}

	abstract public <R> R accept(StmtVisitor<R> visitor);
}
