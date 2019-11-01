package ru.fmtk.hlystov.repl.stage6.processor;

import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;

public interface IContext {
    boolean setVariable(String name, Expression value);
    Expression getVariable(String name);
}
