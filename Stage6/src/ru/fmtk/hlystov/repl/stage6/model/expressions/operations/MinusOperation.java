package ru.fmtk.hlystov.repl.stage6.model.expressions.operations;

import ru.fmtk.hlystov.repl.stage6.Strings;
import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;
import ru.fmtk.hlystov.repl.stage6.model.expressions.IntNumber;

public class MinusOperation extends Operation {

    public MinusOperation(Expression[] arguments) {
        super("-", arguments);
    }

    @Override
    public IntNumber getZero() {
        return new IntNumber(0);
    }

    @Override
    public Expression apply(Expression left, Expression right) {
        if (left.hasError() || !(left instanceof IntNumber)
                    || right.hasError() || !(right instanceof IntNumber)) {
            return new ErrorExpression(Strings.ERROR_MSG_Invalid_expression);
        }
        return new IntNumber(((IntNumber) left).toInt()
                - ((IntNumber) right).toInt());
    }
}