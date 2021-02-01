package app.saikat.iiLang.ast.interfaces;

import app.saikat.iiLang.ast.datatypes.ClassType;
import app.saikat.iiLang.ast.datatypes.FunctionType;
import app.saikat.iiLang.ast.datatypes.Primitive;

public interface TypeVisitor<R> {

	<T> R visitPrimitive(Primitive primitive);

	<T> R visitClass(ClassType cls);

	<T> R visitFunction(FunctionType fn);
}
