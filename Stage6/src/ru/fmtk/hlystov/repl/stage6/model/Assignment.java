package ru.fmtk.hlystov.repl.stage6.model;

import ru.fmtk.hlystov.repl.stage6.Strings;
import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;
import ru.fmtk.hlystov.repl.stage6.model.expressions.operations.Variable;
import ru.fmtk.hlystov.repl.stage6.processor.IStringConsumer;
import ru.fmtk.hlystov.repl.stage6.processor.Processor;

public class Assignment extends AbstractClause {
    private final Variable variable;
    private final Expression expression;

    public static Assignment createError(String error) {
        Assignment assignment = new Assignment(null, null);
        assignment.setError(error);
        return assignment;
    }

    public Assignment(Variable variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public Variable getVariable() {
        return variable;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean process(Processor processor, IStringConsumer consumer) {
        return processor.process(this, consumer);
    }

    @Override
    public String toString() {
        String result = "";
        if(variable != null) {
            result += variable.toString();
        }
        result += "=" + ((getExpression() == null) ? "null": getExpression());
        return result;
    }
}