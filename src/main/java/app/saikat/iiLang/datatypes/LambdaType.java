package app.saikat.iiLang.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.saikat.iiLang.ast.expression.Variable;
import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.datatypes.interfaces.TypeVisitor;

public class LambdaType extends Type {

	private final List<Type> paramTypes;
	private final Type returnType;

	private LambdaType(List<Type> paramTypes, Type returnType) {
		super(createTypename(paramTypes, returnType));
		this.paramTypes = paramTypes;
		this.returnType = returnType;

		this.fields.add(new Variable("call", returnType, null));
		this.fields.add(new Variable("bind", closeOver(), null));
	}

	public List<Type> getParamTypes() {
		return paramTypes;
	}

	public Type getReturnType() {
		return returnType;
	}

	@Override
	public <R> R accept(TypeVisitor<R> visitor) {
		return visitor.visitLambdaType(this);
	}

	@Override
	public boolean isAssignableFrom(Type type) {
		if (type instanceof LambdaType cmpType) {

			if (paramTypes.size() != cmpType.paramTypes.size()) return false;

			boolean flag = true;
			for (int i = 0; i < paramTypes.size(); i++) {
				if (!paramTypes.get(i).isAssignableFrom(cmpType.paramTypes.get(i))) {
					flag = false;
					break;
				}
			}

			if (!flag) return false;
			return returnType.isAssignableFrom(cmpType.returnType);
		}
		return false;
	}

	public boolean hasSameSignature(LambdaType type) {
	    return paramTypes.equals(type.paramTypes) && returnType.equals(type.returnType);
	}

	public LambdaType closeOver() {
		assert paramTypes.size() > 0;
		List<Type> params = new ArrayList<>(paramTypes);
		params.remove(0);
		return LambdaType.getLambdaType(params, returnType);
	}

	private static String createTypename(List<Type> paramTypes, Type returnType) {
		StringBuilder strBuilder = new StringBuilder("fn(");
		for(Type t : paramTypes) {
			strBuilder.append(t.typeName());
			strBuilder.append(',');
		}

		if (!paramTypes.isEmpty()) strBuilder.setLength(strBuilder.length()-1);
		strBuilder.append(':');
		strBuilder.append(returnType.typeName());
		strBuilder.append(')');
		return strBuilder.toString();
	}

	private static record TypeBundle(List<Type> params, Type returnType) {};
	private static final Map<TypeBundle, LambdaType> allLambdaInstances = new HashMap<>();

	public static LambdaType getLambdaType(List<Type> params, Type returnType) {
		TypeBundle key = new TypeBundle(params, returnType);
		if (allLambdaInstances.containsKey(key)) {
			return allLambdaInstances.get(key);
		} else {
			LambdaType newType = new LambdaType(params, returnType);
			allLambdaInstances.put(key, newType);
			return newType;
		}
	}

	//@Override
	//public boolean equals(Object obj) {
	//	if (obj instanceof FunctionType) {
	//		FunctionType other = (FunctionType) obj;
	//		return paramTypes.equals(other.paramTypes) && returnType.equals(other.returnType);
	//	}
	//	return false;
	//}
}
