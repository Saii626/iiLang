package app.saikat.iiLang.ast.visitors;

import java.util.List;

import app.saikat.iiLang.ast.expression.*;
import app.saikat.iiLang.ast.statement.*;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.ExprVisitor;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;
import app.saikat.iiLang.datatypes.interfaces.Type;

public class AstPrinter implements ExprVisitor<String>, StmtVisitor<String> {

	@Override
	public String visitBlockStmt(Block stmt) {
		StringBuilder stringBuilder = new StringBuilder("block(\n");

		for (Stmt statement : stmt.getStatements()){
			stringBuilder.append("\t");
			stringBuilder.append(statement.accept(this));
			stringBuilder.append("\n");
		}

		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	@Override
	public String visitClassStmt(Klass stmt) {
		StringBuilder stringBuilder = new StringBuilder("class ");
		stringBuilder.append(stmt.getType().typeName());
		stringBuilder.append("(\n");

		for (Var fields : stmt.getFields()) {
			stringBuilder.append("\t");
			stringBuilder.append(fields.accept(this));
			stringBuilder.append("\n");
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	@Override
	public String visitExpressionStmt(Expression stmt) {
		return stmt.getExpr().accept(this);
	}

	@Override
	public String visitIfStmt(If stmt) {
		StringBuilder stringBuilder = new StringBuilder("if( ");
		stringBuilder.append(stmt.getCondition().accept(this));
		stringBuilder.append(" ");
		stringBuilder.append(stmt.getThenBranch().accept(this));

		if (stmt.getElseBranch() != null) {
			stringBuilder.append(" ");
			stringBuilder.append(stmt.getElseBranch().accept(this));
		}
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}

	@Override
	public String visitPrintStmt(Print stmt) {
		return "print( " + stmt.getExpression().accept(this) + " )";
	}

	@Override
	public String visitReturnStmt(Return stmt) {
		return "return( " + stmt.getValue().accept(this) + " )";
	}

	@Override
	public String visitVarStmt(Var stmt) {
		StringBuilder stringBuilder = new StringBuilder("( ");
		stringBuilder.append(stmt.getVariable().getResultType().typeName());
		stringBuilder.append(" ");
		stringBuilder.append(stmt.getVariable().getName().getLexeme());

		if (stmt.getInitializer() != null) {
			stringBuilder.append(" ");
			stringBuilder.append(stmt.getInitializer().accept(this));
		}
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}

	@Override
	public String visitWhileStmt(While stmt) {
		return "while( " + stmt.getCondition().accept(this) +
				" " + stmt.getBody().accept(this) + " )";
	}

	@Override
	public String visitAssignExpr(Assign expr) {
		return "( = " + expr.getName().getName().getLexeme() +
				" " + expr.getValue().accept(this);
	}

	@Override
	public String visitBinaryExpr(Binary expr) {
		return "( " + expr.getOperator().getType().name() +	" " +
				expr.getLeft().accept(this) + " " +
				expr.getRight().accept(this) + ")";
	}

	@Override
	public String visitCallExpr(Call expr) {
		StringBuilder stringBuilder = new StringBuilder("( CALL ");
		stringBuilder.append(expr.getCallee().accept(this));
		
		stringBuilder.append(" (");
		for (Expr argExp : expr.getArguments()) {
			stringBuilder.append(" ");
			stringBuilder.append(argExp.accept(this));
		}
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}

	@Override
	public String visitCastExpr(Cast expr) {
		return "( CAST " + expr.getCastType().typeName() + " " +
				expr.getExpr().accept(this) + " )";
	}

	@Override
	public String visitGetExpr(Get expr) {
		return "( GET " + expr.getObject().accept(this) + " " +
				expr.getName().getLexeme() + " )";
	}

	@Override
	public String visitGroupingExpr(Grouping expr) {
		return "( " + expr.getExpression().accept(this) + " )";
	}

	@Override
	public String visitLiteralExpr(Literal expr) {
		return "( " + expr.getValue().toString() + " )";
	}

	@Override
	public String visitLambdaExpr(Lambda expr) {
		StringBuilder stringBuilder = new StringBuilder("( LAMBDA ( ");
		List<Variable> params = expr.getParams();
		List<Type> types = expr.getLambdaType().getParamTypes();

		for(int i=0; i < params.size(); i++) {
			stringBuilder.append("(");
			stringBuilder.append(types.get(i));
			stringBuilder.append(" ");
			stringBuilder.append(params.get(i).getName().getLexeme());
			stringBuilder.append(")");
		}
		stringBuilder.append(" )");
		stringBuilder.append(expr.getBody().accept(this));
		return stringBuilder.toString();
	}

	@Override
	public String visitLogicalExpr(Logical expr) {
		return "( " + expr.getOperator().getType().name() + " " +
				expr.getLeft().accept(this) + " " +
				expr.getRight().accept(this) + " )";
	}

	@Override
	public String visitSetExpr(Set expr) {
		return "( SET " + expr.getObject().accept(this) + " " +
				expr.getValue().accept(this) + " )";
	}

	@Override
	public String visitSuperExpr(Super expr) {
		return "( SUPER " + expr.getField().getLexeme() + " )";
	}

	@Override
	public String visitThisExpr(This expr) {
		return "THIS";
	}

	@Override
	public String visitUnaryExpr(Unary expr) {
		return "( " + expr.getOperator().getType().name() + expr.getRight().accept(this) + " )";
	}

	@Override
	public String visitVariableExpr(Variable expr) {
		return expr.getName().getLexeme();
	}
}
