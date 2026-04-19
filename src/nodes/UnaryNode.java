package nodes;

import interpreter.Context;
import results.RunResult;
import values.ModelOperator;
import values.ModelSymbol;
import values.Value;
import values.ValueType;

public class UnaryNode extends Node {
    private final String operator;
    private final Node operand;

    public UnaryNode(String operator, Node operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        Value operand = res.register(this.operand.execute(ctx));
        if (res.hasError())
            return res;

        if (!operand.is(ValueType.SYMBOL))
            return res.failure("Can only operate on symbols of a model.");

        ModelSymbol symbol = operand.asSymbol();
        ModelOperator operator = symbol.getModel().getOperator(this.operator);
        if (operator == null || operator.isBinary())
            return res.failure(String.format("Unary operator '%s' does not exist in model '%s'.",
                    this.operator, symbol.getModel().toString()));

        return operator.execute(symbol);
    }
}
