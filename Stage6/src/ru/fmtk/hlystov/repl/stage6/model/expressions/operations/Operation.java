package ru.fmtk.hlystov.repl.stage6.model.expressions.operations;

import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;
import ru.fmtk.hlystov.repl.stage6.processor.IContext;
import ru.fmtk.hlystov.repl.stage6.Strings;

public abstract class Operation extends Expression {
    private String operation;
    private Expression[] arguments;

    public Operation(String operation, Expression[] arguments) {
        this.operation = operation;
        this.arguments = arguments;
    }

    public String getOperation() {
        return operation;
    }

    public Expression[] getArgs() {
        return arguments;
    }

    public abstract Expression apply(Expression left, Expression right);

    public abstract Expression getZero();

    @Override
    public Expression fold(IContext context) {
        if (getArgs() == null || hasError()) {
            return new ErrorExpression(Strings.ERROR_MSG_Invalid_expression);
        }
        Expression result = getZero();
        for (Expression exp : getArgs()) {
            if(exp != null) {
                if (exp.hasError()) {
                    return exp;
                }
                Expression newExp = exp.fold(context);
                if (newExp != null) {
                    if(newExp.hasError()) {
                        return newExp;
                    }
                    result = apply(result, newExp);
                    if (result.hasError()) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasError() {
        if (super.hasError() || operation == null || arguments == null) {
            return true;
        }
        for (Expression exp : arguments) {
            if (exp == null || exp.hasError()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getError() {
        if (super.hasError()) {
            return super.getError();
        }
        if (hasError()) {
            return Strings.ERROR_MSG_Invalid_expression;
        }
        return "";
    }

    @Override
    public String toString() {
        if (hasError()) {
            return getError();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(operation);
        sb.append(" ");
        boolean first = true;
        for (Expression arg : arguments) {
            if (arg.hasError()) {
                return arg.getError();
            }
            if (first) {
                first = false;

            } else {
                sb.append(" ");
            }
            sb.append(arg.toString());
        }
        sb.append(")");
        return sb.toString();
    }
}