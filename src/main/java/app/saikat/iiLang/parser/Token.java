//> Scanning token-class
package app.saikat.iiLang.parser;

import app.saikat.iiLang.ast.interfaces.CodeLocation;
import app.saikat.iiLang.parser.interfaces.TokenType;

public record Token(TokenType type, String lexeme, Object literal,
                    CodeLocation codeLocation) {}
