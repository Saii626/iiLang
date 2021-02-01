package app.saikat.iiLang.ast.interfaces;

import app.saikat.iiLang.ast.expression.*;

public interface ExprVisitor<R> {

	R visitAssignExpr(Assign expr);

	R visitBinaryExpr(Binary expr);

	R visitCallExpr(Call expr);

	R visitCastExpr(Cast expr);

	R visitGetExpr(Get expr);

	R visitGroupingExpr(Grouping expr);

	R visitLiteralExpr(Literal expr);

	R visitLambdaExpr(Lambda expr);

	R visitLogicalExpr(Logical expr);

	R visitSetExpr(Set expr);

	R visitSuperExpr(Super expr);

	R visitThisExpr(This expr);

	R visitUnaryExpr(Unary expr);

	R visitVariableExpr(Variable expr);
}
