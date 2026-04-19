package lexer;

import java.util.HashMap;
import java.util.Map;

public class Token {
    public static final Map<String, TokenType> KNOWN = new HashMap<>();

    private final String val;
    private final TokenType type;
    private final int line;
    private final int col;

    public Token(TokenType type, int line, int col) {
        this(type.getDefaultVal(), type, line, col);
    }

    public Token(String val, TokenType type, int line, int col) {
        this.val = val;
        this.type = type;
        this.line = line;
        this.col = col;
    }

    public String getVal() {
        return val;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("[%s_TOKEN:`%s`]", type.toString(), val);
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }
}
