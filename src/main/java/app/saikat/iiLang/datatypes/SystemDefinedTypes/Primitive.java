package app.saikat.iiLang.datatypes.SystemDefinedTypes;

import java.util.*;

import app.saikat.iiLang.datatypes.ClassType;
import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.datatypes.interfaces.TypeVisitor;
import app.saikat.iiLang.parser.interfaces.TokenType;

public class Primitive extends ClassType {

    public static Primitive INT_8_T = new Primitive("i8");
    public static Primitive INT_16_T = new Primitive("i16");
    public static Primitive INT_32_T = new Primitive("i32");
    public static Primitive INT_64_T = new Primitive("i64");
    public static Primitive FLOAT_32_T = new Primitive("f32");
    public static Primitive FLOAT_64_T = new Primitive("f64");
    public static Primitive BOOL_T = new Primitive("bool");
    public static Primitive STRING_T = new Primitive("string");
    public static Primitive VOID_T = new Primitive("void");

    private Primitive(String name) {
        super(name, null, null);
    }

    @Override
    public <R> R accept(TypeVisitor<R> visitor) {
        return visitor.visitPrimitiveType(this);
    }

    @Override
    public boolean isAssignableFrom(Type type) {
        if (type instanceof Primitive prim) {
            return assignableMap.get(prim).contains(this);
        } else {
            return false;
        }
    }

    public static Set<Primitive> PRIMITIVES = Set.of(INT_8_T, INT_16_T, INT_32_T, INT_64_T, FLOAT_32_T, FLOAT_64_T, BOOL_T, STRING_T, VOID_T);
    public static Set<Primitive> NUMBERS = Set.of(INT_8_T, INT_16_T, INT_32_T, INT_64_T, FLOAT_32_T, FLOAT_64_T);
    public static Set<Primitive> INTEGERS = Set.of(INT_8_T, INT_16_T, INT_32_T, INT_64_T);
    public static Set<Primitive> FLOATS = Set.of(FLOAT_32_T, FLOAT_64_T);

    public static Primitive getType(TokenType type) {
        return switch (type) {
            case INT_8 -> INT_8_T;
            case INT_16 -> INT_16_T;
            case INT_32 -> INT_32_T;
            case INT_64 -> INT_64_T;
            case FLOAT_32 -> FLOAT_32_T;
            case FLOAT_64 -> FLOAT_64_T;
            case BOOL -> BOOL_T;
            case STRING -> STRING_T;
            default -> throw new NoSuchElementException();
        };
    }

    private static final Map<Primitive, List<Primitive>> assignableMap = new HashMap<>();

    static {
        assignableMap.put(INT_8_T, List.of(INT_8_T, INT_16_T, INT_32_T, INT_64_T, FLOAT_32_T, FLOAT_64_T));
        assignableMap.put(INT_16_T, List.of(INT_16_T, INT_32_T, INT_64_T, FLOAT_32_T, FLOAT_64_T));
        assignableMap.put(INT_32_T, List.of(INT_32_T, INT_64_T, FLOAT_32_T, FLOAT_64_T));
        assignableMap.put(INT_64_T, List.of(INT_64_T, FLOAT_32_T, FLOAT_64_T));
        assignableMap.put(FLOAT_32_T, List.of(FLOAT_32_T, FLOAT_64_T));
        assignableMap.put(FLOAT_64_T, List.of(FLOAT_64_T));
        assignableMap.put(BOOL_T, List.of(BOOL_T));
        assignableMap.put(VOID_T, List.of(VOID_T));
        assignableMap.put(STRING_T, List.of(STRING_T));
    }
}

