package lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private int idx;
    private final String text;
    private final int len;

    private int line;
    private int col;

    public Lexer(String text) {
        this.text = text;
        this.len = text.length();
        this.line = 1;
        this.col = 1;
    }

    public List<Token> lex() {
        List<Token> tokens = new ArrayList<>();
        while (skipWhitespace())
            tokens.add(next());
        return tokens;
    }

    public Token next() {
        char c = text.charAt(idx);

        if (Token.KNOWN.containsKey(String.valueOf(c))) {
            Token tok = new Token(Token.KNOWN.get(String.valueOf(c)), line, col);
            advance();
            return tok;
        }

        // if [a-zA-Z0-9_], make symbol
        if (Character.isLetterOrDigit(c) || c == '_') {
            StringBuilder symbol = new StringBuilder();
            symbol.append(c);

            int startLine = line;
            int startCol = col;

            advance();

            while (idx < len) {
                char ch = text.charAt(idx);
                if (Character.isLetterOrDigit(ch) || ch == '_') {
                    symbol.append(ch);
                    advance();
                    continue;
                }

                break;
            }

            return new Token(symbol.toString(), TokenType.SYMBOL, startLine, startCol);
        }

        System.err.printf("Unknown character '%c' at %d:%d.\n", c, line, col);
        System.exit(1);
        return null;
    }

    public boolean skipWhitespace() {
        while (idx < len) {
            char c = text.charAt(idx);

            // skip normal whitespace
            if (Character.isWhitespace(c)) {
                advance();
                continue;
            }

            // if it is a comment, skip until newline
            if (c == '#') {
                do advance();
                while (idx < len && text.charAt(idx) != '\n');
                continue;
            }

            // neither whitespace nor comment so break
            break;
        }

        // return whether or not is EOF
        return idx < len;
    }

    public int advance() {
        if (idx < len) {
            char c = text.charAt(idx);
            if (c == '\n') {
                line++;
                col = 1;
            } else {
                col++;
            }
        }

        return idx++;
    }
}
