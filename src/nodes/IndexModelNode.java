package nodes;

import interpreter.Context;
import results.RunResult;
import values.LogicModel;
import values.ModelSymbol;
import values.Value;
import values.ValueType;

public class IndexModelNode extends Node {
    private final Node model;
    private final String symbol;

    public IndexModelNode(Node model, String symbol) {
        this.model = model;
        this.symbol = symbol;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        LogicModel model = ctx.getModel();
        if (this.model != null) {
            Value v = res.register(this.model.execute(ctx));
            if (res.hasError())
                return res;
            if (!v.is(ValueType.MODEL))
                return res.failure("Can only index symbols on models.");
            model = v.asModel();
        }

        ModelSymbol symbol = model.getSymbol(this.symbol);
        if (symbol == null)
            return res.failure(String.format("Symbol '%s' not defined on model '%s'.", this.symbol, model));

        return res.success(new Value(symbol));
    }
}
