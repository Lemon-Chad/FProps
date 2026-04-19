package nodes;

import interpreter.Context;
import results.RunResult;
import stdlib.BooleanLogicModel;
import values.Value;
import values.ValueType;

public class IfNode extends Node {
    private final Node condition;
    private final Node ifTrue;
    private final Node ifFalse;

    public IfNode(Node condition, Node ifTrue, Node ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public IfNode(Node condition, Node ifTrue) {
        this(condition, ifTrue, null);
    }


    @Override
    public RunResult execute(Context ctx) {
        RunResult res = new RunResult();

        Value condition = res.register(this.condition.execute(ctx));
        if (res.hasError())
            return res;
        if (!(condition.is(ValueType.SYMBOL) && condition.asSymbol().getModel().equals(BooleanLogicModel.get())))
            return res.failure("Expected binary symbol.");

        if (condition.asSymbol().equals(BooleanLogicModel.True())) {
            return ifTrue.execute(ctx);
        } else if (ifFalse != null) {
            return ifFalse.execute(ctx);
        }

        return res.success(new Value());
    }
}
