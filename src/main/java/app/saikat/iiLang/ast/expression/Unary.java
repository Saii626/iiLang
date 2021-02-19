package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.*;
import app.saikat.iiLang.parser.interfaces.TokenType;

public class Unary extends Expr {

	public enum UnaryOperator {
		MINUS, BANG;

		private static UnaryOperator getOperatorFromTypeToken(TokenType type) {
			assert (TokenType.UNARY_OPERATORS.contains(type));
			return switch (type) {
				case MINUS -> UnaryOperator.MINUS;
				case BANG -> UnaryOperator.BANG;
				default -> throw new RuntimeException("Cannot reach because of assert");
			};
		}
	}

	private final UnaryOperator operator;
	private final Expr right;

	public Unary(TokenType operator, Expr right, CodeLocation codeLocation) {
		super(right.getResultType(), codeLocation);
		this.operator = UnaryOperator.getOperatorFromTypeToken(operator);
		this.right = right;
	}

	public Unary(Token operator, Expr right, CodeLocation codeLocation) {
	    this(operator.type(), right, codeLocation);
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitUnaryExpr(this);
	}

	public UnaryOperator getOperator() {
		return operator;
	}

	public Expr getRight() {
		return right;
	}
}
