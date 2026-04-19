package nodes;

import interpreter.Context;
import results.RunResult;
import values.Value;

public class GetVarNode extends Node {
    private final String name;

    public GetVarNode(String name) {
        this.name = name;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        Value v = ctx.getVariable(name);
        if (v == null)
            return res.lookupFailure(String.format("No variable '%s' defined.", name));

        return res.success(v);
    }
}
