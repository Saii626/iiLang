package app.saikat.iiLang.ast.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.saikat.iiLang.ast.interfaces.CodeLocation;
import app.saikat.iiLang.datatypes.LambdaType;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.ExprVisitor;

public class Lambda extends Expr {

	public Lambda(List<Variable> params, CodeLocation codeLocation) {
		super(new LambdaType(params.stream().map(Expr::getResultType).collect(Collectors.toList()), null), codeLocation);
		this.params = params;
		this.capturedParams = new ArrayList<>();

	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitLambdaExpr(this);
	}

	private final List<Variable> params;
	private final List<Variable> capturedParams;
	private Block body;

	public List<Variable> getParams() {
		return params;
	}

	public List<Variable> getCapturedParams() {
		return capturedParams;
	}

	public  void addCapturedParam(Variable variable) {
		this.capturedParams.add(variable);
	}

	public LambdaType getLambdaType() {
		return (LambdaType) this.getResultType();
	}

	public Block getBody() {
		return body;
	}

	public void setBody(Block body) {
		this.body = body;
	}
}
