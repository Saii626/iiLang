package app.saikat.iiLang.ast.visitors;

import java.util.List;

import app.saikat.iiLang.ast.expression.*;
import app.saikat.iiLang.ast.statement.*;
import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.ExprVisitor;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;
import app.saikat.iiLang.ast.interfaces.Type;

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
		StringBuilder stringBuilder = new StringBuilder("print( ");
		stringBuilder.append(stmt.getExpression().accept(this));
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}

	@Override
	public String visitReturnStmt(Return stmt) {
		StringBuilder stringBuilder = new StringBuilder("return( ");
		stringBuilder.append(stmt.getValue().accept(this));
		stringBuilder.append(" )");
		return stringBuilder.toString();
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
		StringBuilder stringBuilder = new StringBuilder("while( ");
		stringBuilder.append(stmt.getCondition().accept(this));
		stringBuilder.append(" ");
		stringBuilder.append(stmt.getBody().accept(this));
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}

	@Override
	public String visitAssignExpr(Assign expr) {
		StringBuilder stringBuilder = new StringBuilder("( = ");
		stringBuilder.append(expr.getName().getLexeme());
		stringBuilder.append(" ");
		stringBuilder.append(expr.getValue().accept(this));
		return stringBuilder.toString();
	}

	@Override
	public String visitBinaryExpr(Binary expr) {
		StringBuilder stringBuilder = new StringBuilder("( ");
		stringBuilder.append(expr.getOperator().getType().name());
		stringBuilder.append(" ");
		stringBuilder.append(expr.getLeft().accept(this));
		stringBuilder.append(" ");
		stringBuilder.append(expr.getRight().accept(this));
		stringBuilder.append(")");
		return null;
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
		StringBuilder stringBuilder = new StringBuilder("( CAST ");
		stringBuilder.append(expr.getCastType().typeName());
		stringBuilder.append(" ");
		stringBuilder.append(expr.getExpr().accept(this));
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}

	@Override
	public String visitGetExpr(Get expr) {
		StringBuilder stringBuilder = new StringBuilder("( GET ");
		stringBuilder.append(expr.getObject().accept(this));
		stringBuilder.append(" ");
		stringBuilder.append(expr.getName().getLexeme());
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}

	@Override
	public String visitGroupingExpr(Grouping expr) {
		StringBuilder stringBuilder = new StringBuilder("( ");
		stringBuilder.append(expr.getExpression().accept(this));
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}

	@Override
	public String visitLiteralExpr(Literal expr) {
		return "( " + expr.getValue().toString() + " )";
	}

	@Override
	public String visitLambdaExpr(Lambda expr) {
		StringBuilder stringBuilder = new StringBuilder("( LAMBDA ( ");
		List<Token> params = expr.getParams();
		List<Type> types = expr.getFunctionType().getParamTypes();

		for(int i=0; i < params.size(); i++) {
			stringBuilder.append("(");
			stringBuilder.append(types.get(i));
			stringBuilder.append(" ");
			stringBuilder.append(params.get(i).getLexeme());
			stringBuilder.append(")");
		}
		stringBuilder.append(" )");
		stringBuilder.append(expr.getBody().accept(this));
		return stringBuilder.toString();
	}

	@Override
	public String visitLogicalExpr(Logical expr) {
		StringBuilder stringBuilder = new StringBuilder("( ");
		stringBuilder.append(expr.getOperator().getType().name());
		stringBuilder.append(" ");
		stringBuilder.append(expr.getLeft().accept(this));
		stringBuilder.append(" ");
		stringBuilder.append(expr.getRight().accept(this));
		stringBuilder.append(" )");
		return null;
	}

	@Override
	public String visitSetExpr(Set expr) {
		StringBuilder stringBuilder = new StringBuilder("( SET ");
		stringBuilder.append(expr.getObject().accept(this));
		stringBuilder.append(" ");
		stringBuilder.append(expr.getValue().accept(this));
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}

	@Override
	public String visitSuperExpr(Super expr) {
		StringBuilder stringBuilder = new StringBuilder("( SUPER ");
		stringBuilder.append(expr.getField().getLexeme());
		stringBuilder.append(" )");
		return stringBuilder.toString();
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
