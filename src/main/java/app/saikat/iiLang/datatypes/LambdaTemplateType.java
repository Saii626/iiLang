package app.saikat.iiLang.datatypes;

import app.saikat.iiLang.datatypes.interfaces.Type;
import app.saikat.iiLang.parser.interfaces.Parser;
import app.saikat.iiLang.parser.interfaces.Scope;
import app.saikat.iiLang.datatypes.interfaces.TemplateType;
import app.saikat.iiLang.parser.Token;

import java.util.List;

public class LambdaTemplateType extends TemplateType<LambdaType> {

    public LambdaTemplateType(String typeName, List<String> templateParams, List<Token> typeTemplate, Parser parser) {
        super(typeName, templateParams, typeTemplate, parser);
    }

    @Override
    protected LambdaType constructTypeFrom(List<Token> tokens, Scope scope) {
        return null;
    }
}
