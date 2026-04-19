package values;

import interpreter.Context;
import nodes.BodyNode;
import nodes.Node;
import results.RunResult;
import stdlib.BooleanLogicModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Formula {
    private final Node body;
    private final String[] variables;
    private final Map<String, Value> curry;
    private Context ctx;

    public Formula(String[] variables, Node body) {
        this.variables = variables;
        this.body = body;
        this.curry = new HashMap<>();
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public void curry(String var, Value val) {
        this.curry.put(var, val);
    }

    public RunResult execute(Context parent, Node[] arguments) {
        return execute(parent, arguments, parent.getModel());
    }

    public RunResult execute(Context parent, Node[] arguments, LogicModel model) {
        // simulate more of a valuation rather than anything
        RunResult res = new RunResult();

        Context ctx = new Context(model, this.ctx);

        Value[] args = new Value[Math.min(arguments.length, variables.length)];
        for (int i = 0; i < args.length; i++) {
            Value arg = res.register(arguments[i].execute(parent));

            if (res.hasError())
                return res;

            ctx.setVariable(variables[i], arg);
            args[i] = arg;
        }

        // try to run formula
        // if missing a variable, return curry
        // if regular failure, return error
        // if success, return value
        res.register(body.execute(ctx));
        if (res.hasError() && res.failedLookup()) {
            Formula curry = new Formula(variables, body);
            for (int i = 0; i < args.length; i++)
                curry.curry(variables[i], args[i]);
            return res.success(new Value(curry));
        }

        // return failure/succcess
        return res;
    }

    @Override
    public String toString() {
        String[] values = new String[variables.length];
        for (int i = 0; i < variables.length; i++) {
            if (curry.containsKey(variables[i]))
                values[i] = String.format("%s -> %s", variables[i], curry.get(variables[i]));
            else
                values[i] = variables[i];
        }
        return String.format("< %s >", String.join(", ", values));
    }
}
