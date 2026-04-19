package nodes;

import interpreter.Context;
import results.RunResult;
import values.Formula;
import values.Value;

public class FormulaNode extends Node {
    private final String name;
    private final Formula value;

    public FormulaNode(String name, Formula value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        value.setCtx(ctx);

        ctx.setVariable(name, new Value(value));

        return res.success(new Value(value));
    }
}
