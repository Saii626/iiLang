package app.saikat.iiLang.ast.datatypes;

import java.util.HashMap;
import java.util.Map;

import app.saikat.iiLang.ast.interfaces.Type;
import app.saikat.iiLang.ast.interfaces.TypeVisitor;
import app.saikat.iiLang.ast.statement.Klass;
import app.saikat.iiLang.ast.statement.Var;

public class ClassType implements Type {

	// Name of the class. This is the "type" of new class. Hence should be unique
	private final String className;

	// The actual class corrosponding to this class type. There is 1 to 1 corrospondance
	// Maybe null before klass is fully defined
	private Klass klass;

	// Map of field names to their type
	private final Map<String, Type> fieldTypes;

	public ClassType(String className, Klass klass) {
		this.className = className;
		this.klass = klass;
		this.fieldTypes = new HashMap<>();

		if (this.klass != null) {
			for (Var field : this.klass.getFields()) {
				fieldTypes.put(field.getVariable().getName().getLexeme(), field.getVariable().getResultType());
			}
		}
	}

	public String getClassName() {
		return className;
	}

	public Klass getKlass() {
		return klass;
	}

	public void setKlass(Klass klass) {
		this.klass = klass;
	}

	public void addField(String name, Type type) {
		fieldTypes.put(name, type);
	}

	public Type getField(String name) {
		return fieldTypes.get(name);
	}

	@Override
	public <R> R accept(TypeVisitor<R> visitor) {
		return visitor.visitClass(this);
	}

	@Override
	public String typeName() {
		return "class " + className;
	}

	@Override
	public boolean isAssignableFrom(Type type) {
		if (!(type instanceof ClassType)) return false;
		ClassType cmpType = (ClassType) type;

		if (className.equals(cmpType.className)) return true;
		return isAssignableFrom(cmpType.klass.getSuperclass().getType());
	}

	//@Override
	//public boolean equals(Object obj) {
	//	if (obj instanceof ClassType) {
	//		return className.equals(((ClassType)obj).className);
	//	}
	//	return false;
	//}
}
