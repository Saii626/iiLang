package app.saikat.iiLang.datatypes.interfaces;

import app.saikat.iiLang.datatypes.*;
import app.saikat.iiLang.datatypes.SystemDefinedTypes.Primitive;

public interface TypeVisitor<R> {

    R visitClassType(ClassType type);

    R visitLambdaType(LambdaType lambdaType);

    R visitPrimitiveType(Primitive primitive);
}
