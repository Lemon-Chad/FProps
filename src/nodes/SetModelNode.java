package nodes;

import interpreter.Context;
import results.RunResult;
import values.LogicModel;
import values.Value;

public class SetModelNode extends Node {
    private final LogicModel model;

    public SetModelNode(LogicModel model) {
        this.model = model;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        ctx.declareModel(model);

        return res.success(new Value(model));
    }
}
