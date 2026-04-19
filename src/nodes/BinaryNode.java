package nodes;

import interpreter.Context;
import results.RunResult;
import values.ModelOperator;
import values.ModelSymbol;
import values.Value;
import values.ValueType;

public class BinaryNode extends Node {
    private final String operator;
    private final Node left;
    private final Node right;

    public BinaryNode(String operator, Node left, Node right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        Value left = res.register(this.left.execute(ctx));
        if (res.hasError())
            return res;

        if (!left.is(ValueType.SYMBOL))
            return res.failure("Can only operate on symbols of a model.");

        Value right = res.register(this.right.execute(ctx));
        if (res.hasError())
            return res;

        if (!right.is(ValueType.SYMBOL))
            return res.failure("Can only operate on symbols of a model.");

        ModelSymbol lSymbol = left.asSymbol();
        ModelSymbol rSymbol = right.asSymbol();
        if (!lSymbol.getModel().equals(rSymbol.getModel()))
            return res.failure(String.format(
                    "Can only operate on symbols of the same model. ('%s' : '%s')",
                    lSymbol, rSymbol
            ));

        ModelOperator operator = lSymbol.getModel().getOperator(this.operator);
        if (operator == null || !operator.isBinary())
            return res.failure(String.format("Binary operator '%s' does not exist in model '%s'.",
                    this.operator, lSymbol.getModel().toString()));

        return operator.execute(lSymbol, rSymbol);
    }
}
