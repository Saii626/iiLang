package app.saikat.iiLang.ast_evaluate;

import app.saikat.iiLang.datatypes.interfaces.Type;

public class Instance {

    private final Object val;
    private final Type instanceType;

    public Instance(Object val, Type instanceType) {
        this.val = val;
        this.instanceType = instanceType;
    }

    public Object getVal() {
        return val;
    }

    public Type getInstanceType() {
        return instanceType;
    }
}
