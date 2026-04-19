package results;

import values.Value;

public class RunResult {
    private Value value;
    private String error;
    private boolean lookupFailure = false;
    private boolean isReturn = false;

    public RunResult() {
        this.value = new Value();
        this.error = null;
    }

    public RunResult(Value value) {
        this.value = value;
        this.error = null;
    }

    public RunResult(String error) {
        this.value = new Value();
        this.error = error;
    }

    public Value getValue() {
        return value;
    }

    public String getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }

    public Value register(RunResult res) {
        if (res.hasError()) {
            error = res.error;
            lookupFailure = res.lookupFailure;
            return null;
        }

        isReturn = res.isReturn;
        value = res.getValue();
        return value;
    }

    public RunResult success(Value val) {
        this.value = val;
        this.error = null;
        this.lookupFailure = false;
        this.isReturn = false;
        return this;
    }

    public RunResult failure(String error) {
        this.error = error;
        this.value = new Value();
        this.lookupFailure = false;
        this.isReturn = false;
        return this;
    }

    @Override
    public String toString() {
        if (hasError())
            return String.format("[err: '%s']", error);

        return String.format("[val: (%s)]", value != null ? value.toString(): "null");
    }

    public RunResult lookupFailure(String error) {
        this.error = error;
        this.lookupFailure = true;
        this.value = new Value();
        this.isReturn = false;
        return this;
    }

    public RunResult returnValue(Value val) {
        this.value = val;
        this.isReturn = true;
        this.error = null;
        this.lookupFailure = false;
        return this;
    }

    public boolean failedLookup() {
        return lookupFailure;
    }

    public boolean isReturn() {
        return isReturn;
    }
}
