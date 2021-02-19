package app.saikat.iiLang.datatypes.SystemDefinedTypes.Templates;

import app.saikat.iiLang.datatypes.ClassTemplateType;
import app.saikat.iiLang.datatypes.ClassType;
import app.saikat.iiLang.datatypes.SystemDefinedTypes.ListClass;
import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.parser.interfaces.Parser;
import app.saikat.iiLang.parser.interfaces.Scope;
import app.saikat.iiLang.utils.Either;

import java.util.List;

public class ListTemplateClass extends ClassTemplateType {

    public ListTemplateClass(Parser parser) {
        super("list", List.of("T"), null, parser);
    }

    @Override
    public ListClass getTypeForGenericArg(List<List<Token>> typeArgs, Scope scope) {
        assert (typeArgs.size() == 1);
        Type type = parser.parseType(typeArgs.get(0), scope);
        assert (type != null);

        List<Either<Type, Literal>> key = List.of(Either.left(type));
        if (generatedTypeInstances.containsKey(key)) {
            return (ListClass)generatedTypeInstances.get(key);
        } else {
            ListClass listClass = new ListClass("list("+type.typeName()+")", type);
            generatedTypeInstances.put(key, listClass);
            return listClass;
        }
    }

    @Override
    protected ClassType constructTypeFrom(List<Token> tokens, Scope scope) {
        return null;
    }
}
