package app.saikat.iiLang.datatypes.interfaces;

import app.saikat.iiLang.ast.expression.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class Type {

	protected final String typeName;
	private final UUID id;

	protected final List<Variable> fields;

	protected Type(String typeName) {
		this.typeName = typeName;
		this.fields = new ArrayList<>();
		this.id = UUID.randomUUID();
	}

	abstract public <R> R accept(TypeVisitor<R> visitor);

	/**
	* String representation of the type
	* @return string
	 */
	public String typeName() {
		return typeName;
	}

	/**
	 * Get map of fields and their types defined on this type
	 * @return map of field to type defined on this type
	 */
	public List<Variable> getFields() {
		return fields;
	}


	public List<Variable> getFields(String name) {
		return fields.stream().filter(f -> f.getName().equals(name)).collect(Collectors.toList());
	}

	public List<Variable> getFields(String name, Type type) {
		return fields.stream().filter(f -> f.getName().equals(name) && type.isAssignableFrom(f.getResultType())).collect(Collectors.toList());
	}

	/**
	* Checks if this type can be assigned from another type
	* @param type type to compare against
	* @return true iff 'this' same as or supertype of 'type'
	 */
	public abstract boolean isAssignableFrom(Type type);

	@Override
	public boolean equals(Object o) {
		// Every Type is distinct. Use isAssignableFrom to check if type can be casted implicitly
		if (this == o) return true;
		if (o instanceof Type t) {
			return id.equals(t.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
