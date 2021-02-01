package app.saikat.iiLang.ast.interfaces;

public interface Stmt {
	<R> R accept(StmtVisitor<R> visitor);
}
