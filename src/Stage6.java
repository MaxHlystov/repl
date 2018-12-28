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

import java.util.Objects;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class Stage6 {

    private static String ERROR_MSG_Invalid_assignment = "Invalid assignment";
    private static String ERROR_MSG_Invalid_identifier = "Invalid identifier";
    private static String ERROR_MSG_Invalid_expression = "Invalid expression";
    private static String ERROR_MSG_Invalid_value = "Invalid value";
    private static String ERROR_MSG_Unknown_variable = "Unknown variable";
    private static String ERROR_MSG_Unknown_command = "Unknown command";
    private static String ERROR_MSG_EmptyVariables = 
			"There are no any stored variables.";

    private static String MSG_Bye_bye = "Bye bye.";
    private static String MSG_HELP = "The program calculates the sum of numbers";
    private static String MSG_LIST_OF_VARIABLES = "List of variables:";

    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Processor processor = new Processor(new REPLContext());
		Parser parser = new Parser();
		
        while (true) {
            String line = sc.nextLine();
            if (line == null || line.isEmpty()) { continue; }

            AbstractClause clause = parser.parse(line);
            if (clause != null 
					&& !processor.process(clause, System.out::println)) {
                break;
            }
        }
        System.out.println(MSG_Bye_bye);
    }

    
    private static class Processor {
        private REPLContext context;
        
        public Processor(REPLContext context) {
            this.context = context;
        }
        
        public boolean process(AbstractClause clause,
                               IStringConsumer consumer) {
            return clause.process(this, consumer);
        }
                                      
        public boolean process(Command command,
                                      IStringConsumer consumer) {
            if (!processError(command, consumer)) {
                if (command.equals("exit")) {
                    return false;
                }
                if (command.equals("help")) {
                    consumer.apply(MSG_HELP);
                } else if (command.equals("list")) {
                    Map<String,IntNumber> vars = context.getIntVars();
                    if (vars.isEmpty())
                        consumer.apply(ERROR_MSG_EmptyVariables);
                    else {
                        consumer.apply(MSG_LIST_OF_VARIABLES);
                        for (Map.Entry<String, IntNumber> e: vars.entrySet())
                            consumer.apply(String.format(
                                    "\t%s;=%s;", e.getKey(), e.getValue()));
                    }
                } else {
                    consumer.apply(ERROR_MSG_Unknown_command);
                }
            }
            return true;
        }

        public boolean process(Assignment assignment,
                                      IStringConsumer consumer) {
            if (!assignment.hasError()) {
                String varName = assignment.getVariableName();
                Expression foldedExpression = fold(assignment.getExpression());
                if (checkVariableName(varName) && !foldedExpression.hasError()
                            && foldedExpression instanceof IntNumber) {
                    context.addIntVariable(varName, (IntNumber) foldedExpression);
                }
            }
            consumer.apply(ERROR_MSG_Invalid_assignment);
            return true;
        }

        public boolean process(Expression expression,
                                      IStringConsumer consumer) {
            Expression foldedExpression = fold(expression);
            if (!processError(foldedExpression, consumer)) {
                consumer.apply(foldedExpression.toString());
            }
            return true;
        }
        
        protected Expression fold(Expression expression) {
            if(expression == null) {
                return new ErrorExpression(ERROR_MSG_Invalid_expression);
            }
            if(expression.hasError()) {
                return expression; 
            }
            
            return null;
        }
        
        protected boolean processError(AbstractClause clause,
                                            IStringConsumer consumer) {
            if (clause.hasError()) {
                consumer.apply(clause.getError());
                return true;
            }
            return false;
        }
        
        protected boolean checkVariableName(String variableName) {
            return variableName.matches("[a-zA-Z]+");
        }
    }

	
    private static abstract class AbstractClause {
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

        protected void setError(String error) {
            this.error = error;
        }
        
        public abstract boolean process(Processor processor,
                                        IStringConsumer consumer);
		
		@Override
        public String toString() {
            if(hasError()) { return getError(); }
            return "";
        }
    }

	
	private static class Parser {
		
		// TODO: replace regexRules and expressionRules to reflection
		// over the Expression children in initTokenizer().
		private static String[] regexRules = { "[a-zA-Z]+", "\\d+", "\\-", "\\+" };
		private static Class[] expressionRules = { 
			Variable.class, IntNumber.class, Operation.class, Operation.class };
			
		private static Tokenaizer tokenizer = null;
		
		private static void initTokenizer() {
			if(tokenizer == null) {
				tokenizer = new Tokenaizer(regexRules);
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
            if (assigString == null || assigString.isEmpty()) {
                return null;
            }
            int pos = assigString.indexOf('=');
            int length = assigString.length();
            // if '=' is at the first or last position, then we have an error.
            if (pos > 0 || pos < length - 1) {
                Expression expression = parseExpression(
                        assigString.substring(pos + 1, length));
                if (expression != null && !expression.hasError()) {
                    return new Assignment(assigString.substring(0, pos), expression);
                }
            }
            Assignment assignment = new Assignment(null, null);
            assignment.setError(ERROR_MSG_Invalid_assignment);
            return assignment;
        }

		        
        public Expression parseExpression(String line) {
            if (line == null || line.isEmpty()) {
                return null;
            }
			Expression result = null;
			boolean isError = false;
            initTokenizer();
			List<Token> tokens = tokenizer.tokenize(line);
			if(tokens == null || tokens.size() == 0) {
				isError = true;
			} else {
				for (Token token : tokens) {
					String tokenText = token.getText();
					int ruleIdx = token.getGroupIndex();
					if(tokenText == null || tokenText.isEmpty() 
								|| ruleIdx >= expressionRules.length) {
						isError = true;
						break;
					}
					Class expType = expressionRules[ruleIdx];
					if (expType.isInstance(Operation.class)) {
						if(result == null) {
							isError = true;
							break;
						}
						result = new Operation(tokenText, new Expression[] { result, null });
					} else {
						Expression tmp = null;
						if (expType.isInstance(IntNumber.class)) {
							tmp = parseIntNumber(tokenText);
						}
						else if (expType.isInstance(Variable.class)) {
							tmp = new Variable(tokenText);
						}
						else {
							isError = true;
							break;
						}
						if(result == null) {
							result = tmp;
						}
						else if(result instanceof Operation) {
							Expression[] args = ((Operation) result).getArgs();
							if(args == null || args.length < 2 || args[1] != null) {
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
				if(result instanceof Operation) {
					Expression[] args = ((Operation) result).getArgs();
					if(args == null || args.length < 2 || args[1] == null) {
						isError = true;
					}
				}
			}
			if(isError || result == null) {
				result = new ErrorExpression("12" + ERROR_MSG_Invalid_expression);
			}
            System.out.println("This is expression: " + result.toString());
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
		
		
		protected static class Token {
			private String text;
			private int groupIndex;
			
			public Token(String text, int groupIndex) {
				this.text = text;
				this.groupIndex = groupIndex;
			}
			
			public String getText() { return text; }
			public int getGroupIndex() { return groupIndex; }
		}
	
		protected static class Tokenaizer {
			private Pattern pattern;
			
			public Tokenaizer(String[] tokenPatterns) {
				if(tokenPatterns != null && tokenPatterns.length > 0) {
					String stringPattern = "(\\s+)(" + String.join(")|(", tokenPatterns) + ")";
					Pattern pattern = Pattern.compile(stringPattern);
				}
			}
			
			/**
			* Returns null if there is an error character in the line.
			* Returns Token list otherwise.
			**/
			public List<Token> tokenize(String line) {
				if(line != null && !line.isEmpty() && pattern != null) {
					List<Token> result = new ArrayList<>();
					Matcher matcher = pattern.matcher(line);
					int end = 0;
					while (matcher.find(end)) {
						// group(1) - are the space characters
						if (matcher.group(1) == null) {
                            String text = matcher.group(0);
							if (text == null || matcher.end() - end > text.length()) {
								return null; // there are unwaiting characters
							}
							int groupIndex = groupIndex(matcher);
							result.add(new Token(matcher.group(0), groupIndex));
						}
						end = matcher.end();
					}
					return result;
				}
				return null;
			}
					
			private int groupIndex(Matcher matcher) {
				int count = matcher.groupCount();
				for (int i = 1; i <= count; ++i) {
					if (matcher.group(i) != null) {
						return i - 2; // 0 - full group, 1 - spaces
					}
				}
				return -1;
			}
		}
	}
	
    private static class Command extends AbstractClause {
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

	
    private static class Assignment extends AbstractClause {
        private final String variableName;
        private final Expression expression;

        public Assignment(String variableName, Expression expression) {
            this.variableName = variableName;
            this.expression = expression;
        }

        public String getVariableName() {
            return variableName;
        }

        public Expression getExpression() {
            return expression;
        }
        
        public boolean process(Processor processor, IStringConsumer consumer) {
            return processor.process((Assignment) this, consumer);
        }
    }


    private static abstract class Expression extends AbstractClause {
		private Expression root = null;
        
        public boolean process(Processor processor, IStringConsumer consumer) {
            return processor.process((Expression) this, consumer);
        }
    }
    
	
	private static class ErrorExpression extends Expression {
        public ErrorExpression(String error) {
            setError(error);
        }
	}
	
	
	private static class Variable extends Expression {
		private String name;
		
		public Variable(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
    
	
    private static class IntNumber extends Expression {
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

    
    private static class Operation extends Expression {
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
		
		@Override
		public boolean hasError() {
			if(super.hasError() || operation == null || arguments == null) {
				return true;
			}
			for(Expression exp : arguments) {
				if(exp == null || exp.hasError()) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public String getError() {
			if(super.hasError()) {
				return super.getError();
			}
			if(hasError()) {
				return ERROR_MSG_Invalid_expression;
			}
			return "";
		}
		
        @Override
        public String toString() {
            if(hasError()) { return getError(); }
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            sb.append(operation);
            sb.append(" ");
            boolean first = true;
            for(Expression arg : arguments) {
                if(arg.hasError()) { return arg.getError(); }
                if(first) {
                    first = false;
                    
                }
                else { 
                    sb.append(" ");
                }
                sb.append(arg.toString());
            }
            sb.append(")");
            return sb.toString();
        }
    }

    
    private static class REPLContext {
        private final Map<String, IntNumber> intVars = new HashMap<>();

        public Map <String, IntNumber> getIntVars() {
            return intVars;
        }

        public void addIntVariable(String name, IntNumber value) {
            if (name != null) {
                intVars.put(name, value);
            }
        }

        public boolean hasIntVariable(String name) {
            if (name == null) {
                return false;
            }
            return intVars.containsKey(name);
        }
    }

    
    public interface IStringConsumer {
        void apply(String message);
    }
}
