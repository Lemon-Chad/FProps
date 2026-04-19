package values;

public class Value {
    private final ValueType type;
    private final Object object;

    public Value() {
        this(null);
    }

    public Value(Object object) {
        this.object = object;
        this.type = switch (object) {
            case LogicModel _ -> ValueType.MODEL;
            case ModelSymbol _ -> ValueType.SYMBOL;
            case ModelOperator _ -> ValueType.OPERATOR;
            case Formula _ -> ValueType.FORMULA;
            case null -> ValueType.VOID;
            default -> throw new RuntimeException("Unknown value type.");
        };
    }

    public ValueType getType() {
        return type;
    }

    public boolean is(ValueType valueType) {
        return type.equals(valueType);
    }

    public LogicModel asModel() {
        return (LogicModel) object;
    }

    public ModelSymbol asSymbol() {
        return (ModelSymbol) object;
    }

    public ModelOperator asOperator() {
        return (ModelOperator) object;
    }

    public Formula asFormula() {
        return (Formula) object;
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
