package ru.fmtk.hlystov.repl.stage6.model.expressions.operations;

import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;

public class ErrorExpression extends Expression {
    public ErrorExpression(String error) {
        setError(error);
    }
}