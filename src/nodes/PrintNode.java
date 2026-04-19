package nodes;

import interpreter.Context;
import results.RunResult;
import values.Value;

public class PrintNode extends Node {
    private final Node val;

    public PrintNode(Node val) {
        this.val = val;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        Value v = res.register(val.execute(ctx));
        if (res.hasError())
            return res;

        System.out.println(v.toString());

        return res.success(v);
    }
}
