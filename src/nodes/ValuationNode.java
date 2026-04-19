package nodes;

import interpreter.Context;
import results.RunResult;
import values.Value;
import values.ValueType;

public class ValuationNode extends Node {
    private final Node formula;
    private final Node[] valuation;

    public ValuationNode(Node formula, Node[] valuation) {
        this.formula = formula;
        this.valuation = valuation;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        Value formula = res.register(this.formula.execute(ctx));
        if (res.hasError())
            return res;
        if (!formula.is(ValueType.FORMULA))
            return res.failure("Expected formula.");

        return formula.asFormula().execute(ctx, valuation);
    }
}
