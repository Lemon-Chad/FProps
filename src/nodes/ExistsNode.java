package nodes;

import interpreter.Context;
import results.RunResult;
import stdlib.BooleanLogicModel;
import values.ModelSymbol;
import values.Value;
import values.ValueType;

import java.util.Set;

public class ExistsNode extends Node {
    private final String variable;
    private final Node expression;

    public ExistsNode(String variable, Node expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        Set<ModelSymbol> symbols = ctx.getModel().getSymbols();
        for (ModelSymbol symbol : symbols) {
            Context subCtx = new Context(ctx.getModel(), ctx)
                    .setVariable(variable, new Value(symbol));

            Value val = res.register(expression.execute(subCtx));
            if (res.hasError())
                return res;
            if (!(val.is(ValueType.SYMBOL) && val.asSymbol().getModel().equals(BooleanLogicModel.get())))
                return res.failure("Exists body does not evaluate to a boolean value.");

            // satisfication found. return true.
            if (val.asSymbol().equals(BooleanLogicModel.True()))
                return res.success(new Value(BooleanLogicModel.True()));
        }

        // no satisfying element found. return false.
        return res.success(new Value(BooleanLogicModel.False()));
    }
}
