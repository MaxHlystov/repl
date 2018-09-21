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

import java.util.Scanner;

class Stage5 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while(true) {
			String line = sc.nextLine();
			if(line.isEmpty())
				continue;
			String[] s = line.split("\\s+");
			int len = s.length;
			if(len == 0 || s[0].isEmpty())
				continue;
            if(s[0].charAt(0) == '/') {
                if(s[0].equals("/exit"))
                    break;
                if(s[0].equals("/help")) {
                    System.out.println("The program calculates the sum of numbers");
                    
                }
                else 
					System.out.println("Unknown command");
				continue;
            }
			int result = 0;
			boolean error = false;
			int sign = 1;
			boolean nextSign = false;
			for(int i=0; i<len; ++i) {
				if(nextSign) {
					nextSign = false;
					if(s[i].equals("+"))
						sign = 1;
					else if(s[i].equals("-"))
						sign = -1;
					else {
						error = true;
                        System.out.println("Invalid expression");
						break;
					}
				}
				else {
					try {
						result += sign * Integer.parseInt(s[i]);
						nextSign = true;
					}
					catch(NumberFormatException ex) {
						error = true;
                        System.out.println("Invalid expression");
						break;
					}
				}
			}
			if(!error && nextSign)
				System.out.println(result);
		}
		System.out.println("Bye!");
	}
}