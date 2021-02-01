package app.saikat.iiLang.ast.interfaces;

import app.saikat.iiLang.ast.statement.*;

public interface StmtVisitor<R> {

	R visitBlockStmt(Block stmt);

	R visitClassStmt(Klass stmt);

	R visitIfStmt(If stmt);

	R visitPrintStmt(Print stmt);

	R visitReturnStmt(Return stmt);

	R visitVarStmt(Var stmt);

	R visitWhileStmt(While stmt);
}
