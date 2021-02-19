package app.saikat.iiLang.ast.statement;

import app.saikat.iiLang.ast.interfaces.CodeLocation;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;

public class Expression extends Stmt {

    private final Expr expr;

    public Expression(Expr expr, CodeLocation codeLocation) {
        super(codeLocation);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitExpressionStmt(this);
    }
}
