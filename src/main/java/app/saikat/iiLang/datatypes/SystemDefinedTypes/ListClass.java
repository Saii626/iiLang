package app.saikat.iiLang.datatypes.SystemDefinedTypes;

import app.saikat.iiLang.ast.expression.Variable;
import app.saikat.iiLang.datatypes.ClassType;
import app.saikat.iiLang.datatypes.LambdaType;
import app.saikat.iiLang.datatypes.interfaces.Type;

import java.util.List;

public class ListClass extends ClassType {

    private final Type type;
    public ListClass(String className, Type type) {
        super(className, null, null);
        this.type = type;

        this.addField(new Variable("get", LambdaType.getLambdaType(List.of(this, Primitive.INT_32_T), type), null), false, true);
        this.addField(new Variable("set", LambdaType.getLambdaType(List.of(this, Primitive.INT_32_T, type), Primitive.VOID_T), null), false, true);
        this.addField(new Variable("append", LambdaType.getLambdaType(List.of(this, type), Primitive.VOID_T), null), false, true);
    }

    @Override
    public boolean isAssignableFrom(Type type) {
        if (type instanceof ListClass cmp) {
            return this.type.isAssignableFrom(cmp.type);
        }
        return false;
    }
}
