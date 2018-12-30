package ru.fmtk.hlystov.repl.stage6.model.expressions.operations;


import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;
import ru.fmtk.hlystov.repl.stage6.processor.IContext;
import ru.fmtk.hlystov.repl.stage6.processor.IStringConsumer;
import ru.fmtk.hlystov.repl.stage6.processor.Processor;

public class Variable extends Expression {
    private String name;

    public Variable(String name) {
        this.name = name;
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
}