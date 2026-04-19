package nodes;

import interpreter.Context;
import results.RunResult;
import values.Value;

public class SetVarNode extends Node {
    private final String name;
    private final Node value;

    public SetVarNode(String name, Node value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        Value value = res.register(this.value.execute(ctx));
        if (!res.hasError())
            return res;

        ctx.setVariable(name, value);

        return res.success(value);
    }
}
