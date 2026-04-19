package interpreter;

import stdlib.BooleanLogicModel;
import values.LogicModel;
import values.ModelSymbol;
import values.Value;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private Context parent;

    private LogicModel model;
    private Map<String, Value> variables;
    private Map<String, LogicModel> models;

    public Context(LogicModel model, Context parent) {
        this.model = model;
        this.variables = new HashMap<>();
        this.models = new HashMap<>();
        this.parent = parent;

        // stdlib
        declareModel(BooleanLogicModel.get());
    }

    public Context(Context parent) {
        this(parent != null ? parent.getModel() : BooleanLogicModel.get(), parent);
    }

    public Context(LogicModel model) {
        this(model, null);
    }

    public Context() {
        this((Context) null);
    }

    public Context setDefaultModel(LogicModel model) {
        this.model = model;
        return this;
    }

    public Context setVariable(String name, Value val) {
        variables.put(name, val);
        return this;
    }

    public Value getVariable(String name) {
        if (variables.containsKey(name))
            return variables.get(name);

        ModelSymbol modelSymbol = model.getSymbol(name);
        if (modelSymbol != null)
            return new Value(modelSymbol);

        if (parent != null)
            return parent.getVariable(name);

        return null;
    }

    public LogicModel getModel(String name) {
        if (models.containsKey(name))
            return models.get(name);

        if (parent != null)
            return parent.getModel(name);

        return null;
    }

    public LogicModel getModel() {
        return model;
    }

    public void declareModel(LogicModel model) {
        this.models.put(model.getName(), model);
    }

    public Context getParent() {
        return parent;
    }
}
