package app.saikat.iiLang.ast.datatypes;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import app.saikat.iiLang.ast.interfaces.Type;
import app.saikat.iiLang.ast.interfaces.TypeVisitor;
import app.saikat.iiLang.parser.TokenType;

public enum Primitive implements Type {

	INT_8("i8"), INT_16("i16"), INT_32("i32"), INT_64("i64"), FLOAT_32("f32"), FLOAT_64("f64"), BOOL("bool"), VOID("void"), STRING("string");

	private final String name;

	private Primitive(String name) {
		this.name = name;
	}

	@Override
	public <R> R accept(TypeVisitor<R> visitor) {
		return visitor.visitPrimitive(this);
	}

	@Override
	public boolean isAssignableFrom(Type type) {
		if (type instanceof Primitive prim) {
			return assignableMap.get(prim).contains(this);
		} else {
			return false;
		}
	}

	@Override
	public String typeName() {
		return name;
	}

	public static EnumSet<Primitive> NUMBERS = EnumSet.of(INT_8, INT_16, INT_32, INT_64, FLOAT_32, FLOAT_64);

	public static Primitive getType(TokenType type) {
		switch (type) {
			case INT_8:  return INT_8;
			case INT_16: return INT_16;
			case INT_32: return INT_32;
			case INT_64: return INT_64;
			case FLOAT_32: return FLOAT_32;
			case FLOAT_64: return FLOAT_64;
			case BOOL: return BOOL;
			case STRING: return STRING;
			default: throw new NoSuchElementException();
		}
	}

	private static final Map<Primitive, List<Primitive>> assignableMap = new HashMap<>();

	static {
		assignableMap.put(INT_8, List.of(INT_8, INT_16, INT_32, INT_64, FLOAT_32, FLOAT_64));
		assignableMap.put(INT_16, List.of(INT_16, INT_32, INT_64, FLOAT_32, FLOAT_64));
		assignableMap.put(INT_32, List.of(INT_32, INT_64, FLOAT_32, FLOAT_64));
		assignableMap.put(INT_64, List.of(INT_64, FLOAT_32, FLOAT_64));
		assignableMap.put(FLOAT_32, List.of(FLOAT_32, FLOAT_64));
		assignableMap.put(FLOAT_64, List.of(FLOAT_64));
		assignableMap.put(BOOL, List.of(BOOL));
		assignableMap.put(VOID, List.of(VOID));
		assignableMap.put(STRING, List.of(STRING));
	}
}

