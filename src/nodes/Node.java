package nodes;

import interpreter.Context;
import results.RunResult;

public abstract class Node {
    public abstract RunResult execute(Context ctx);
}
