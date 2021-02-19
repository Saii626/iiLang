package app.saikat.iiLang.datatypes;

import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.datatypes.interfaces.TypeVisitor;

public class InstanceType extends Type {

    private final ClassType classType;

    InstanceType(String typeName, ClassType classType) {
        super(typeName);
        this.classType = classType;
    }

    @Override
    public <R> R accept(TypeVisitor<R> visitor) {
        return null;
    }

    public ClassType getClassType() {
        return classType;
    }

    @Override
    public boolean isAssignableFrom(Type type) {
        return false;
    }
}
