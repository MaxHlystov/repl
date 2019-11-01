/* Stage #6: Supporting variables
Improve the project to support variable using maps

Description
Modify the program and send the link to your pull request.
At this stage, the program should support for variables to REPL.
Suppose that the name of a variable (identifier) can contain only
Latin letters. The case is also important; for example, n is not
the same as N. The value can be an integer number or a value
of another variable.
Use Map to support variables.

The assignment statement may look like the following:
n = 3
m=4
a  =   5
b = a

A variable can have a name consisting of more than one letter.
count = 10

To print the value of a variable you should just type its name.
N = 5
N
5

It should be possible to set a new value to an existing variable.
a = 1
a = 2
a = 3
a
3

If an identifier or value of a variable is invalid, the program
must print a message like the one below.

a1 = 8
Invalid identifier
n = a2a
Invalid value
a = 7 = 8
Invalid assignment

If at the time of use a variable is not declared yet, the program
should print a message like "Unknown variable".

a = 8
b = c
Unknown variable
e
Unknown variable

Handle as many incorrect inputs as possible. The program must never
throw the NumberFormatException or any other exception.

It is important to note that the value of all variables must
be kept between the calculations of different expressions.

Output example

a = 3
b = 4
c = 5
a + b - c
2
b - c + 4 - a
0
a = 800
a + b + c
809
BIG = 9000
BIG
9000
big
Unknown variable

The program should not stop until the user enters the /exit command.

 */

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Stage6 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Processor processor = new Processor(new REPLContext());
        Parser parser = new Parser();

        while (true) {
            String line = sc.nextLine();
            if (line == null || line.isEmpty()) {
                continue;
            }

            AbstractClause clause = parser.parse(line);
            if (clause != null && !processor.process(clause, System.out::println)) {
                break;
            }
        }
        System.out.println(Strings.MSG_Bye_bye);
    }

    public static class Strings {
        public static String ERROR_MSG_Invalid_assignment = "Invalid assignment";
        public static String ERROR_MSG_Invalid_identifier = "Invalid identifier";
        public static String ERROR_MSG_Invalid_expression = "Invalid expression";
        public static String ERROR_MSG_Invalid_value = "Invalid value";
        public static String ERROR_MSG_Unknown_variable = "Unknown variable";
        public static String ERROR_MSG_Unknown_command = "Unknown command";
        public static String ERROR_MSG_EmptyVariables = "There are no any stored variables.";

        public static String MSG_Bye_bye = "Bye bye.";
        public static String MSG_HELP = "The program calculates the sum of numbers";
        public static String MSG_LIST_OF_VARIABLES = "List of variables:";
    }

    public static class Token {
        private String text;
        private Class expType;

        public Token(String text, Class expType) {
            this.text = text;
            this.expType = expType;
        }

        public String getText() {
            return text;
        }

        public Class getExpType() {
            return expType;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("t(");
            if (text != null) {
                sb.append(text);
            }
            sb.append(", ");
            if (expType != null) {
                sb.append(expType.getName());
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static class Tokenaizer {
        private Pattern pattern;
        private Class[] expTypes;

        public Tokenaizer(String[] tokenPatterns, Class[] expTypes) {
            if (tokenPatterns != null && tokenPatterns.length > 0) {
                String stringPattern = "(" + String.join(")|(", tokenPatterns) + ")|(\\s+)";
                pattern = Pattern.compile(stringPattern);
                if (expTypes != null && expTypes.length >= tokenPatterns.length) {
                    this.expTypes = expTypes;
                }
            }
        }

        /**
         * Returns null if there is an error character in the line.
         * Returns Token list otherwise.
         **/
        public List<Token> tokenize(String line) {
            if (expTypes != null && line != null && !line.isEmpty() && pattern != null) {
                List<Token> result = new ArrayList<>();
                Matcher matcher = pattern.matcher(line);
                int end = 0;
                while (matcher.find(end)) {
                    String text = matcher.group(0);
                    if (text == null || matcher.end() - end > text.length()) {
                        return null; // there are unwaiting characters
                    }
                    int groupIndex = groupIndex(matcher);
                    if (groupIndex >= 0) {
                        result.add(new Token(matcher.group(0),
                                expTypes[groupIndex]));
                    }
                    end = matcher.end();
                }
                return result;
            }
            return null;
        }

        private int groupIndex(Matcher matcher) {
            int count = matcher.groupCount();
            // count is the spaces group
            for (int i = 1; i < count; ++i) {
                if (matcher.group(i) != null) {
                    return i - 1;
                }
            }
            return -1;
        }
    }

    public static class Parser {

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

    public static abstract class AbstractClause {
        private String error = null;

        public boolean hasError() {
            return error != null;
        }

        public String getError() {
            if (error == null) {
                return "";
            }
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public abstract boolean process(Processor processor,
                                        IStringConsumer consumer);

        @Override
        public String toString() {
            if (hasError()) {
                return getError();
            }
            return "";
        }
    }

    public static class Command extends AbstractClause {
        private final String commandName;

        public Command(String commandName) {
            this.commandName = commandName;
        }

        public String getCommandName() {
            return commandName;
        }

        public boolean process(Processor processor, IStringConsumer consumer) {
            return processor.process((Command) this, consumer);
        }

        public boolean equals(Object o) {
            if (hasError()) {
                return false;
            }
            if (o instanceof Command) {
                return Objects.equals(commandName, ((Command) o).commandName);
            }
            if (o instanceof String) {
                return Objects.equals(commandName, o);
            }
            return false;
        }
    }

    public static class Assignment extends AbstractClause {
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
            if (variable != null) {
                result += variable.toString();
            }
            result += "=" + ((getExpression() == null) ? "null" : getExpression());
            return result;
        }
    }

    public static abstract class Expression extends AbstractClause {
        private Expression root = null;

        public boolean process(Processor processor, IStringConsumer consumer) {
            return processor.process(this, consumer);
        }

        public Expression fold(IContext context) {
            if (hasError()) {
                return new ErrorExpression(Strings.ERROR_MSG_Invalid_expression);
            }
            if (root != null) {
                return root.fold(context);
            }
            return this;
        }
    }

    public static class ErrorExpression extends Expression {
        public ErrorExpression(String error) {
            setError(error);
        }
    }

    public static class IntNumber extends Expression {
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

    public static class Variable extends Expression {
        private String name;

        public Variable(String name) {
            if (checkVariableName(name)) {
                this.name = name;
            } else {
                setError(Strings.ERROR_MSG_Invalid_identifier);
            }
        }

        public String getName() {
            return name;
        }

        public boolean process(Processor processor, IStringConsumer consumer) {
            return processor.process(this, consumer);
        }

        @Override
        public Expression fold(IContext context) {
            return context.getVariable(getName());
        }

        @Override
        public String toString() {
            return name;
        }

        private boolean checkVariableName(String variableName) {
            if (variableName == null) {
                return false;
            }
            return variableName.matches("[a-zA-Z]+");
        }
    }

    public static abstract class Operation extends Expression {
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
                if (exp != null) {
                    if (exp.hasError()) {
                        return exp;
                    }
                    Expression newExp = exp.fold(context);
                    if (newExp != null) {
                        if (newExp.hasError()) {
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

    public static class MinusOperation extends Operation {

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

    public static class PlusOperation extends Operation {

        public PlusOperation(Expression[] arguments) {
            super("+", arguments);
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
                    + ((IntNumber) right).toInt());
        }
    }

    public interface IContext {
        boolean setVariable(String name, Expression value);
        Expression getVariable(String name);
    }

    public interface IStringConsumer {
        void apply(String message);
    }

    public static class REPLContext implements IContext {
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
                if (result != null) {
                    return result;
                }
            }
            return new ErrorExpression(Strings.ERROR_MSG_Unknown_variable);
        }
    }

    public static class Processor {
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
                } else {
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

}

