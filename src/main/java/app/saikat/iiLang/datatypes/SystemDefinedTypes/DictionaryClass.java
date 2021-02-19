package app.saikat.iiLang.datatypes.SystemDefinedTypes;

import app.saikat.iiLang.ast.expression.Variable;
import app.saikat.iiLang.datatypes.ClassType;
import app.saikat.iiLang.datatypes.LambdaType;
import app.saikat.iiLang.datatypes.interfaces.Type;

import java.util.List;

public class DictionaryClass extends ClassType {

    private final Type keyType;
    private final Type valueType;

    public DictionaryClass(String className, Type keyType, Type valueType) {
        super(className, null, null);
        this.keyType = keyType;
        this.valueType = valueType;

        this.addField(new Variable("get", LambdaType.getLambdaType(List.of(this, keyType), valueType), null), false, true);
        this.addField(new Variable("set", LambdaType.getLambdaType(List.of(this, keyType, valueType), Primitive.VOID_T), null), false, true);
        this.addField(new Variable("contains", LambdaType.getLambdaType(List.of(this, keyType), Primitive.BOOL_T), null), false, true);
    }

    @Override
    public boolean isAssignableFrom(Type type) {
        if (type instanceof DictionaryClass cmp) {
            return this.keyType.isAssignableFrom(cmp.keyType) && this.valueType.isAssignableFrom(cmp.valueType);
        }
        return false;
    }
}
