package values;

import interpreter.Context;
import nodes.Node;
import results.RunResult;

public class ModelOperator {
    private final LogicModel model;
    private final String name;
    private final boolean binary;
    private Node body;
    private OperatorBody stdBody;

    public ModelOperator(LogicModel model, String name, boolean binary, Node body) {
        this.model = model;
        this.name = name;
        this.binary = binary;
        this.body = body;
    }

    public ModelOperator(LogicModel model, String name, boolean binary, OperatorBody stdBody) {
        this.model = model;
        this.name = name;
        this.binary = binary;
        this.stdBody = stdBody;
    }

    public RunResult execute(ModelSymbol left, ModelSymbol right) {
        if (stdBody != null)
            return stdBody.binary(left, right);

        Context ctx = new Context(model)
                .setVariable("left", new Value(left))
                .setVariable("right", new Value(right));
        return body.execute(ctx);
    }

    public RunResult execute(ModelSymbol value) {
        if (stdBody != null)
            return stdBody.unary(value);

        Context ctx = new Context(model)
                .setVariable("val", new Value(value));
        return body.execute(ctx);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format(
                "%s.%s(%s)",
                model.toString(), name,
                binary ? "left, right" : "val"
        );
    }

    public boolean isBinary() {
        return binary;
    }
}
