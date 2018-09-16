/* Stage #5: Invalid input handling
Description
Modify the program to handle different cases when the given expression has an invalid format. The program should output a readable message to the user. The program must never throw the NumberFormatException or another exception.

If a user inputs an invalid command, the program must print another message.

Do not forget to write methods to decompose your program.

The example of work
8 + 7 - 4
11
abc
Invalid expression
123+
Invalid expression
+15
15
18 22
Invalid expression

-22
-22
/go
Unknown command
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
            if(s[0].charAt(0) == '/') {
                if(s[0].equals("/exit"))
                    break;
                if(s[0].equals("/help")) {
                    System.out.println("The program calculates the sum of numbers");
                    continue;
                }
                System.out.println("Unknown command");
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