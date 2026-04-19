package nodes;

import interpreter.Context;
import results.RunResult;

public class BodyNode extends Node {
    private final Node[] nodes;

    public BodyNode(Node[] nodes) {
        this.nodes = nodes;
    }

    @Override
    public RunResult execute(Context ctx) {
        return execute(ctx, true);
    }

    public RunResult execute(Context ctx, boolean createContext) {
        RunResult res = new RunResult();

        Context bodyCtx = ctx;
        if (createContext)
            bodyCtx = new Context(ctx);

        for (Node node : nodes) {
            res.register(node.execute(bodyCtx));
            if (res.hasError() || res.isReturn())
                return res;
        }
        return res;
    }
}
