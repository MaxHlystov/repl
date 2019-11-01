package ru.fmtk.hlystov.repl.stage6.model.expressions.operations;


import ru.fmtk.hlystov.repl.stage6.Strings;
import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;
import ru.fmtk.hlystov.repl.stage6.processor.IContext;
import ru.fmtk.hlystov.repl.stage6.processor.IStringConsumer;
import ru.fmtk.hlystov.repl.stage6.processor.Processor;

public class Variable extends Expression {
    private String name;

    public Variable(String name) {
        if(checkVariableName(name)) {
            this.name = name;
        }
        else {
            setError(Strings.ERROR_MSG_Invalid_identifier);
        }
    }

    public String getName() {
        return name;
    }

    public boolean process(Processor processor, IStringConsumer consumer) {
        return processor.process(this, consumer);
    }

    @Override
    public Expression fold(IContext context) {
        return context.getVariable(getName());
    }

    @Override
    public String toString() {
        return name;
    }

    private boolean checkVariableName(String variableName) {
        if(variableName == null) {
            return false;
        }
        return variableName.matches("[a-zA-Z]+");
    }
}