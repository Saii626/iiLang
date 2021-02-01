package app.saikat.iiLang.ast.datatypes;

import java.util.List;

import app.saikat.iiLang.ast.interfaces.Type;
import app.saikat.iiLang.ast.interfaces.TypeVisitor;

public class FunctionType implements Type {

	private final List<Type> paramTypes;

	private final Type returnType;

	// Computated property. Added here just for the sake of caching
	private final String typeName;

	public FunctionType(List<Type> paramTypes, Type returnType) {
		this.paramTypes = paramTypes;
		this.returnType = returnType;

		StringBuilder strBuilder = new StringBuilder("fn(");
		for(Type t : paramTypes) {
			strBuilder.append(t.typeName());
			strBuilder.append(',');
		}
		
		if (!paramTypes.isEmpty()) strBuilder.setLength(strBuilder.length()-1);
		strBuilder.append(':');
		strBuilder.append(returnType.typeName());
		strBuilder.append(')');
		this.typeName = strBuilder.toString();
	}

	public List<Type> getParamTypes() {
		return paramTypes;
	}

	public Type getReturnType() {
		return returnType;
	}

	public String getTypeName() {
		return typeName;
	}

	@Override
	public <R> R accept(TypeVisitor<R> visitor) {
		return visitor.visitFunction(this);
	}

	@Override
	public String typeName() {
		return this.typeName;
	}

	@Override
	public boolean isAssignableFrom(Type type) {
		if (!(type instanceof FunctionType)) return false;

		FunctionType cmpType = (FunctionType) type;
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


	//@Override
	//public boolean equals(Object obj) {
	//	if (obj instanceof FunctionType) {
	//		FunctionType other = (FunctionType) obj;
	//		return paramTypes.equals(other.paramTypes) && returnType.equals(other.returnType);
	//	}
	//	return false;
	//}
}
