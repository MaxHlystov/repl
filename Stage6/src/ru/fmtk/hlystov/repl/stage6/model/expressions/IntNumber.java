package ru.fmtk.hlystov.repl.stage6.model.expressions;

import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;

public class IntNumber extends Expression {
    private final int value;

    public IntNumber(int value) {
        this.value = value;
    }

    public int toInt() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}