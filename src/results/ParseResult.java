package results;

import lexer.Token;
import nodes.Node;

public class ParseResult {
    private Node node;

    private String error;
    private Token errorToken;

    public ParseResult() {
        this.node = null;
        this.error = null;
        this.errorToken = null;
    }

    public ParseResult success(Node node) {
        this.node = node;
        this.error = null;
        this.errorToken = null;
        return this;
    }

    public ParseResult failure(String msg, Token tok) {
        this.node = null;
        this.error = msg;
        this.errorToken = tok;
        return this;
    }

    public Node register(ParseResult res) {
        if (res.hasError()) {
            error = res.error;
            errorToken = res.errorToken;
            return null;
        }

        node = res.node;
        return node;
    }

    public Node getNode() {
        return node;
    }

    public boolean hasError() {
        return error != null;
    }

    public void printError() {
        if (errorToken == null)
            System.err.printf("Error@(??:??) :: %s\n", error);
        else
            System.err.printf("Error@(%d:%d) :: %s\n", errorToken.getLine(), errorToken.getCol(), error);
    }

    public ParseResult success() {
        return success(null);
    }

    public boolean fails(ParseResult res) {
        register(res);
        return hasError();
    }
}
