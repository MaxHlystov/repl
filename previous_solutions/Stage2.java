/* Description
You need to write a program that reads two numbers in a loop
and prints the sum in the standard output. If a user enters
only a single number, the program should print the same number.
If a user enters an empty line, the program should ignore it.

When the command /exit is entered, the program must print "Bye!"
or something similar, and then, stop.
The example of work
The example below shows inputs and the corresponding outputs.
Your program should work in the same way.

7 9
16
-2 5
3

7
7
/exit
Bye!
*/

import java.util.Scanner;

class Stage2 {
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
			int a = 0;
			int b = 0;
			try {
				a = Integer.parseInt(s[0]);
			}
			catch(NumberFormatException ex) {
				continue;
			};
			if(len > 1)
				try {
					b = Integer.parseInt(s[1]);
				}
				catch(NumberFormatException ex) {
					b = 0;
				};
			System.out.println(a + b);
		}
		System.out.println("Bye!");
	}
}