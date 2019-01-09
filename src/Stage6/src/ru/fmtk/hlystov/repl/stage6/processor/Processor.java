package ru.fmtk.hlystov.repl.stage6.processor;

import ru.fmtk.hlystov.repl.stage6.Strings;
import ru.fmtk.hlystov.repl.stage6.model.AbstractClause;
import ru.fmtk.hlystov.repl.stage6.model.Assignment;
import ru.fmtk.hlystov.repl.stage6.model.Command;
import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;
import ru.fmtk.hlystov.repl.stage6.model.expressions.IntNumber;
import ru.fmtk.hlystov.repl.stage6.model.expressions.operations.ErrorExpression;
import ru.fmtk.hlystov.repl.stage6.model.expressions.operations.Variable;

import java.util.Map;

public class Processor {
    private REPLContext context;

    public Processor(REPLContext context) {
        this.context = context;
    }

    public boolean process(AbstractClause clause,
                           IStringConsumer consumer) {
        if (!processError(clause, consumer)) {
            return clause.process(this, consumer);
        }
        return true;
    }

    public boolean process(Command command,
                           IStringConsumer consumer) {
        if (!processError(command, consumer)) {
            if (command.equals("exit")) {
                return false;
            }
            if (command.equals("help")) {
                consumer.apply(Strings.MSG_HELP);
            } else if (command.equals("list")) {
                Map<String, Expression> vars = context.getVariables();
                if (vars.isEmpty())
                    consumer.apply(Strings.ERROR_MSG_EmptyVariables);
                else {
                    consumer.apply(Strings.MSG_LIST_OF_VARIABLES);
                    for (Map.Entry<String, Expression> e : vars.entrySet())
                        consumer.apply(String.format(
                                "\t%s=%s;", e.getKey(), e.getValue()));
                }
            } else {
                consumer.apply(Strings.ERROR_MSG_Unknown_command);
            }
        }
        return true;
    }

    public boolean process(Assignment assignment,
                           IStringConsumer consumer) {
        if (!assignment.hasError()) {
            Variable variable = assignment.getVariable();
            if (variable == null) {
                consumer.apply(Strings.ERROR_MSG_Invalid_identifier);
            } else if (variable.hasError()) {
                consumer.apply(variable.getError());
            } else{
                Expression foldedExpression = assignment.getExpression().fold(context);
                if (foldedExpression.hasError()) {
                    consumer.apply(foldedExpression.getError());
                } else if (!context.setVariable(variable.getName(), foldedExpression)) {
                    consumer.apply(Strings.ERROR_MSG_Invalid_assignment);
                }
            }
        } else {
            consumer.apply(Strings.ERROR_MSG_Invalid_assignment);
        }
        return true;
    }

    public boolean process(Expression expression,
                           IStringConsumer consumer) {
        if (!processError(expression, consumer)) {
            Expression foldedExpression = expression.fold(context);
            if (!processError(foldedExpression, consumer)) {
                consumer.apply(foldedExpression.toString());
                return true;
            }
        }
        return true;
    }

    public boolean process(IntNumber number,
                           IStringConsumer consumer) {
        if (!processError(number, consumer)) {
            consumer.apply(number.toString());
            return true;
        }
        return true;
    }

    public boolean process(Variable variable,
                           IStringConsumer consumer) {
        if (!processError(variable, consumer)) {
            Expression expression = context.getVariable(variable.getName());
            if (expression == null) {
                expression = new ErrorExpression(Strings.ERROR_MSG_Invalid_identifier);
            }
            return process(expression, consumer);
        }
        return true;
    }

    protected boolean processError(AbstractClause clause,
                                   IStringConsumer consumer) {
        if (clause == null) {
            consumer.apply(Strings.ERROR_MSG_Invalid_expression);
        }
        if (clause.hasError()) {
            consumer.apply(clause.getError());
            return true;
        }
        return false;
    }
}

