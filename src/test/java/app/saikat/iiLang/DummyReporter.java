package app.saikat.iiLang;

import app.saikat.iiLang.interfaces.Reporter;
import app.saikat.iiLang.parser.Token;

import java.util.LinkedList;
import java.util.List;

public class DummyReporter implements Reporter {

    public static class Error {
        String msg;
        int lineno;
        int charno;
        Token token;

        Error(String msg, Token token) {
            this.msg = msg;
            this.token = token;
        }

        Error(String msg, int lineno, int charno) {
            this.msg = msg;
            this.lineno = lineno;
            this.charno = charno;
        }
    }

    public static class Debug {
        String tag;
        String msg;

        Debug(String tag, String msg) {
            this.tag = tag;
            this.msg = msg;
        }
    }

    private final List<Error> errors = new LinkedList<>();
    private final List<Debug> debugs = new LinkedList<>();

    @Override
    public void reportError(String msg, int lineno, int charno) {
        errors.add(new Error(msg, lineno,charno));
    }

    @Override
    public void reportError(String msg, Token token) {
        errors.add(new Error(msg, token));
    }

    @Override
    public void debugInfo(String tag, String info) {
        debugs.add(new Debug(tag, info));
    }

    public List<Error> getErrors() {
        return errors;
    }

    public List<Debug> getDebugs() {
        return debugs;
    }

    public void clear() {
        errors.clear();
        debugs.clear();
    }
}
