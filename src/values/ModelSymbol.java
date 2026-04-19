package values;

public class ModelSymbol {
    private final String name;
    private final LogicModel model;

    public ModelSymbol(LogicModel model, String name) {
        this.name = name;
        this.model = model;
    }

    public LogicModel getModel() {
        return this.model;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("%s.%s", model.toString(), name);
    }
}
