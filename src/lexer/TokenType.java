package lexer;

public enum TokenType {
    SYMBOL,
    VARIABLE_INDICATOR("'"),
    LBRACKET("["),
    RBRACKET("]"),
    LBRACE("{"),
    RBRACE("}"),
    LPAREN("("),
    RPAREN(")"),
    SEMICOLON(";"),
    COLON(":"),
    COMMA(","),
    EQ("="),
    DOT("."),
    LVAL("<"),
    RVAL(">");

    private final String defaultVal;

    TokenType() {
        this.defaultVal = null;
    }

    TokenType(String val) {
        this.defaultVal = val;
        Token.KNOWN.put(val, this);
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    @Override
    public String toString() {
        return defaultVal == null ? name() : defaultVal;
    }
}
