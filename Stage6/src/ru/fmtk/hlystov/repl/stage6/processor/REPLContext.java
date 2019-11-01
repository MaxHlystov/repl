package ru.fmtk.hlystov.repl.stage6.processor;

import ru.fmtk.hlystov.repl.stage6.Strings;
import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;
import ru.fmtk.hlystov.repl.stage6.model.expressions.operations.ErrorExpression;

import java.util.HashMap;
import java.util.Map;

public class REPLContext implements IContext {
    private final Map<String, Expression> variables = new HashMap<>();

    public Map<String, Expression> getVariables() {
        return variables;
    }

    @Override
    public boolean setVariable(String name, Expression value) {
        if (name != null) {
            if (value == null) {
                variables.remove(name);
            } else {
                variables.put(name, value);
            }
            return true;
        }
        return false;
    }

    @Override
    public Expression getVariable(String name) {
        if (name != null) {
            Expression result = variables.get(name);
            if(result != null) {
                return result;
            }
        }
        return new ErrorExpression(Strings.ERROR_MSG_Unknown_variable);
    }


}
