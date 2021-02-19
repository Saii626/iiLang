package app.saikat.iiLang.ast_evaluate;

import app.saikat.iiLang.ast.expression.*;
import app.saikat.iiLang.ast.interfaces.ExprVisitor;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;
import app.saikat.iiLang.ast.statement.*;
import app.saikat.iiLang.datatypes.SystemDefinedTypes.Primitive;
import app.saikat.iiLang.datatypes.interfaces.Type;

import java.util.Deque;
import java.util.Map;

public class AstEvaluator implements ExprVisitor<Object>, StmtVisitor<Void> {

    private Deque<Map<Variable, Object>> valuesMap;

    public Primitive getBiggerNumberType(Type left, Type right) {
        if (!Primitive.NUMBERS.contains(left) || !Primitive.NUMBERS.contains(right)) {
            return null;
        }

        if (left == Primitive.FLOAT_64_T || right == Primitive.FLOAT_64_T) {
            return Primitive.FLOAT_64_T;
        } else if (left == Primitive.FLOAT_32_T || right == Primitive.FLOAT_32_T) {
            return Primitive.FLOAT_32_T;
        } else if (left == Primitive.INT_64_T || right == Primitive.INT_64_T) {
            return Primitive.INT_64_T;
        } else if (left == Primitive.INT_32_T || right == Primitive.INT_32_T) {
            return Primitive.INT_32_T;
        } else if (left == Primitive.INT_16_T || right == Primitive.INT_16_T) {
            return Primitive.INT_16_T;
        } else {
            return Primitive.INT_8_T;
        }
    }

    @Override
    public Object visitAssignExpr(Assign expr) {
        Object value = expr.getValue().accept(this);
        valuesMap.peek().put(expr.getName(), value);
        return value;
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = expr.getLeft().accept(this);
        Object right = expr.getRight().accept(this);
        return switch (expr.getOperator().getType()) {
            case BANG_EQUAL -> left != right;
            case EQUAL_EQUAL -> left == right;
            case GREATER -> ((Comparable)left).compareTo(right) > 0;
            case GREATER_EQUAL -> ((Comparable)left).compareTo(right) >= 0;
            case LESS -> ((Comparable)left).compareTo(right) < 0;
            case LESS_EQUAL -> ((Comparable)left).compareTo(right) <= 0;
            case PLUS -> {
                Primitive type = getBiggerNumberType(expr.getLeft().getResultType(), expr.getRight().getResultType());
                if (type != null) {
                    if (type == Primitive.INT_8_T) {
                        yield ((Byte) left) + ((Byte) right);
                    } else if(type == Primitive.INT_16_T) {
                        yield ((Short) left) + ((Short) right);
                    } else if(type == Primitive.INT_32_T) {
                        yield ((Integer) left) + ((Integer) right);
                    } else if(type == Primitive.INT_64_T) {
                        yield ((Long) left) + ((Long) right);
                    } else if(type == Primitive.FLOAT_32_T) {
                        yield ((Float) left) + ((Float) right);
                    } else if(type == Primitive.FLOAT_64_T) {
                        yield ((Double) left) + ((Double) right);
                    } else {
                        throw new RuntimeException("Unexpected type " + type.typeName());
                    }
                } else {
                    yield ((String) left) + ((String) right);
                }
            };
            case MINUS -> {
                Primitive type = getBiggerNumberType(expr.getLeft().getResultType(), expr.getRight().getResultType());
                if (type != null) {
                    if (type == Primitive.INT_8_T) {
                        yield ((Byte) left) - ((Byte) right);
                    } else if (type == Primitive.INT_16_T) {
                        yield ((Short) left) - ((Short) right);
                    } else if (type == Primitive.INT_32_T) {
                        yield ((Integer) left) - ((Integer) right);
                    } else if (type == Primitive.INT_64_T) {
                        yield ((Long) left) - ((Long) right);
                    } else if (type == Primitive.FLOAT_32_T) {
                        yield ((Float) left) - ((Float) right);
                    } else if (type == Primitive.FLOAT_64_T) {
                        yield ((Double) left) - ((Double) right);
                    } else {
                        throw new RuntimeException("Unexpected type " + type.typeName());
                    }
                }
            }
            default -> null;
        };
    }

    @Override
    public Object visitCallExpr(Call expr) {
        return null;
    }

    @Override
    public Object visitCastExpr(Cast expr) {
        return null;
    }

    @Override
    public Object visitGetExpr(Get expr) {
        return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return null;
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return null;
    }

    @Override
    public Object visitLambdaExpr(Lambda expr) {
        return null;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) {
        return null;
    }

    @Override
    public Object visitSetExpr(Set expr) {
        return null;
    }

    @Override
    public Object visitSuperExpr(Super expr) {
        return null;
    }

    @Override
    public Object visitThisExpr(This expr) {
        return null;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        return null;
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        return null;
    }

    @Override
    public Void visitClassStmt(Klass stmt) {
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        return null;
    }
}
