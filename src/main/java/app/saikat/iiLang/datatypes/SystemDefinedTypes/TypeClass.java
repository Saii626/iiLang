//package app.saikat.iiLang.datatypes.SystemDefinedTypes;
//
//import app.saikat.iiLang.ast.interfaces.CodeLocation;
//import app.saikat.iiLang.datatypes.ClassType;
//
///**
// * Meta type for the programming language
// * {type} {modifier, ...} {identifier} = {expression};
// *
// * During compilation, this will be inferred as
// * new Variable({identifier}, {type});
// * new Var(variable, {expression});
// *
// * During runtime, this will be inferred as
// * {.type=type, .obj=obj}
// *
// * This TypeClass allows us to do similar thing
// */
//public class TypeClass extends ClassType {
//
//    public TypeClass(String className, ClassType superClass, CodeLocation codeLocation) {
//        super(className, superClass, codeLocation);
//    }
//}
