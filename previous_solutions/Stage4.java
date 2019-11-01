/* Stage #4: Addition and subtraction

Description

At this stage, you should modify the program to support
the addition + and subtraction - operators.

The program must calculate the expressions like these:
4 + 6 - 8, 2 - 3 - 4 and so on.
It must support both unary and binary minuses. 
If the user has entered several operators following each other, 
the program still should work (like Java or Python REPL).
The two adjacent minus signs should be interpreted as a plus.

Modify the result of the /help command to explain these operations.

Decompose your program using methods to simplify its understanding
and further development.
The example of work

8
8

-2 + 4 - 5 + 6
3
9 +++ 10 -- 8
27
3 --- 5
-2
14       -   12
2

The program should not stop until the /exit command is entered.
*/

import java.util.Scanner;

class Stage4 {
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
			if(s[0].equals("/exit"))
				break;
			if(s[0].equals("/help")) {
				System.out.println("The program calculates the sum of numbers");
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