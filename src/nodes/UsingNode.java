package nodes;

import interpreter.Context;
import results.RunResult;
import values.Value;
import values.ValueType;

public class UsingNode extends Node {
    private final Node model;
    private final Node body;

    public UsingNode(Node model, Node body) {
        this.model = model;
        this.body = body;
    }

    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        Value model = res.register(this.model.execute(ctx));
        if (res.hasError())
            return res;
        if (!model.is(ValueType.MODEL))
            return res.failure("Must be a model.");

        Context subCtx = new Context(model.asModel(), ctx);
        if (body instanceof BodyNode b)
            return b.execute(subCtx, false);
        return body.execute(subCtx);
    }
}
