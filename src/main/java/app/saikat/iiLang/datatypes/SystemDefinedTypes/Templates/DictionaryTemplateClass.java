package app.saikat.iiLang.datatypes.SystemDefinedTypes.Templates;

import app.saikat.iiLang.datatypes.ClassTemplateType;
import app.saikat.iiLang.datatypes.ClassType;
import app.saikat.iiLang.datatypes.SystemDefinedTypes.DictionaryClass;
import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.parser.interfaces.Parser;
import app.saikat.iiLang.parser.interfaces.Scope;
import app.saikat.iiLang.utils.Either;

import java.util.List;

public class DictionaryTemplateClass extends ClassTemplateType {

    public DictionaryTemplateClass(Parser parser) {
        super("dict", List.of("K", "V"), null, parser);
    }

    @Override
    public DictionaryClass getTypeForGenericArg(List<List<Token>> typeArgs, Scope scope) {
        assert (typeArgs.size() == 2);
        Type keyType = parser.parseType(typeArgs.get(0), scope);
        Type valType = parser.parseType(typeArgs.get(1), scope);
        assert (keyType != null);
        assert (valType != null);

        List<Either<Type, Literal>> key = List.of(Either.left(keyType), Either.left(valType));
        if (generatedTypeInstances.containsKey(key)) {
            return (DictionaryClass)generatedTypeInstances.get(key);
        } else {
            DictionaryClass dictionaryClass = new DictionaryClass(createTypename(keyType, valType), keyType, valType);
            generatedTypeInstances.put(key, dictionaryClass);
            return dictionaryClass;
        }
    }

    @Override
    protected ClassType constructTypeFrom(List<Token> tokens, Scope scope) {
        return null;
    }

    private static String createTypename(Type key, Type val) {
        return "dict(" + key.typeName() + "," + val.typeName() + ")";
    }
}
