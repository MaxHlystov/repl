package ru.fmtk.hlystov.repl.stage6.model.expressions;

import ru.fmtk.hlystov.repl.stage6.model.AbstractClause;
import ru.fmtk.hlystov.repl.stage6.model.expressions.operations.ErrorExpression;
import ru.fmtk.hlystov.repl.stage6.processor.IContext;
import ru.fmtk.hlystov.repl.stage6.processor.IStringConsumer;
import ru.fmtk.hlystov.repl.stage6.processor.Processor;
import ru.fmtk.hlystov.repl.stage6.Strings;

public abstract class Expression extends AbstractClause {
    private Expression root = null;

    public boolean process(Processor processor, IStringConsumer consumer) {
        return processor.process(this, consumer);
    }

    public Expression fold(IContext context) {
        if (hasError()) {
            return new ErrorExpression(Strings.ERROR_MSG_Invalid_expression);
        }
        if(root != null) {
            return root.fold(context);
        }
        return this;
    }
}