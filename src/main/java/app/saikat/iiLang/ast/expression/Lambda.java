package app.saikat.iiLang.ast.expression;

import java.util.List;

import app.saikat.iiLang.ast.datatypes.FunctionType;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.ExprVisitor;
import app.saikat.iiLang.ast.statement.Block;

public class Lambda extends Expr {

	public Lambda(FunctionType functionType, List<Variable> params, Block body) {
		super(functionType);
		this.params = params;
		this.functionType = functionType;
		this.body = body;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitLambdaExpr(this);
	}

	final List<Variable> params;
	final FunctionType functionType;
	final Block body;

	public List<Variable> getParams() {
		return params;
	}

	public FunctionType getFunctionType() {
		return functionType;
	}

	public Block getBody() {
		return body;
	}
}
