import interpreter.Context;
import lexer.Lexer;
import lexer.Token;
import nodes.Node;
import parser.Parser;
import results.ParseResult;
import results.RunResult;
import stdlib.BooleanLogicModel;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: fprop <file>");
            System.exit(1);
        }

        String path = args[0];
        String file = Files.readString(Path.of(path), Charset.defaultCharset());

        Lexer lexer = new Lexer(file);
        List<Token> tokens = lexer.lex();

        Parser parser = new Parser(tokens);

        ParseResult parseResult = parser.parse();
        if (parseResult.hasError()) {
            parseResult.printError();
            System.exit(1);
        }
        Node ast = parseResult.getNode();

        Context globalCtx = new Context();
        globalCtx.declareModel(BooleanLogicModel.get());
        RunResult res = ast.execute(globalCtx);

        if (res.hasError())
            System.err.printf("Error: %s\n", res.getError());
    }
}