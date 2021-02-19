package app.saikat.iiLang.ast.expression;

import app.saikat.iiLang.ast.interfaces.CodeLocation;
import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.ast.interfaces.Expr;
import app.saikat.iiLang.ast.interfaces.ExprVisitor;
import app.saikat.iiLang.parser.interfaces.TokenType;

public class Binary extends Expr {

	public enum BinaryOperator {
		BANG_EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, MINUS, PLUS, SLASH, STAR;

		private static BinaryOperator getOperatorFromTokenType(TokenType tokenType) {
			assert(TokenType.BINARY_OPERATORS.contains(tokenType));
			return switch (tokenType) {
				case BANG_EQUAL -> BinaryOperator.BANG_EQUAL;
				case EQUAL_EQUAL -> BinaryOperator.EQUAL_EQUAL;
				case GREATER -> BinaryOperator.GREATER;
				case GREATER_EQUAL -> BinaryOperator.GREATER_EQUAL;
				case LESS -> BinaryOperator.LESS;
				case LESS_EQUAL -> BinaryOperator.LESS_EQUAL;
				case MINUS -> BinaryOperator.MINUS;
				case PLUS -> BinaryOperator.PLUS;
				case SLASH -> BinaryOperator.SLASH;
				case STAR -> BinaryOperator.STAR;
				default -> throw new RuntimeException("Cannot reach because of assert");
			};
		}
	}

	private final Expr left;
	private final BinaryOperator operator;
	private final Expr right;

	public Binary(Expr left, TokenType type, Expr right, Type resultType, CodeLocation codeLocation) {
		super(resultType, codeLocation);
		this.left = left;
		this.right = right;
		this.operator = BinaryOperator.getOperatorFromTokenType(type);
	}

	public Binary(Expr left, Token operator, Expr right, Type resultType, CodeLocation codeLocation) {
		this(left, operator.type(), right, resultType, codeLocation);
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitBinaryExpr(this);
	}

	public Expr getLeft() {
		return left;
	}

	public BinaryOperator getOperator() {
		return operator;
	}

	public Expr getRight() {
		return right;
	}
}
