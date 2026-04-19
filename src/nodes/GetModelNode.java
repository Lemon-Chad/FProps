package nodes;

import interpreter.Context;
import results.RunResult;
import values.LogicModel;
import values.Value;

public class GetModelNode extends Node {
    private final String name;

    public GetModelNode(String name) {
        this.name = name;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        LogicModel v = ctx.getModel(name);
        if (v == null)
            return res.failure(String.format("No model '%s' defined.", name));

        return res.success(new Value(v));
    }
}
