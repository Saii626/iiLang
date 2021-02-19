package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.datatypes.SystemDefinedTypes.Primitive;
import app.saikat.iiLang.ast.interfaces.*;
import app.saikat.iiLang.parser.interfaces.TokenType;

public class Logical extends Expr {

	public enum LogicalOperator {
		AND, OR,;

		private static LogicalOperator getOperatorFromTokenType(TokenType tokenType) {
			assert (TokenType.LOGICAL_OPERATORS.contains(tokenType));
			return switch (tokenType) {
				case AND -> LogicalOperator.AND;
				case OR -> LogicalOperator.OR;
				default -> throw new RuntimeException("Cannot reach because of assert");
			};
		}
	}

	private final Expr left;
	private final LogicalOperator operator;
	private final Expr right;

	public Logical(Expr left, TokenType type, Expr right, CodeLocation codeLocation) {
		super(Primitive.BOOL_T, codeLocation);
		this.left = left;
		this.operator = LogicalOperator.getOperatorFromTokenType(type);
		this.right = right;
	}

	public Logical(Expr left, Token token, Expr right, CodeLocation codeLocation) {
		this(left, token.type(), right, codeLocation);
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitLogicalExpr(this);
	}

	public Expr getLeft() {
		return left;
	}

	public LogicalOperator getOperator() {
		return operator;
	}

	public Expr getRight() {
		return right;
	}
}
