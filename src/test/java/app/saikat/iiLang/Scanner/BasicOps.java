package app.saikat.iiLang.Scanner;

import app.saikat.iiLang.DummyReporter;
import app.saikat.iiLang.ast.interfaces.Stmt;
import app.saikat.iiLang.interfaces.CmdlineOptions;
import app.saikat.iiLang.parser.Parser;
import app.saikat.iiLang.parser.Scanner;
import app.saikat.iiLang.parser.Token;
import org.junit.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class BasicOps {

    DummyReporter reporter = new DummyReporter();

    @Test
    public void expressions() {

        CmdlineOptions.selectedOptions = EnumSet.of(CmdlineOptions.DUMP_TOKENS);
        CmdlineOptions.selectedOptions = EnumSet.of(CmdlineOptions.DUMP_AST);

        Scanner scanner = new Scanner("1 + 2;", reporter);
        List<Token> tokens = scanner.scanTokens();
        assert reporter.getErrors().size() == 0;
        System.out.println(Arrays.toString(reporter.getDebugs().toArray()));

        Parser parser = new Parser(tokens, reporter);
        List<Stmt> statements = parser.parse();
        assert reporter.getErrors().size() == 0;
        System.out.println(Arrays.toString(reporter.getDebugs().toArray()));
    }
}
