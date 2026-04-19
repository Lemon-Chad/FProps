package values;

import nodes.Node;
import results.RunResult;
import stdlib.BooleanLogicModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LogicModel {
    private final String name;
    private final Map<String, ModelSymbol> symbols;
    private final Map<String, ModelOperator> operators;

    public LogicModel(String name) {
        this.name = name;
        symbols = new HashMap<>();
        operators = new HashMap<>();

        // default comparison operator
        defineOperator("is", (left, right) ->
                new RunResult(BooleanLogicModel.Evaluate(left.equals(right))));
    }

    public ModelSymbol defineSymbol(String symbol) {
        ModelSymbol s = new ModelSymbol(this, symbol);
        symbols.put(symbol, s);
        return s;
    }

    public ModelOperator defineOperator(String name, boolean binary, Node body) {
        return defineOperator(new ModelOperator(this, name, binary, body));
    }

    public ModelOperator defineOperator(String name, boolean binary, OperatorBody body) {
        return defineOperator(new ModelOperator(this, name, binary, body));
    }

    public ModelOperator defineOperator(String name, OperatorBody.Unary unary) {
        return defineOperator(name, false, OperatorBody.Unary(unary));
    }

    public ModelOperator defineOperator(String name, OperatorBody.Binary binary) {
        return defineOperator(name, true, OperatorBody.Binary(binary));
    }

    public ModelOperator defineOperator(ModelOperator operator) {
        operators.put(operator.getName(), operator);
        return operator;
    }

    public ModelSymbol getSymbol(String name) {
        return symbols.get(name);
    }

    public ModelOperator getOperator(String name) {
        return operators.get(name);
    }

    @Override
    public String toString() {
        return String.format("[%s]", name);
    }

    public String getName() {
        return name;
    }

    public Set<ModelSymbol> getSymbols() {
        return new HashSet<>(symbols.values());
    }
}
