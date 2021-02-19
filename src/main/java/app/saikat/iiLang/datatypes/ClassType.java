package app.saikat.iiLang.datatypes;

import java.util.*;

import app.saikat.iiLang.ast.expression.Variable;
import app.saikat.iiLang.ast.interfaces.CodeLocation;
import app.saikat.iiLang.datatypes.SystemDefinedTypes.Primitive;
import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.datatypes.interfaces.TypeVisitor;

public class ClassType extends Type {

	public static class FieldInfo {
		private final Variable variable;
		private final boolean isStatic;

		private FieldInfo(Variable variable, boolean isStatic) {
			this.variable = variable;
			this.isStatic = isStatic;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			FieldInfo that = (FieldInfo) o;
			return isStatic == that.isStatic && variable.equals(that.variable);
		}

		@Override
		public int hashCode() {
			return Objects.hash(variable, isStatic);
		}
	};

    private final ClassType superClass;
	private final Map<String, FieldInfo> fieldsMap;
	private final CodeLocation codeLocation;
	private final Map<String, ClassType> dependentTypes;

	private final InstanceType  instanceType;

	public ClassType(String className, ClassType superClass, CodeLocation codeLocation) {
		super(className);
		this.superClass = superClass;
		this.fieldsMap = new HashMap<>();
		this.codeLocation = codeLocation;
		this.dependentTypes = new HashMap<>();
		this.instanceType = new InstanceType("instance_" + typeName(), this);
	}

	public void addField(Variable field, boolean isStatic) {
		this.fieldsMap.put(field.getName(), new FieldInfo(field, isStatic));
		this.fields.add(field);
	}

	public FieldInfo getFieldInfo(String name) {
		return fieldsMap.get(name);
	}

	public ClassType getSuperClass() {
		return superClass;
	}

	public void addDependentType(ClassType classType) {
		dependentTypes.put(classType.typeName(), classType);
	}

	public Map<String, ClassType> getDependentTypes() {
		return dependentTypes;
	}

	public CodeLocation getCodeLocation() {
		return codeLocation;
	}

	/**
	 * Finish off defining classType. System-defined fields are added, all fields are resolved (including those of
	 * superclass) and 'instanceType' is initialized
	 */
	public void classDefinitionComplete() {
		// merge fields: superclass fields/system-defined fields and user-defined fields
		// If superclass is present, then it would already have systemDefined fields
		List<Variable> userDefinedFields = new LinkedList<>(this.fields);
		this.fields.clear();

        if (this.superClass != null) {
			this.fields.addAll(this.superClass.fields);
		} else {
			List<Variable> systemDefinedFields = List.of(new Variable("getClassName", LambdaType.getLambdaType(List.of(this.instanceType), Primitive.STRING_T), null),
					new Variable("copy", LambdaType.getLambdaType(List.of(this.instanceType), this), null),
					new Variable("deepCopy", LambdaType.getLambdaType(List.of(this.instanceType), this), null),
					new Variable("toString", LambdaType.getLambdaType(List.of(this.instanceType), Primitive.STRING_T), null));

			this.fields.addAll(systemDefinedFields);
		}

		// Next, add/replace user defined fields
		ListIterator<Variable> it = this.fields.listIterator();
        while (it.hasNext()) {
        	Variable preDefinedField = it.next();
        	if (this.fieldsMap.containsKey(preDefinedField.getName())) {
        		FieldInfo userDefinedFieldInfo = this.fieldsMap.get(preDefinedField.getName());
        		Variable userDefinedField = userDefinedFieldInfo.variable;

        		// call is unconditionally overridden
				assert preDefinedField.getName().equals("call") || (preDefinedField.getResultType().isAssignableFrom(userDefinedField.getResultType()));

        		it.set(userDefinedField);
				userDefinedFields.remove(userDefinedField);
			}
		}

        // Add non overridden fields
		this.fields.addAll(userDefinedFields);

        // Add default constructor if no user defined ones exists
		Optional<Variable> cons = this.fields.stream().filter(f -> f.getName().equals("init")).findAny();
		assert (cons.isEmpty() || cons.get().getResultType().equals(instanceType));

		if (cons.isEmpty()) this.fields.add(new Variable("init", instanceType, null));

		// Initialize InstanceType
		for (Variable field : fields) {
			if (field.getResultType() instanceof LambdaType lambda) {
				this.instanceType.getFields().
			}
		}

	}

	@Override
	public <R> R accept(TypeVisitor<R> visitor) {
		return visitor.visitClassType(this);
	}

    @Override
	public boolean isAssignableFrom(Type type) {
		if (type instanceof ClassType classType) {
			if (classType.typeName.equals(this.typeName)) return true;
			else return isAssignableFrom(classType.superClass);
		} else {
			return false;
		}
	}
}
