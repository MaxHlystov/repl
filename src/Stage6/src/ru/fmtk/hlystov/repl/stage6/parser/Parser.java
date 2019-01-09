package ru.fmtk.hlystov.repl.stage6.parser;

import ru.fmtk.hlystov.repl.stage6.Strings;
import ru.fmtk.hlystov.repl.stage6.model.AbstractClause;
import ru.fmtk.hlystov.repl.stage6.model.Assignment;
import ru.fmtk.hlystov.repl.stage6.model.Command;
import ru.fmtk.hlystov.repl.stage6.model.expressions.Expression;
import ru.fmtk.hlystov.repl.stage6.model.expressions.IntNumber;
import ru.fmtk.hlystov.repl.stage6.model.expressions.operations.*;

import java.util.List;

public class Parser {

    // TODO: replace regexRules and expTypes to reflection
    // over the Expression children in initTokenizer().
    private static String[] regexRules = {"[a-zA-Z]+", "\\d+", "\\-", "\\+"};
    private static Class[] expTypes = {
            Variable.class, IntNumber.class, MinusOperation.class, PlusOperation.class};

    private static Tokenaizer tokenizer;

    private static synchronized void initTokenizer() {
        if (tokenizer == null) {
            tokenizer = new Tokenaizer(regexRules, expTypes);
        }
    }

    public AbstractClause parse(String line) {
        Command command = parseCommand(line);
        if (command != null) {
            return command;
        }

        Assignment assignment = parseAssignment(line);
        if (assignment != null) {
            return assignment;
        }

        Expression expression = parseExpression(line);
        if (expression != null) {
            return expression;
        }
        return null;
    }

    public Command parseCommand(String cmd) {
        if (cmd == null || cmd.charAt(0) != '/') {
            return null;
        }
        return new Command(cmd.substring(1));
    }

    public Assignment parseAssignment(String assigString) {
        if (assigString != null && !assigString.isEmpty()) {
            int pos = assigString.indexOf('=');
            if (pos >= 0) {
                int length = assigString.length();
                // if '=' is at the first or last position, then we have an error.
                if (pos > 0 || pos < length - 1) {
                    String expText = assigString.substring(pos + 1, length);
                    if (expText.indexOf('=') >= 0) {
                        return Assignment.createError(Strings.ERROR_MSG_Invalid_assignment);
                    }
                    Expression expression = parseExpression(expText);
                    if (expression == null || expression.hasError()) {
                        return Assignment.createError(Strings.ERROR_MSG_Invalid_value);
                    }
                    String varName = assigString.substring(0, pos).trim();
                    return new Assignment(new Variable(varName), expression);
                } else {
                    return Assignment.createError(Strings.ERROR_MSG_Invalid_assignment);
                }
            }
        }
        return null;
    }


    public Expression parseExpression(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        Expression result = null;
        boolean isError = false;
        initTokenizer();
        List<Token> tokens = tokenizer.tokenize(line);
        if (tokens == null || tokens.size() == 0) {
            isError = true;
        } else {
            for (Token token : tokens) {
                String tokenText = token.getText();
                Class expType = token.getExpType();
                if (tokenText == null || tokenText.isEmpty()
                        || expType == null) {
                    isError = true;
                    break;
                }
                if (expType == MinusOperation.class) {
                    result = new MinusOperation(new Expression[]{result, null});
                } else if (expType == PlusOperation.class) {
                    result = new PlusOperation(new Expression[]{result, null});
                } else {
                    Expression tmp;
                    if (expType == IntNumber.class) {
                        tmp = parseIntNumber(tokenText);
                    } else if (expType == Variable.class) {
                        tmp = new Variable(tokenText);
                    } else {
                        isError = true;
                        break;
                    }
                    if (result == null) {
                        result = tmp;
                    } else if (result instanceof Operation) {
                        Expression[] args = ((Operation) result).getArgs();
                        if (args == null || args.length < 2 || args[1] != null) {
                            isError = true;
                            break;
                        }
                        args[1] = tmp;
                    } else {
                        isError = true;
                        break;
                    }
                }
            }
            if (!isError && result instanceof Operation) {
                Expression[] args = ((Operation) result).getArgs();
                if (args == null || args.length < 2 || args[1] == null) {
                    isError = true;
                }
            }
        }
        if (isError || result == null) {
            result = new ErrorExpression(Strings.ERROR_MSG_Invalid_expression);
        }
        return result;
    }

    public Variable parseVariable(String text) {
        Variable result = new Variable(text);
        return result;
    }

    public IntNumber parseIntNumber(String text) {
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return null;
        }
        return new IntNumber(value);
    }
}
