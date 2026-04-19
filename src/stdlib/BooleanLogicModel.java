package stdlib;

import results.RunResult;
import values.*;

public class BooleanLogicModel extends LogicModel {
    private static final BooleanLogicModel INSTANCE = new BooleanLogicModel();

    public static BooleanLogicModel get() {
        return INSTANCE;
    }

    public static ModelSymbol True() {
        return INSTANCE.TRUE;
    }

    public static ModelSymbol False() {
        return INSTANCE.FALSE;
    }

    private final ModelSymbol TRUE;
    private final ModelSymbol FALSE;

    public BooleanLogicModel() {
        super("Boolean");

        TRUE = defineSymbol("True");
        FALSE = defineSymbol("False");

        defineOperator("and", (left, right) ->
                new RunResult(Evaluate(left == TRUE && right == TRUE)));
        defineOperator("or", (left, right) ->
                new RunResult(Evaluate(left == TRUE || right == TRUE)));
        defineOperator("implies", (left, right) ->
                new RunResult(Evaluate(left == FALSE || right == TRUE)));
        defineOperator("iff", (left, right) ->
                new RunResult(Evaluate((left == TRUE) == (right == TRUE))));

        defineOperator("not", (value) -> new RunResult(Evaluate(value == FALSE)));
    }

    public static ModelSymbol Truthy(boolean v) {
        return v ? True() : False();
    }

    public static Value Evaluate(boolean v) {
        return new Value(Truthy(v));
    }
}
