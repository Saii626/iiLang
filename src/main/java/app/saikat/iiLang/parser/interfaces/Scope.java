package app.saikat.iiLang.parser.interfaces;

import app.saikat.iiLang.ast.expression.Variable;
import app.saikat.iiLang.datatypes.ClassType;
import app.saikat.iiLang.datatypes.interfaces.TemplateType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Scope {

    private final Scope parent;
    private final Map<String, ClassType> definedTypes;
    private final Map<String, Variable> variables;
    private final Map<String, TemplateType<?>> templateTypes;

    public Scope(Scope parent) {
        this.parent = parent;
        this.definedTypes = new HashMap<>();
        this.variables = new HashMap<>();
        this.templateTypes = new HashMap<>();
    }

    public Map<String, Variable> getVariables() {
        return variables;
    }

    public Map<String, ClassType> getDefinedTypes() {
        return definedTypes;
    }

    public Map<String, TemplateType<?>> getTemplateTypes() {
        return templateTypes;
    }

    public void addVariable(Variable variable) {
        variables.put(variable.getName(), variable);
    }

    public void addClassType(ClassType classType) {
        definedTypes.put(classType.typeName(), classType);
    }

    public void addTemplateType(TemplateType<?> templateType) {
        templateTypes.put(templateType.getTypeName(), templateType);
    }

    public boolean isInside(Scope scope) {
        Scope s = this.parent;
        while (s != null) {
            if (s == scope) {
                return true;
            } else {
                s = s.parent;
            }
        }
        return false;
    }

    private <T> T search(Function<Scope, Map<String, T>> mapToSearch, String toSearch) {
        Scope s = this;
        Map<String, T> map;
        do {
            map = mapToSearch.apply(s);
            if (map.containsKey(toSearch)) {
                return map.get(toSearch);
            } else {
                s = s.parent;
            }
        } while  (s != null);
        return null;
    }

    public Variable searchVariable(String name) {
        return search(s -> s.variables, name);
    }

    public ClassType searchClassType(String name) {
        return search(s -> s.definedTypes, name);
    }

    public TemplateType<?> searchTypeTemplate(String name) {
        return search(s -> s.templateTypes, name);
    }

    public Scope cloneScope() {
        Scope s = new Scope(parent == null ? null : parent.cloneScope());
        s.definedTypes.putAll(definedTypes);
        s.variables.putAll(variables);
        s.templateTypes.putAll(templateTypes);
        return s;
    }
}
