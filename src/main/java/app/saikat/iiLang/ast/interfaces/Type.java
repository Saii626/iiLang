package app.saikat.iiLang.ast.interfaces;

public interface Type {

	<R> R accept(TypeVisitor<R> visitor);

	/**
	* String representation of the type
	* @return string
	 */
	String typeName();

	/**
	* Checks if this type can be assigned from another type
	* @param type type to compare against
	* @return true iff 'this' same as or supertype of 'type'
	 */
	boolean isAssignableFrom(Type type);
}
