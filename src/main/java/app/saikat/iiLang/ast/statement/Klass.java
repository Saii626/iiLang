package app.saikat.iiLang.ast.statement;

import java.util.List;

import app.saikat.iiLang.ast.datatypes.ClassType;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.ast.interfaces.StmtVisitor;
import app.saikat.iiLang.parser.Token;

public class Klass implements Stmt {

	public Klass(Token name, Klass superclass, List<Var> fields) {
		this.name = name;
		this.type = new ClassType(name.getLexeme(), this);
		this.superclass = superclass;
		this.fields = fields;
	}

	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitClassStmt(this);
	}

	final Token name;
	final Klass superclass;
	final List<Var> fields;
	final ClassType type;

	public Token getName() {
		return name;
	}

	public Klass getSuperclass() {
		return superclass;
	}

	public List<Var> getFields() {
		return fields;
	}

	public ClassType getType() {
		return type;
	}

	
}
