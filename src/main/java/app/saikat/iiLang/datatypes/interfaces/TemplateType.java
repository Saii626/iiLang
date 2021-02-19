package app.saikat.iiLang.datatypes.interfaces;

import app.saikat.iiLang.datatypes.SystemDefinedTypes.Primitive;
import app.saikat.iiLang.parser.Token;
import app.saikat.iiLang.parser.interfaces.TokenType;
import app.saikat.iiLang.parser.interfaces.Parser;
import app.saikat.iiLang.parser.interfaces.Scope;
import app.saikat.iiLang.utils.Either;

import java.util.*;

public abstract class TemplateType<T extends Type> {

    protected final String typeName;
    protected final List<String> templateParams;
    protected List<Token> typeTemplate;
    protected final Parser parser;

    protected TemplateType(String typeName, List<String> templateParams, List<Token> typeTemplate, Parser parser) {
        this.typeName = typeName;
        this.templateParams = templateParams;
        this.typeTemplate = typeTemplate;
        this.parser = parser;
    }

    private List<Token> replaceGenericArgs(List<List<Token>> genericArgs) {
        assert (genericArgs.size() == templateParams.size());

        Map<String, List<Token>> replaceDict = new HashMap<>();
        for (int i = 0; i < genericArgs.size(); i++) {
            replaceDict.put(templateParams.get(i), genericArgs.get(i));
        }

        LinkedList<Token> replacedTemplate = new LinkedList<>();
        for (Token templateToken : typeTemplate) {
            if (templateToken.type() == TokenType.IDENTIFIER && replaceDict.containsKey(templateToken.lexeme())) {
                replacedTemplate.addAll(replaceDict.get(templateToken.lexeme()));
            } else {
                replacedTemplate.add(templateToken);
            }
        }

        return replacedTemplate;
    }

    public String getTypeName() {
        return typeName;
    }

    /**
     * Returns a already created type, or creates a new one if not already created
     * @param typeArgs arguments to generic parameters
     * @param scope where the type is being instantiated
     * @return concrete type of this template type
     */
    @SuppressWarnings("unchecked")
    public T getTypeForGenericArg(List<List<Token>> typeArgs, Scope scope) {
        assert (typeArgs.size() == templateParams.size());

        List<Either<Type, Literal>> generatedInstancesKey = new ArrayList<>();
        for (List<Token> tok : typeArgs) {
            assert(tok.size() > 0);
            boolean isLiteral = TokenType.SYSTEM_DEFINED_DATA.contains(tok.get(0).type());
            if (isLiteral) {
                generatedInstancesKey.add(Either.right(new Literal(Primitive.getType(tok.get(0).type()), tok.get(0))));
            } else {
                Type type = parser.parseType(tok, scope);
                if (type == null) {
                    throw new RuntimeException("Generic arg is neither a type or a literal");
                }
                generatedInstancesKey.add(Either.left(type));
            }
        }

        if (generatedTypeInstances.containsKey(generatedInstancesKey)) {
            return (T)generatedTypeInstances.get(typeArgs);
        } else {
            List<Token> replacedTemplate = replaceGenericArgs(typeArgs);
            Type t = constructTypeFrom(replacedTemplate, scope);
            generatedTypeInstances.put(generatedInstancesKey, t);
            return (T)t;
        }
    }

    protected abstract Type constructTypeFrom(List<Token> tokens, Scope scope);

    protected static record Literal(Type type, Object val) {};
    protected static Map<List<Either<Type, Literal>>, Type> generatedTypeInstances = new HashMap<>();
}
