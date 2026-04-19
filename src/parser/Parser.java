package parser;

import lexer.Token;
import lexer.TokenType;
import nodes.*;
import results.ParseResult;
import values.Formula;
import values.LogicModel;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private final int length;
    private int idx;
    private Token current;
    private Token previous;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.length = tokens.size();
        this.idx = -1;
        advance();
    }

    public ParseResult parse() {
        ParseResult res = new ParseResult();
        List<Node> body = new ArrayList<>();

        while (idx < length && !res.hasError())
            body.add(res.register(statement(true)));

        if (res.hasError())
            return res;

        return res.success(new BodyNode(body.toArray(new Node[0])));
    }

    public ParseResult statement(boolean terminates) {
        ParseResult res = new ParseResult();
        // test for
        // - if statement
        // - model declaration
        // - formula declaration
        // - variable declaration
        // - return statement
        // - print statement

        Node statement;
        if (tryConsume("if")) {
            // if (
            if (res.fails(consume(TokenType.LPAREN)))
                return res;

            // if (condition
            Node condition = res.register(expression());
            if (res.hasError())
                return res;

            // if (condition)
            if (res.fails(consume(TokenType.RPAREN)))
                return res;

            // if (condition) statement;
            Node ifTrue = res.register(statement(true));
            if (res.hasError())
                return res;

            // if (condition) statement; else? statement;
            Node ifFalse = null;
            if (tryConsume("else")) {
                ifFalse = res.register(statement(true));
                if (res.hasError())
                    return res;
            }

            statement = new IfNode(condition, ifTrue, ifFalse);
            return res.success(statement);
        } else if (tryConsume("model")) {
            if (res.fails(consume(TokenType.LBRACKET)))
                return res;

            if (res.fails(consume(TokenType.SYMBOL)))
                return res;
            String modelName = previous.getVal();
            LogicModel model = new LogicModel(modelName);

            if (res.fails(consume(TokenType.RBRACKET)))
                return res;
            if (res.fails(consume(TokenType.LBRACE)))
                return res;

            while (tryConsume(TokenType.SYMBOL)) {
                String keyword = previous.getVal();
                switch (keyword) {
                    case "symbols": {
                        if (res.fails(consume(TokenType.LBRACE)))
                            return res;

                        // read set of symbols
                        if (tryPeek(TokenType.SYMBOL)) do {
                            if (res.fails(consume(TokenType.SYMBOL)))
                                return res;
                            model.defineSymbol(previous.getVal());
                        } while (tryConsume(TokenType.COMMA));

                        if (res.fails(consume(TokenType.RBRACE)))
                            return res;
                        break;
                    }

                    case "bin": {
                        if (res.fails(consume(TokenType.SYMBOL)))
                            return res;

                        String operatorName = previous.getVal();

                        Node body = res.register(body());
                        if (res.hasError())
                            return res;
                        model.defineOperator(operatorName, true, body);
                        break;
                    }

                    case "un": {
                        if (res.fails(consume(TokenType.SYMBOL)))
                            return res;

                        String operatorName = previous.getVal();

                        Node body = res.register(body());
                        if (res.hasError())
                            return res;
                        model.defineOperator(operatorName, false, body);
                        break;
                    }

                    default:
                        return res.failure(String.format("Unknown keyword '%s'.", keyword), previous);
                }
            }

            if (res.fails(consume(TokenType.RBRACE)))
                return res;

            return res.success(new SetModelNode(model));
        } else if (tryConsume("form")) {
            // form name<'a, 'b, 'c, ...> = statement;
            if (res.fails(consume(TokenType.SYMBOL)))
                return res;

            String formulaName = previous.getVal();

            if (res.fails(consume(TokenType.LVAL)))
                return res;

            // consume variable names
            List<String> variables = new ArrayList<>();
            if (tryPeek(TokenType.VARIABLE_INDICATOR)) do {
                if (res.fails(consume(TokenType.VARIABLE_INDICATOR)))
                    return res;
                if (res.fails(consume(TokenType.SYMBOL)))
                    return res;

                // 'symbol
                variables.add(previous.getVal());
            } while (idx < length && tryConsume(TokenType.COMMA));

            if (res.fails(consume(TokenType.RVAL)))
                return res;

            if (res.fails(consume(TokenType.EQ)))
                return res;

            Node body = res.register(statement(true));
            if (res.hasError())
                return res;
            return res.success(new FormulaNode(formulaName, new Formula(variables.toArray(new String[0]), body)));
        } else if (tryConsume("let")) {
            // let '
            if (res.fails(consume(TokenType.VARIABLE_INDICATOR)))
                return res;

            // let 'name
            if (res.fails(consume(TokenType.SYMBOL)))
                return res;

            String variableName = previous.getVal();

            // let 'name =
            if (res.fails(consume(TokenType.EQ)))
                return res;

            Node variableValue = res.register(this.expression());
            if (res.hasError())
                return res;

            statement = new SetVarNode(variableName, variableValue);
        } else if (tryConsume("return")) {
            Node returnValue = res.register(this.expression());
            if (res.hasError())
                return res;
            statement = new ReturnNode(returnValue);
        } else if (tryPeek(TokenType.LBRACE)) {
            return body();
        } else if (tryConsume("print")) {
            Node expression = res.register(this.expression());
            statement = new PrintNode(expression);
        } else {
            statement = res.register(this.expression());
            if (res.hasError())
                return res;
        }

        if (terminates) {
            res.register(consume(TokenType.SEMICOLON));
            if (res.hasError())
                return res;
        }

        return res.success(statement);
    }

    private ParseResult expression() {
        // parse for
        // - body
        // - forall
        // - exists
        // - using [Model]

        if (tryPeek(TokenType.LBRACE)) {
            return body();
        } else if (tryConsume("forall") || tryConsume("exists")) {
            ParseResult res = new ParseResult();

            boolean isForall = previous.getVal().equals("forall");

            if (res.fails(consume(TokenType.VARIABLE_INDICATOR)))
                return res;
            if (res.fails(consume(TokenType.SYMBOL)))
                return res;
            String variable = previous.getVal();

            Node expression = res.register(this.expression());
            return res.success(isForall ? new ForallNode(variable, expression) : new ExistsNode(variable, expression));
        } else if (tryConsume("using")) {
            ParseResult res = new ParseResult();
            Node model = res.register(this.atom());
            if (res.hasError())
                return res;

            Node expression = res.register(this.expression());
            return res.success(new UsingNode(model, expression));
        }

        return this.unary();
    }

    private ParseResult unary() {
        ParseResult res = new ParseResult();

        if (tryConsume(TokenType.SYMBOL)) {
            String unaryOperator = previous.getVal();

            Node operand = res.register(this.unary());
            if (res.hasError())
                return res;
            return res.success(new UnaryNode(unaryOperator, operand));
        }

        Node binary = res.register(this.binary());
        if (res.hasError())
            return res;
        return res.success(binary);
    }

    private ParseResult binary() {
        ParseResult res = new ParseResult();

        Node left = res.register(this.valuation());

        // eat all operators
        while (tryConsume(TokenType.SYMBOL)) {
            String operator = previous.getVal();
            Node right = res.register(this.valuation());
            if (res.hasError())
                return res;
            left = new BinaryNode(operator, left, right);
        }

        return res.success(left);
    }

    private ParseResult valuation() {
        ParseResult res = new ParseResult();

        Node index = res.register(this.index());
        if (res.hasError())
            return res;

        if (tryConsume(TokenType.LVAL)) {
            List<Node> valuation = new ArrayList<>();

            // read valuation
            if (!tryPeek(TokenType.RVAL)) do {
                Node val = res.register(this.expression());
                if (res.hasError())
                    return res;
                valuation.add(val);
            } while (idx < length && tryConsume(TokenType.COMMA));

            if (res.fails(consume(TokenType.RVAL)))
                return res;

            return res.success(new ValuationNode(index, valuation.toArray(new Node[0])));
        }

        return res.success(index);
    }

    private ParseResult index() {
        ParseResult res = new ParseResult();

        Node atom = res.register(this.atom());
        if (res.hasError())
            return res;

        // indexing model
        if (tryConsume(TokenType.DOT)) {
            if (res.fails(consume(TokenType.SYMBOL)))
                return res;

            String symbolName = previous.getVal();
            return res.success(new IndexModelNode(atom, symbolName));
        }

        return res.success(atom);
    }

    private ParseResult atom() {
        ParseResult res = new ParseResult();
        if (tryConsume(TokenType.LPAREN)) {
            Node node = res.register(expression());
            if (res.hasError())
                return res;
            if (res.fails(consume(TokenType.RPAREN)))
                return res;
            return res.success(node);
        } else if (tryConsume(TokenType.LBRACKET)) {
            if (res.fails(consume(TokenType.SYMBOL)))
                return res;

            String modelName = previous.getVal();

            if (res.fails(consume(TokenType.RBRACKET)))
                return res;

            return res.success(new GetModelNode(modelName));
        } else if (tryConsume(TokenType.VARIABLE_INDICATOR)) {
            if (res.fails(consume(TokenType.SYMBOL)))
                return res;

            String varName = previous.getVal();
            return res.success(new GetVarNode(varName));
        } else if (tryConsume(TokenType.DOT)) {
            if (res.fails(consume(TokenType.SYMBOL)))
                return res;

            String symbolName = previous.getVal();
            return res.success(new IndexModelNode(null, symbolName));
        }
        return res.failure("Unknown expression.", current);
    }

    private ParseResult body() {
        ParseResult res = new ParseResult();

        // consume {
        if (res.fails(consume(TokenType.LBRACE)))
            return res;

        // eat up statements
        List<Node> body = new ArrayList<>();

        while (idx < length && !res.hasError() && !current.getType().equals(TokenType.RBRACE))
            body.add(res.register(statement(true)));

        if (res.hasError())
            return res;

        // consume }
        if (res.fails(consume(TokenType.RBRACE)))
            return res;

        return res.success(new BodyNode(body.toArray(new Node[0])));
    }

    public void advance() {
        idx++;

        if (current != null)
            previous = current;

        if (idx < length)
            current = tokens.get(idx);
    }

    public ParseResult expect(TokenType tok) {
        ParseResult res = new ParseResult();
        if (idx >= length)
            return res.failure(String.format("Expected '%s', got EOF.", tok.toString()), current);

        if (!current.getType().equals(tok))
            return res.failure(
                    String.format("Expected '%s', got '%s'.", current.getVal(), tok.toString()), current);

        return res.success();
    }

    public ParseResult consume(TokenType tok) {
        ParseResult res = new ParseResult();

        res.register(expect(tok));

        if (!res.hasError())
            advance();

        return res;
    }

    public ParseResult expect(String word) {
        ParseResult res = new ParseResult();
        if (idx >= length)
            return res.failure(String.format("Expected '%s', got EOF.", word), current);

        if (!current.getVal().equals(word))
            return res.failure(
                    String.format("Expected '%s', got '%s'.", current.getVal(), word), current);

        return res.success();
    }

    public ParseResult consume(String word) {
        ParseResult res = new ParseResult();

        res.register(expect(word));

        if (!res.hasError())
            advance();

        return res;
    }

    public boolean tryConsume(String word) {
        if (idx < length && current.getVal().equals(word)) {
            advance();
            return true;
        }

        return false;
    }

    public boolean tryConsume(TokenType tok) {
        if (idx < length && current.getType().equals(tok)) {
            advance();
            return true;
        }

        return false;
    }

    public boolean tryPeek(String word) {
        return idx < length && current.getVal().equals(word);
    }

    public boolean tryPeek(TokenType tok) {
        return idx < length && current.getType().equals(tok);
    }
}
