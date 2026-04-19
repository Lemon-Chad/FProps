package values;

import results.RunResult;

public interface OperatorBody {
    RunResult unary(ModelSymbol val);
    RunResult binary(ModelSymbol left, ModelSymbol right);

    interface Unary {
        RunResult unary(ModelSymbol val);
    }

    interface Binary {
        RunResult binary(ModelSymbol left, ModelSymbol right);
    }

    static OperatorBody Binary(Binary bin) {
        return new OperatorBody() {
            @Override
            public RunResult unary(ModelSymbol val) {
                throw new RuntimeException("Operator is binary.");
            }

            @Override
            public RunResult binary(ModelSymbol left, ModelSymbol right) {
                return bin.binary(left, right);
            }
        };
    }

    static OperatorBody Unary(Unary una) {
        return new OperatorBody() {
            @Override
            public RunResult unary(ModelSymbol val) {
                return una.unary(val);
            }

            @Override
            public RunResult binary(ModelSymbol left, ModelSymbol right) {
                throw new RuntimeException("Operator is unary.");
            }
        };
    }
}
